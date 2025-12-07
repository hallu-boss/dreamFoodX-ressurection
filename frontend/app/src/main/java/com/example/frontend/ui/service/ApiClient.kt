package com.example.frontend.ui.service

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
//    private const val BASE_URL = "https://7e510bc1e283.ngrok-free.app/api/"
    // IP danego komputera w scieci lokalnej  Należy zmienic też w network_security_config

    // 10.0.2.2 to adres hosta komputera gdzie działa emulator
    private const val BASE_URL = "https://contrate-liza-superingenious.ngrok-free.dev/api/" //"http://10.0.2.2:5000/api/"

    // Przy lgowaniu się z innej sieci używać z ngroka (przy logowaniu przez googla lub faceboka jest to konieczne)
    // ngrok http 5000
    // Za każdym razem da inne adresy , przykładowy:
    // private const val BASE_URL = "https://9ab68e83afdf.ngrok-free.app/api/"
    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    val api: ApiService by lazy { // zapytanie bez autoryzacji
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    private fun getAuthClient(token: String) = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(request)
        }
        .build()

    fun getApi(token: String): ApiService { // zapytanie z autoryzacją
        val client = getAuthClient(token)
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

}
