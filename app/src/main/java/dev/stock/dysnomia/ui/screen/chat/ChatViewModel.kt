package dev.stock.dysnomia.ui.screen.chat

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.stock.dysnomia.data.MessageBody
import dev.stock.dysnomia.data.MessageEntity
import dev.stock.dysnomia.data.NetworkRepository
import dev.stock.dysnomia.data.OfflineRepository
import dev.stock.dysnomia.utils.MESSAGE_POLLING_TIME
import dev.stock.dysnomia.utils.TIMEOUT_MILLIS
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
import javax.inject.Inject

data class ChatUiState(
    val messageText: TextFieldValue = TextFieldValue()
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val networkRepository: NetworkRepository,
    private val offlineRepository: OfflineRepository
) : ViewModel() {
    private val _chatUiState = MutableStateFlow(ChatUiState())
    val chatUiState = _chatUiState.asStateFlow()

    val chatHistory: Flow<List<MessageEntity>> =
        offlineRepository.getAllHistory().stateIn(
            scope = viewModelScope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS)
        )

    val messageCollectionJob = Job()

    init {
        viewModelScope.launch(messageCollectionJob) {
            while (true) {
                networkRepository.getMessages().asReversed().forEach {
                    offlineRepository.addToHistory(it)
                }
                delay(MESSAGE_POLLING_TIME)
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
                _chatUiState.update {
                    it.copy(
                        messageText = TextFieldValue()
                    )
                }
            }
        }
    }

    fun changeChatText(messageText: TextFieldValue) {
        _chatUiState.update {
            it.copy(
                messageText = messageText
            )
        }
    }
}
