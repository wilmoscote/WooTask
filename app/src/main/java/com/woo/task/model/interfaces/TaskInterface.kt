package com.woo.task.model.interfaces

import com.squareup.okhttp.ResponseBody
import com.woo.task.model.responses.GenericResponse
import com.woo.task.model.responses.TaskResponse
import retrofit2.Call
import retrofit2.http.*

interface TaskInterface {

    @GET("tasks")
    fun getTasks(@Query("state") state:Int?): Call<TaskResponse>

    @POST("tasks")
    fun addTask(@Query("title") title:String?,@Query("state") state:Int?): Call<GenericResponse>

    @DELETE("tasks/{id}")
    fun deleteTask(@Path("id") id:Int?): Call<GenericResponse>

}