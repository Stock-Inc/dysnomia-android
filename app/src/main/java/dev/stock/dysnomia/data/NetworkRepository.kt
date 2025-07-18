package dev.stock.dysnomia.data

import dev.stock.dysnomia.model.AuthResponse
import dev.stock.dysnomia.model.MessageBody
import dev.stock.dysnomia.model.MessageEntity
import dev.stock.dysnomia.model.SignInBody
import dev.stock.dysnomia.model.SignUpBody
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
    suspend fun signIn(signInBody: SignInBody): AuthResponse
    suspend fun signUp(signUpBody: SignUpBody): AuthResponse
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
    override suspend fun sendCommand(command: String): String =
        dysnomiaApiService.sendCommand(command)

    override fun observeLifecycle(): Flowable<LifecycleEvent> =
        dysnomiaStompClient.lifecycle()
            .subscribeOn(Schedulers.io(), false)
            .observeOn(AndroidSchedulers.mainThread())

    override fun observeHistory(): Flowable<List<MessageEntity>> {
        return dysnomiaStompClient.topic(HISTORY_TOPIC)
            .subscribeOn(Schedulers.io(), false)
            .observeOn(Schedulers.computation())
            .map { listMessageJson ->
                Json.decodeFromString<List<MessageEntity>>(listMessageJson.payload)
            }
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun observeMessages(): Flowable<MessageEntity> =
        dysnomiaStompClient.topic(MESSAGE_TOPIC)
            .subscribeOn(Schedulers.io(), false)
            .observeOn(Schedulers.computation())
            .map { messageJson ->
                Json.decodeFromString<MessageEntity>(messageJson.payload)
            }
            .observeOn(AndroidSchedulers.mainThread())

    override fun sendMessage(messageBody: MessageBody): Completable =
        dysnomiaStompClient.send(
            CHAT_APP,
            Json.encodeToString(messageBody)
        )

    override fun requestHistory(): Completable =
        dysnomiaStompClient.send(HISTORY_APP, null)

    override suspend fun signIn(signInBody: SignInBody): AuthResponse =
        dysnomiaApiService.signIn(signInBody)

    override suspend fun signUp(signUpBody: SignUpBody): AuthResponse =
        dysnomiaApiService.signUp(signUpBody)

    override fun connect() {
        dysnomiaStompClient.connect()
    }

    override fun closeConnection() {
        dysnomiaStompClient.disconnect()
    }
}
