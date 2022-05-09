package com.woo.task.model.apliclient

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitServiceLenient {
    companion object{
        //IP PRODUCCIÓN
        private const val BASE_URL = "http://54.198.185.208/microserver/api/"
        val gson = GsonBuilder().setLenient().create()
        fun getClient(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
    }
}