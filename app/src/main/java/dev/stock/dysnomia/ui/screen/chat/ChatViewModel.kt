package dev.stock.dysnomia.ui.screen.chat

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
import dev.stock.dysnomia.utils.MESSAGE_POLLING_TIME
import dev.stock.dysnomia.utils.RECONNECTION_TIME
import dev.stock.dysnomia.utils.TIMEOUT_MILLIS
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

sealed interface ChatUiState {
    data class Success(val afterReconnecting: Boolean = false) : ChatUiState
    data class Loading(val afterReconnecting: Boolean = false) : ChatUiState
    data object Error : ChatUiState
}

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val networkRepository: NetworkRepository,
    private val offlineRepository: OfflineRepository
) : ViewModel() {
    var chatUiState: ChatUiState by mutableStateOf(ChatUiState.Loading())
        private set

    var messageText: TextFieldValue by mutableStateOf(TextFieldValue())
        private set

    var isMessagePending: Boolean by mutableStateOf(false)
        private set

    val chatHistory: Flow<List<MessageEntity>> =
        offlineRepository.getAllHistory().stateIn(
            scope = viewModelScope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS)
        )

    private var messageCollectionJob: Job? = null

    init {
        connect()
    }

    fun connect(afterReconnecting: Boolean = false) {
        var afterReconnectingLocal = afterReconnecting
        messageCollectionJob?.cancel()

        messageCollectionJob = viewModelScope.launch {
            while (true) {
                try {
                    chatUiState = ChatUiState.Loading(afterReconnectingLocal)
                    networkRepository.getMessages().asReversed().forEach {
                        offlineRepository.addToHistory(it)
                    }
                    if (chatUiState !is ChatUiState.Success) {
                        chatUiState = ChatUiState.Success(afterReconnectingLocal)
                    }
                    afterReconnectingLocal = false
                    delay(MESSAGE_POLLING_TIME)
                } catch (e: HttpException) {
                    chatUiState = ChatUiState.Error
                    Timber.e(e.toString())
                    delay(RECONNECTION_TIME)
                } catch (e: IOException) {
                    chatUiState = ChatUiState.Error
                    Timber.e(e.toString())
                    delay(RECONNECTION_TIME)
                }
            }
        }
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
                        )
                        isMessagePending = false
                    }
                } catch (e: IOException) {
                    offlineRepository.addToHistory(
                        MessageEntity(
                            message = "Error connecting to the server:\n$e",
                            isCommand = true
                        )
                    )
                } catch (e: HttpException) {
                    offlineRepository.addToHistory(
                        MessageEntity(
                            message = "Error connecting to the server:\n$e",
                            isCommand = true
                        )
                    )
                }
                messageText = TextFieldValue()
            }
        }
    }

    fun changeChatText(messageText: TextFieldValue) {
        this.messageText = messageText
    }
}
