package com.woo.task.model.interfaces

import com.woo.task.model.responses.TaskResponse
import retrofit2.Call
import retrofit2.http.*

interface TaskInterface {
    @Headers("Accept: application/json")
    @GET("tasks/{state}/state")
    fun getTasks(@Path(value = "state", encoded = true) state:String?): Call<TaskResponse>

}