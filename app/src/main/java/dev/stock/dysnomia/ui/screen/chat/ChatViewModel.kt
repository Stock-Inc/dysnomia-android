package dev.stock.dysnomia.ui.screen.chat

import android.annotation.SuppressLint
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
import dev.stock.dysnomia.model.MessageBody
import dev.stock.dysnomia.model.MessageEntity
import dev.stock.dysnomia.utils.RECONNECTION_TIME
import dev.stock.dysnomia.utils.SHARING_TIMEOUT_MILLIS
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException
import timber.log.Timber
import ua.naiksoftware.stomp.dto.LifecycleEvent
import java.lang.IllegalStateException
import javax.inject.Inject

enum class ConnectionState {
    Success, Loading
}

data class ChatUiState(
    val connectionState: ConnectionState = ConnectionState.Success,
    val isMessagePending: Boolean = false
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val networkRepository: NetworkRepository,
    private val offlineRepository: OfflineRepository
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

    private var reconnectionJob: Job? = null

    init {
        observeWebsocketLifecycle()
        networkRepository.connect()
    }

    @SuppressLint("CheckResult")
    private fun observeWebsocketLifecycle() {
        networkRepository.observeLifecycle().subscribe(
            { lifecycleEvent ->
                when (lifecycleEvent.type!!) {
                    LifecycleEvent.Type.OPENED -> {
                        reconnectionJob?.cancel()
                        Timber.d("Connection opened")

                        subscribeToTopics()
                        networkRepository.requestHistory().subscribe()

//                        changeConnectionState(ConnectionState.Success)
                    }

                    LifecycleEvent.Type.CLOSED -> {
                        reconnectionJob?.cancel()
                        Timber.d("Connection closed, reconnecting in $RECONNECTION_TIME ms")

                        reconnectionJob = viewModelScope.launch {
                            delay(RECONNECTION_TIME)
                            // TODO: Exponential backoff
                            // changeConnectionState(ConnectionState.Loading)
                            networkRepository.connect()
                        }
                    }

                    LifecycleEvent.Type.ERROR -> {
                        Timber.e("Connection error")
                    }

                    LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> {
                        Timber.d("Failed server heartbeat")
                    }
                }
            },
            { e ->
                websocketErrorHandler(e)
            }
        )
    }

    @SuppressLint("CheckResult")
    private fun subscribeToTopics() {
        networkRepository.observeMessages().subscribe(
            { message ->
                viewModelScope.launch {
                    offlineRepository.addToHistory(message)
                }
            },
            { e ->
                websocketErrorHandler(e)
            }
        )

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
    }

    fun sendMessage(
        currentName: String,
        message: String
    ) { // TODO: take messageText out of viewmodel
        if (message.isNotEmpty() && message != "/") {
            viewModelScope.launch {
                try {
                    _chatUiState.update {
                        it.copy(
                            isMessagePending = true
                        )
                    }
                    if (message.startsWith('/')) {
                        offlineRepository.addToHistory(
                            MessageEntity(
                                name = message.drop(1),
                                message = networkRepository.sendCommand(
                                    message.drop(1)
                                ),
                                isCommand = true
                            )
                        )
                        clearPendingState()
                    } else {
                        networkRepository.sendMessage(
                            MessageBody(
                                name = currentName,
                                message = message
                            )
                        ).subscribe(
                            {
                                clearPendingState()
                            },
                            { e ->
                                websocketErrorHandler(e)
                                clearPendingState()
                            }
                        )
                    }
                } catch (e: IOException) {
                    apiErrorHandler(e)
                } catch (e: HttpException) {
                    apiErrorHandler(e)
                }
            }
        }
    }

    fun changeChatText(messageText: TextFieldValue) {
        this.messageText = messageText
    }

//    private fun changeConnectionState(connectionState: ConnectionState) {
//        if (_chatUiState.value.connectionState != connectionState) {
//            _chatUiState.update {
//                it.copy(
//                    connectionState = connectionState
//                )
//            }
//        }
//    }

    private fun clearPendingState() {
        if (_chatUiState.value.isMessagePending) {
            _chatUiState.update {
                it.copy(
                    isMessagePending = false
                )
            }
            messageText = TextFieldValue()
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
        when (e) {
            is HttpException, is IOException -> {
                FirebaseCrashlytics.getInstance().recordException(e)
                Timber.e(e)
                offlineRepository.addToHistory(
                    MessageEntity(
                        message = "Error connecting to the server:\n$e",
                        isCommand = true
                    )
                )
                clearPendingState()
            }
            else -> {
                throw e
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        networkRepository.closeConnection()
    }
}
