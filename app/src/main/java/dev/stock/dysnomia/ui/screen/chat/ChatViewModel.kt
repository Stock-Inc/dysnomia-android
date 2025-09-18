package dev.stock.dysnomia.ui.screen.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.stock.dysnomia.data.NetworkRepository
import dev.stock.dysnomia.data.OfflineRepository
import dev.stock.dysnomia.data.PreferencesRepository
import dev.stock.dysnomia.model.CommandSuggestion
import dev.stock.dysnomia.model.DeliveryStatus
import dev.stock.dysnomia.model.MessageBody
import dev.stock.dysnomia.model.MessageEntity
import dev.stock.dysnomia.model.RepliedMessage
import dev.stock.dysnomia.model.toRepliedMessage
import dev.stock.dysnomia.utils.ANONYMOUS
import dev.stock.dysnomia.utils.SHARING_TIMEOUT_MILLIS
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException
import timber.log.Timber
import ua.naiksoftware.stomp.dto.LifecycleEvent
import javax.inject.Inject
import kotlin.math.pow

enum class ConnectionState {
    Success, Connecting
}

data class ChatUiState(
    val connectionState: ConnectionState = ConnectionState.Success,
    val isCommandPending: Boolean = false,
    val repliedMessage: RepliedMessage? = null,
    val commandSuggestionList: List<CommandSuggestion> = emptyList()
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val networkRepository: NetworkRepository,
    private val offlineRepository: OfflineRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    private val _chatUiState = MutableStateFlow(ChatUiState())
    val chatUiState = _chatUiState.asStateFlow()

    var messageText: TextFieldValue by mutableStateOf(TextFieldValue())
        private set

    val chatHistory: Flow<List<MessageEntity>> =
        offlineRepository.getAllHistory().stateIn(
            scope = viewModelScope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed(SHARING_TIMEOUT_MILLIS)
        )

    private val currentName: Flow<String> = preferencesRepository.name

    private var reconnectionJob: Job? = null
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    init {
        getCommandSuggestions()
        observeWebsocketLifecycle()
        networkRepository.connect()
    }

    private fun reconnectWithBackoff(
        maxDelay: Double = 8000.0,
        maxRetries: Int = Int.MAX_VALUE
    ) {
        reconnectionJob?.cancel()
        reconnectionJob = viewModelScope.launch {
            repeat(maxRetries) { attempt ->
                val delayMillis = (2.0.pow(attempt) * 1000L).coerceAtMost(maxDelay).toLong()
                delay(delayMillis)
                Timber.w("Reconnect attempt $attempt after $delayMillis ms")
                if (attempt > 2) setConnectionState(ConnectionState.Connecting)
                networkRepository.connect()
            }
        }
    }

    private fun observeWebsocketLifecycle() {
        compositeDisposable.add(
            networkRepository.observeLifecycle().subscribe(
                { lifecycleEvent ->
                    when (lifecycleEvent.type!!) {
                        LifecycleEvent.Type.OPENED -> {
                            reconnectionJob?.cancel()
                            Timber.d("Connection opened")
                            setConnectionState(ConnectionState.Success)

                            subscribeToTopics()
                            compositeDisposable.add(
                                networkRepository.requestHistory()
                                    .subscribe(
                                        {},
                                        { e ->
                                            websocketErrorHandler(e)
                                        }
                                    )
                            )
                        }

                        LifecycleEvent.Type.CLOSED -> {
                            if (reconnectionJob?.isActive != true) {
                                reconnectWithBackoff()
                            }
                        }

                        LifecycleEvent.Type.ERROR -> {
                            Timber.e("Connection error")
                        }

                        LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> {
                            Timber.w("Failed server heartbeat")
                        }
                    }
                },
                { e ->
                    websocketErrorHandler(e)
                }
            )
        )
    }

    private fun subscribeToTopics() {
        compositeDisposable.addAll(
            networkRepository.observeMessages().subscribe(
                { message ->
                    viewModelScope.launch {
                        if (message.name == preferencesRepository.name.first()) {
                            offlineRepository.setDelivered(message)
                        } else {
                            offlineRepository.addToHistory(message)
                        }
                    }
                },
                { e ->
                    websocketErrorHandler(e)
                }
            ),
            networkRepository.observeHistory().subscribe(
                { messagesList ->
                    viewModelScope.launch {
                        messagesList.asReversed().forEach { message ->
                            offlineRepository.addToHistory(message)
                        }
                    }
                },
                { e ->
                    websocketErrorHandler(e)
                }
            )
        )
    }

    fun sendMessage() {
        val message = messageText.text.trim()
        if (message.isNotEmpty()) {
            viewModelScope.launch {
                val name = currentName.first()
                val repliedMessage = _chatUiState.value.repliedMessage

                offlineRepository.addToHistory(
                    MessageEntity(
                        name = name,
                        message = message,
                        deliveryStatus = DeliveryStatus.PENDING,
                        replyId = repliedMessage?.id ?: 0
                    )
                )
                clearPendingState()
                cancelReply()
                compositeDisposable.add(
                    networkRepository.sendMessage(
                        MessageBody(
                            name = name,
                            message = message,
                            replyId = repliedMessage?.id ?: 0
                        )
                    ).subscribe(
                        {},
                        { e ->
                            websocketErrorHandler(e)
                        }
                    )
                )
            }
        }
    }

    fun sendCommand(command: String?) {
        val trimmedCommand = command?.trim() ?: messageText.text.trim()

        if (trimmedCommand.startsWith('/')) {
            viewModelScope.launch {
                try {
                    _chatUiState.update {
                        it.copy(
                            isCommandPending = true
                        )
                    }
                    offlineRepository.addToHistory(
                        MessageEntity(
                            name = trimmedCommand.drop(1),
                            message = networkRepository.sendCommand(
                                trimmedCommand.drop(1)
                            ),
                            isCommand = true
                        )
                    )
                } catch (e: IOException) {
                    apiErrorHandler(e)
                } catch (e: HttpException) {
                    apiErrorHandler(e)
                } finally {
                    clearPendingState()
                }
            }
        }
    }

    fun getMessageStateFlowByMessageId(messageId: Int): MutableStateFlow<RepliedMessage> =
        MutableStateFlow(
            RepliedMessage(
                id = messageId,
                name = ANONYMOUS,
                message = "Loading reply...",
            )
        ).also { flow ->
            viewModelScope.launch {
                val offlineMessage = offlineRepository.getMessageByMessageId(messageId)
                if (offlineMessage != null) {
                    flow.value = offlineMessage.toRepliedMessage()
                }

                try {
                    val networkMessage = networkRepository.getMessageByMessageId(messageId)
                    flow.value = networkMessage.toRepliedMessage()
                } catch (e: IOException) {
                    Timber.e(e)
                    flow.value = RepliedMessage(
                        id = messageId,
                        name = "Error",
                        message = "Unable to load reply"
                    )
                } catch (e: HttpException) {
                    Timber.e(e)
                    flow.value = RepliedMessage(
                        id = messageId,
                        name = "Error",
                        message = "Unable to load reply"
                    )
                }
            }
        }

    private fun getCommandSuggestions() {
        viewModelScope.launch {
            try {
                val commandSuggestions = networkRepository.getCommandSuggestions()
                _chatUiState.update {
                    it.copy(
                        commandSuggestionList = commandSuggestions
                    )
                }
            } catch (e: IOException) {
                Timber.e(e)
            } catch (e: HttpException) {
                Timber.e(e)
            }
        }
    }

    fun replyTo(messageEntity: MessageEntity) {
        _chatUiState.update {
            it.copy(
                repliedMessage = RepliedMessage(
                    id = messageEntity.messageId!!,
                    name = messageEntity.name.ifEmpty { ANONYMOUS },
                    message = messageEntity.message
                )
            )
        }
    }

    fun cancelReply() {
        _chatUiState.update {
            it.copy(
                repliedMessage = null
            )
        }
    }

    fun changeChatText(messageText: TextFieldValue) {
        this.messageText = messageText
    }

    private fun clearPendingState() {
        _chatUiState.update {
            it.copy(
                isCommandPending = false
            )
        }
        messageText = TextFieldValue()
    }

    private fun setConnectionState(connectionState: ConnectionState) {
        _chatUiState.update {
            it.copy(
                connectionState = connectionState
            )
        }
    }

    private fun websocketErrorHandler(e: Throwable) {
        if (e is IllegalStateException) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Timber.e(e)
        } else {
            throw e
        }
    }

    private suspend fun apiErrorHandler(e: Throwable) {
        FirebaseCrashlytics.getInstance().recordException(e)
        Timber.e(e)
        offlineRepository.addToHistory(
            MessageEntity(
                message = "Error connecting to the server:\n$e",
                isCommand = true
            )
        )
    }

    override fun onCleared() {
        super.onCleared()
        networkRepository.closeConnection()
        compositeDisposable.dispose()
    }
}
