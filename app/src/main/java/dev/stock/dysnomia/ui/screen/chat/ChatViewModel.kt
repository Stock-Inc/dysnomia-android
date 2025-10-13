package dev.stock.dysnomia.ui.screen.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.stock.dysnomia.data.ConnectionState
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import okio.IOException
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.pow

data class ChatUiState(
    val connectionState: ConnectionState = ConnectionState.Connecting,
    val isCommandPending: Boolean = false,
    val repliedMessage: RepliedMessage? = null
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val networkRepository: NetworkRepository,
    private val offlineRepository: OfflineRepository,
    preferencesRepository: PreferencesRepository
) : ViewModel() {
    private val _chatUiState = MutableStateFlow(ChatUiState())
    val chatUiState = _chatUiState.asStateFlow()

    var messageText: TextFieldValue by mutableStateOf(TextFieldValue())
        private set

    val chatHistory: Flow<List<MessageEntity>> = offlineRepository.getAllHistory()
        .stateIn(
            scope = viewModelScope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed(SHARING_TIMEOUT_MILLIS)
        )

    private val username = preferencesRepository.name.stateIn(
        scope = viewModelScope,
        initialValue = "",
        started = SharingStarted.Eagerly
    )

    val commandSuggestions: MutableStateFlow<List<CommandSuggestion>> =
        MutableStateFlow(emptyList())

    init {
        observeConnectionState()
        observeIncomingMessages()
        observeIncomingHistory()
        getCommandSuggestions()
    }

    private fun observeConnectionState(
        maxDelay: Double = 8000.0
    ) {
        var attempt = 1
        networkRepository.connectionState
            .onEach { state ->
                when (state) {
                    ConnectionState.Disconnected -> {
                        Timber.d("Connecting to the server...")
                        networkRepository.connect()
                    }

                    ConnectionState.Connected -> {
                        attempt = 1
                        setConnectionState(ConnectionState.Connected)
                        Timber.d("Connected. Requesting history...")
                        networkRepository.requestHistory()
                    }

                    is ConnectionState.Error -> {
                        setConnectionState(ConnectionState.Connecting)
                        val delayMillis = (2.0.pow(attempt) * 1000L).coerceAtMost(maxDelay).toLong()
                        delay(delayMillis)
                        Timber.d("Reconnect attempt $attempt after $delayMillis ms")
                        attempt++
                        networkRepository.connect()
                    }

                    else -> Unit
                }
            }
            .launchIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeIncomingMessages() {
        networkRepository.connectionState
            .filterIsInstance<ConnectionState.Connected>()
            .flatMapLatest {
                networkRepository.messagesFlow
            }
            .onEach { message ->
                Timber.d("Received message")
                if (message.name == username.value && username.value.isNotEmpty()) {
                    offlineRepository.setDelivered(message)
                } else {
                    offlineRepository.addToHistory(message)
                }
            }
            .launchIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeIncomingHistory() {
        networkRepository.connectionState
            .filterIsInstance<ConnectionState.Connected>()
            .flatMapLatest {
                networkRepository.historyFlow
            }
            .onEach { messages ->
                Timber.d("Received history")
                messages.forEach { message ->
                    if (message.name == username.value && username.value.isNotEmpty()) {
                        offlineRepository.setDelivered(message)
                    } else {
                        offlineRepository.addToHistory(message)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun sendMessage() {
        val message = messageText.text.trim()
        if (message.isNotEmpty()) {
            viewModelScope.launch {
                val repliedMessage = _chatUiState.value.repliedMessage
                val messageToBeSent =
                    MessageEntity(
                        name = username.value,
                        message = message,
                        deliveryStatus = DeliveryStatus.PENDING,
                        replyId = repliedMessage?.id ?: 0
                    )

                offlineRepository.addToHistory(messageToBeSent)
                clearPendingState()
                cancelReply()
                Timber.d("Sending message: %s", messageToBeSent.toString())
                networkRepository.sendMessage(
                    MessageBody(
                        name = username.value,
                        message = message,
                        replyId = repliedMessage?.id ?: 0
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
                    return@launch
                }

                try {
                    val networkMessage = networkRepository.getMessageByMessageId(messageId)
                    flow.value = networkMessage.toRepliedMessage()
                    offlineRepository.addToHistory(networkMessage)
                } catch (e: IOException) {
                    Timber.d(e)
                    flow.value = RepliedMessage(
                        id = messageId,
                        name = "Error",
                        message = "Unable to load reply"
                    )
                } catch (e: HttpException) {
                    Timber.d(e)
                    flow.value = RepliedMessage(
                        id = messageId,
                        name = "Error",
                        message = "Unable to load reply"
                    )
                } catch (e: SerializationException) {
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
                commandSuggestions.value = networkRepository.getCommandSuggestions()
            } catch (e: IOException) {
                Timber.d(e)
            } catch (e: HttpException) {
                Timber.d(e)
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
        if (_chatUiState.value.connectionState == connectionState) return
        _chatUiState.update {
            it.copy(
                connectionState = connectionState
            )
        }
    }

    private suspend fun apiErrorHandler(e: Throwable) {
        Timber.d(e)
        offlineRepository.addToHistory(
            MessageEntity(
                message = "Error connecting to the server:\n$e",
                isCommand = true
            )
        )
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            networkRepository.disconnect()
        }
    }
}
