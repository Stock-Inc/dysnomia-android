package dev.stock.dysnomia.ui.screen.chat

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.stock.dysnomia.data.MessageBody
import dev.stock.dysnomia.data.MessageEntity
import dev.stock.dysnomia.data.NetworkRepository
import dev.stock.dysnomia.data.OfflineRepository
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
        var afterReconnecting = afterReconnecting
        messageCollectionJob?.cancel()

        messageCollectionJob = viewModelScope.launch {
            while (true) {
                try {
                    chatUiState = ChatUiState.Loading(afterReconnecting)
                    networkRepository.getMessages().asReversed().forEach {
                        offlineRepository.addToHistory(it)
                    }
                    if (chatUiState !is ChatUiState.Success) {
                        chatUiState = ChatUiState.Success(afterReconnecting)
                    }
                    afterReconnecting = false
                    delay(MESSAGE_POLLING_TIME)
                } catch (e: HttpException) {
                    chatUiState = ChatUiState.Error
                    Log.e(TAG, e.toString())
                    delay(RECONNECTION_TIME)
                } catch (e: IOException) {
                    chatUiState = ChatUiState.Error
                    Log.e(TAG, e.toString())
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
                        networkRepository.sendMessage(
                            MessageBody(
                                name = currentName,
                                message = message
                            )
                        )
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

    companion object {
        const val TAG = "ChatViewModel"
    }
}
