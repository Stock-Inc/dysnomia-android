package dev.stock.dysnomia.data

import dev.stock.dysnomia.model.CommandSuggestion
import dev.stock.dysnomia.model.MessageBody
import dev.stock.dysnomia.model.MessageEntity
import dev.stock.dysnomia.model.SignInBody
import dev.stock.dysnomia.model.SignInResponse
import dev.stock.dysnomia.model.SignUpBody
import dev.stock.dysnomia.model.SignUpResponse
import dev.stock.dysnomia.network.DysnomiaApiService
import dev.stock.dysnomia.utils.CHAT_APP
import dev.stock.dysnomia.utils.HISTORY_APP
import dev.stock.dysnomia.utils.HISTORY_TOPIC
import dev.stock.dysnomia.utils.MESSAGE_TOPIC
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.serialization.json.Json
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import javax.inject.Inject
import javax.inject.Singleton

interface Repository {
    suspend fun sendCommand(command: String): String
    suspend fun getCommandSuggestions(): List<CommandSuggestion>
    suspend fun signIn(signInBody: SignInBody): SignInResponse
    suspend fun signUp(signUpBody: SignUpBody): SignUpResponse
    suspend fun getMessageByMessageId(messageId: Int): MessageEntity
    fun observeLifecycle(): Flowable<LifecycleEvent>
    fun observeMessages(): Flowable<MessageEntity>
    fun observeHistory(): Flowable<List<MessageEntity>>
    fun requestHistory(): Completable
    fun sendMessage(messageBody: MessageBody): Completable
    fun connect()
    fun closeConnection()
}

@Singleton
class NetworkRepository @Inject constructor(
    private val dysnomiaApiService: DysnomiaApiService,
    private val dysnomiaStompClient: StompClient,
) : Repository {
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun sendCommand(command: String): String =
        dysnomiaApiService.sendCommand(command)

    override suspend fun getCommandSuggestions(): List<CommandSuggestion> =
        dysnomiaApiService.getCommandSuggestions()

    override suspend fun signIn(signInBody: SignInBody): SignInResponse =
        dysnomiaApiService.signIn(signInBody)

    override suspend fun signUp(signUpBody: SignUpBody): SignUpResponse =
        dysnomiaApiService.signUp(signUpBody)

    override suspend fun getMessageByMessageId(messageId: Int): MessageEntity =
        dysnomiaApiService.getMessageByMessageId(messageId)

    override fun observeLifecycle(): Flowable<LifecycleEvent> =
        dysnomiaStompClient.lifecycle()
            .subscribeOn(Schedulers.io(), false)
            .observeOn(AndroidSchedulers.mainThread())

    override fun observeHistory(): Flowable<List<MessageEntity>> {
        return dysnomiaStompClient.topic(HISTORY_TOPIC)
            .subscribeOn(Schedulers.io(), false)
            .observeOn(Schedulers.computation())
            .map { listMessageJson ->
                json.decodeFromString<List<MessageEntity>>(listMessageJson.payload)
            }
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun observeMessages(): Flowable<MessageEntity> =
        dysnomiaStompClient.topic(MESSAGE_TOPIC)
            .subscribeOn(Schedulers.io(), false)
            .observeOn(Schedulers.computation())
            .map { messageJson ->
                json.decodeFromString<MessageEntity>(messageJson.payload)
            }
            .observeOn(AndroidSchedulers.mainThread())

    override fun sendMessage(messageBody: MessageBody): Completable =
        dysnomiaStompClient
            .send(CHAT_APP, json.encodeToString(messageBody))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    override fun requestHistory(): Completable =
        dysnomiaStompClient
            .send(HISTORY_APP, null)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    override fun connect() {
        dysnomiaStompClient.connect()
    }

    override fun closeConnection() {
        dysnomiaStompClient.disconnect()
    }
}
