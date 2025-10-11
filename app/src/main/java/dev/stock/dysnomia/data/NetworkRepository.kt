package dev.stock.dysnomia.data

import dev.stock.dysnomia.model.AuthTokens
import dev.stock.dysnomia.model.CommandSuggestion
import dev.stock.dysnomia.model.MessageBody
import dev.stock.dysnomia.model.MessageEntity
import dev.stock.dysnomia.model.Profile
import dev.stock.dysnomia.model.SignInBody
import dev.stock.dysnomia.model.SignUpBody
import dev.stock.dysnomia.network.DysnomiaApiService
import dev.stock.dysnomia.utils.CHAT_APP
import dev.stock.dysnomia.utils.HISTORY_APP
import dev.stock.dysnomia.utils.HISTORY_TOPIC
import dev.stock.dysnomia.utils.MESSAGE_TOPIC
import dev.stock.dysnomia.utils.WEBSOCKETS_BASE_URL
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.retry
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.hildan.krossbow.stomp.ConnectionException
import org.hildan.krossbow.stomp.ConnectionTimeout
import org.hildan.krossbow.stomp.LostReceiptException
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompReceipt
import org.hildan.krossbow.stomp.conversions.kxserialization.StompSessionWithKxSerialization
import org.hildan.krossbow.stomp.conversions.kxserialization.convertAndSend
import org.hildan.krossbow.stomp.conversions.kxserialization.json.withJsonConversions
import org.hildan.krossbow.stomp.conversions.kxserialization.subscribe
import org.hildan.krossbow.stomp.sendEmptyMsg
import org.hildan.krossbow.websocket.WebSocketConnectionException
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

interface NetworkRepository {
    suspend fun connect()
    suspend fun disconnect()
    suspend fun requestHistory(): StompReceipt?
    suspend fun sendMessage(messageBody: MessageBody): StompReceipt?
    suspend fun sendCommand(command: String): String
    suspend fun getCommandSuggestions(): List<CommandSuggestion>
    suspend fun signIn(signInBody: SignInBody): AuthTokens
    suspend fun signUp(signUpBody: SignUpBody): AuthTokens
    suspend fun getMessageByMessageId(messageId: Int): MessageEntity
    suspend fun getProfile(username: String): Profile
    suspend fun getNewTokens(bearerRefreshToken: String): AuthTokens
    val connectionState: StateFlow<ConnectionState>
    val messagesFlow: Flow<MessageEntity>
    val historyFlow: Flow<List<MessageEntity>>
}

sealed class ConnectionState {
    data object Disconnected : ConnectionState()
    data object Connecting : ConnectionState()
    data object Connected : ConnectionState()
    data class Error(val error: Throwable) : ConnectionState()
}

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class NetworkRepositoryImpl @Inject constructor(
    private val dysnomiaApiService: DysnomiaApiService,
    private val dysnomiaStompClient: StompClient,
    private val json: Json
) : NetworkRepository {
    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _sessionState = MutableStateFlow<StompSessionWithKxSerialization?>(null)
    private val sessionState: StateFlow<StompSessionWithKxSerialization?> = _sessionState.asStateFlow()

    override val messagesFlow: Flow<MessageEntity> = sessionState
        .filterNotNull()
        .flatMapLatest { session ->
            session.subscribe(
                destination = MESSAGE_TOPIC,
                deserializer = MessageEntity.serializer()
            )
        }
        .retry { error ->
            Timber.d(error)
            _connectionState.value = ConnectionState.Error(error)
            true
        }

    override val historyFlow: Flow<List<MessageEntity>> = sessionState
        .filterNotNull()
        .flatMapLatest { session ->
            session.subscribe(
                destination = HISTORY_TOPIC,
                deserializer = ListSerializer(MessageEntity.serializer())
            )
        }
        .retry { error ->
            Timber.d(error)
            _connectionState.value = ConnectionState.Error(error)
            true
        }

    override suspend fun connect() {
        try {
            _connectionState.value = ConnectionState.Connecting

            disconnect()

            val newSession = dysnomiaStompClient
                .connect(WEBSOCKETS_BASE_URL)
                .withJsonConversions(json)

            _sessionState.value = newSession
            _connectionState.value = ConnectionState.Connected
        } catch (e: ConnectionTimeout) {
            Timber.d(e)
            _connectionState.value = ConnectionState.Error(e)
            _sessionState.value = null
        } catch (e: ConnectionException) {
            Timber.d(e)
            _connectionState.value = ConnectionState.Error(e)
            _sessionState.value = null
        } catch (e: WebSocketConnectionException) {
            Timber.d(e)
            _connectionState.value = ConnectionState.Error(e)
            _sessionState.value = null
        }
    }

    override suspend fun disconnect() {
        _sessionState.value?.disconnect()
        _sessionState.value = null
    }

    override suspend fun sendMessage(messageBody: MessageBody): StompReceipt? {
        val session = _sessionState.value
        if (session != null && _connectionState.value == ConnectionState.Connected) {
            try {
                return session.convertAndSend(
                    CHAT_APP,
                    messageBody,
                    MessageBody.serializer()
                )
            } catch (e: LostReceiptException) {
                Timber.e(e)
                _connectionState.value = ConnectionState.Error(e)
            }
        } else {
            Timber.e("Not connected to server. session: $session, _connectionState.value = ${_connectionState.value}")
        }
        return null
    }

    override suspend fun requestHistory(): StompReceipt? {
        val session = _sessionState.value
        if (session != null && _connectionState.value == ConnectionState.Connected) {
            try {
                return session.sendEmptyMsg(HISTORY_APP)
            } catch (e: LostReceiptException) {
                Timber.e(e)
                _connectionState.value = ConnectionState.Error(e)
            }
        } else {
            Timber.e("Not connected to server. session: $session, _connectionState.value = ${_connectionState.value}")
        }
        return null
    }

    override suspend fun sendCommand(command: String): String =
        dysnomiaApiService.sendCommand(command)

    override suspend fun getCommandSuggestions(): List<CommandSuggestion> =
        dysnomiaApiService.getCommandSuggestions()

    override suspend fun signIn(signInBody: SignInBody): AuthTokens =
        dysnomiaApiService.signIn(signInBody)

    override suspend fun signUp(signUpBody: SignUpBody): AuthTokens =
        dysnomiaApiService.signUp(signUpBody)

    override suspend fun getMessageByMessageId(messageId: Int): MessageEntity =
        dysnomiaApiService.getMessageByMessageId(messageId)

    override suspend fun getProfile(username: String): Profile =
        dysnomiaApiService.getProfile(username)

    override suspend fun getNewTokens(bearerRefreshToken: String): AuthTokens =
        dysnomiaApiService.refreshToken(bearerRefreshToken)
}
