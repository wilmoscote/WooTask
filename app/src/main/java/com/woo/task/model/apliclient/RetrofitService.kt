package com.woo.task.model.apliclient


import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitService {


    companion object{


        //IP PRODUCCIÓN
        private const val BASE_URL = "https://wootask.herokuapp.com/"
        //private const val BASE_URL = "http://54.198.185.208/microserver/api/"
        private val req = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)


        private val httpClient = okhttp3.OkHttpClient.Builder().addInterceptor(req)
        fun getClient(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build()
        }
    }
}