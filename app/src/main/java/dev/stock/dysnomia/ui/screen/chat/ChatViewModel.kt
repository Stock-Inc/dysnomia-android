package dev.stock.dysnomia.ui.screen.chat

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException
import timber.log.Timber
import ua.naiksoftware.stomp.dto.LifecycleEvent
import javax.inject.Inject

sealed interface ChatUiState {
    data object Success : ChatUiState
    data object Loading : ChatUiState
    data object Error : ChatUiState
}

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val networkRepository: NetworkRepository,
    private val offlineRepository: OfflineRepository
) : ViewModel() {
    var chatUiState: ChatUiState by mutableStateOf(ChatUiState.Loading)
        private set

    var messageText: TextFieldValue by mutableStateOf(TextFieldValue())
        private set

    var isMessagePending: Boolean by mutableStateOf(false)
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
        subscribeToTopics()
        networkRepository.reconnect()
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
                        chatUiState = ChatUiState.Success
                    }

                    LifecycleEvent.Type.CLOSED -> {
                        reconnectionJob?.cancel()
                        Timber.d("Connection closed, reconnecting in $RECONNECTION_TIME ms")
                        chatUiState = ChatUiState.Loading
                        reconnectionJob = viewModelScope.launch {
                            delay(RECONNECTION_TIME)
                            networkRepository.reconnect()
                        }
                    }

                    LifecycleEvent.Type.ERROR -> {
                        Timber.e("Connection error")
                        chatUiState = ChatUiState.Error
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
                if (chatUiState !is ChatUiState.Success) {
                    chatUiState = ChatUiState.Success
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
                if (chatUiState !is ChatUiState.Success) {
                    chatUiState = ChatUiState.Success
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
    ) {
        if (message.isNotEmpty() && message != "/") {
            viewModelScope.launch {
                try {
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
                    } else {
                        isMessagePending = true
                        networkRepository.sendMessage(
                            MessageBody(
                                name = currentName,
                                message = message
                            )
                        ).subscribe(
                            {
                                isMessagePending = false
                                messageText = TextFieldValue()
                            },
                            { e ->
                                websocketErrorHandler(e)
                                isMessagePending = false
                                messageText = TextFieldValue()
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

    private fun websocketErrorHandler(e: Throwable) {
        if (e is java.lang.IllegalStateException) {
            chatUiState = ChatUiState.Error
            Timber.e(e.toString())
        } else {
            throw e
        }
    }

    private suspend fun apiErrorHandler(e: Throwable) {
        when (e) {
            is HttpException, is IOException -> {
                offlineRepository.addToHistory(
                    MessageEntity(
                        message = "Error connecting to the server:\n$e",
                        isCommand = true
                    )
                )
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
