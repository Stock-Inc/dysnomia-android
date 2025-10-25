package dev.stock.dysnomia.network

//import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
//import dev.stock.dysnomia.utils.API_BASE_URL
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.runBlocking
//import kotlinx.serialization.json.Json
//import okhttp3.MediaType.Companion.toMediaType
//import okhttp3.OkHttpClient
//import org.junit.Before
//import org.junit.Test
//import retrofit2.Retrofit
//import retrofit2.converter.scalars.ScalarsConverterFactory
//import kotlin.collections.iterator
//
//class ApiTest {
//    lateinit var dysnomiaApiService: DysnomiaApiService
//
//    @Before
//    fun setup() {
//        val json = Json {
//            ignoreUnknownKeys = true
//            encodeDefaults = true
//            coerceInputValues = true
//        }
//
//        val okHttpClient = OkHttpClient.Builder()
//            .addInterceptor(TestAuthInterceptor(""))
//            .build()
//
//        val retrofit = Retrofit.Builder()
//            .client(okHttpClient)
//            .baseUrl(API_BASE_URL)
//            .addConverterFactory(ScalarsConverterFactory.create())
//            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
//            .build()
//
//        dysnomiaApiService = retrofit.create(DysnomiaApiService::class.java)
//    }
//
//    @Test
//    fun getWheelDistribution() {
//        val delayMs = 300L
//        val repeats = 200
//        val options = ""
//        val command = "wheel $options"
//
//        val occurrencesMap = mutableMapOf<String, Int>()
//
//        runBlocking {
//            repeat(repeats) {
//                occurrencesMap.merge(
//                    dysnomiaApiService.sendCommand(command),
//                    1,
//                    Int::plus
//                )
//                println("${(it / repeats.toFloat() * 100).toInt()}% завершено")
//                delay(delayMs)
//            }
//        }
//        for (entry in occurrencesMap) {
//            println("${entry.key}: ${entry.value}")
//        }
//    }
//}
