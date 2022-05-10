package com.woo.task.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.squareup.okhttp.OkHttpClient
import com.woo.task.model.apliclient.RetrofitService
import com.woo.task.model.apliclient.RetrofitServiceLenient
import com.woo.task.model.interfaces.TaskInterface
import com.woo.task.model.responses.GenericResponse
import com.woo.task.model.responses.TaskResponse
import com.woo.task.model.responses.TaskValues
import kotlinx.coroutines.launch
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TasksViewModel : ViewModel() {
    var progress = MutableLiveData<Boolean>()
    var todoTasks = MutableLiveData<List<TaskValues>>()
    var doingTasks = MutableLiveData<List<TaskValues>>()
    var doneTasks = MutableLiveData<List<TaskValues>>()
    val logRequest = HttpLoggingInterceptor()

    fun getTodoTasks(status:Int?){
        progress.postValue(true)
        viewModelScope.launch {


        val retrofit = RetrofitService.getClient()
        val service = retrofit.create(TaskInterface::class.java)
        service.getTasks(status)
            .enqueue(object: Callback<TaskResponse> {
                override fun onResponse(
                    call: Call<TaskResponse>,
                    response: Response<TaskResponse>
                ) {
                    if(response.isSuccessful){
                        todoTasks.postValue(response.body()!!.data)
                        Log.d("TASKDEBUG","TODO: ${response.body()!!}")
                    }
                    progress.postValue(false)
                }

                override fun onFailure(call: Call<TaskResponse>, t: Throwable) {
                    Log.d("TASKDEBUG",t.message.toString())
                    progress.postValue(false)
                }
            })
        }
    }

    fun getDoingTasks(status:String?){
        progress.postValue(true)
        viewModelScope.launch {
            val retrofit = RetrofitService.getClient()
            val service = retrofit.create(TaskInterface::class.java)
            service.getTasks(status?.toInt())
                .enqueue(object: Callback<TaskResponse> {
                    override fun onResponse(
                        call: Call<TaskResponse>,
                        response: Response<TaskResponse>
                    ) {
                        if(response.isSuccessful){
                            doingTasks.postValue(response.body()?.data)
                        }
                        progress.postValue(false)
                    }

                    override fun onFailure(call: Call<TaskResponse>, t: Throwable) {
                        Log.d("TASKDEBUG",t.message.toString())
                        progress.postValue(false)
                    }

                })
        }
    }

    fun getDoneTasks(status:String?){
        progress.postValue(true)
        viewModelScope.launch {
            val retrofit = RetrofitServiceLenient.getClient()
            val service = retrofit.create(TaskInterface::class.java)
            service.getTasks(status?.toInt())
                .enqueue(object: Callback<TaskResponse> {
                    override fun onResponse(
                        call: Call<TaskResponse>,
                        response: Response<TaskResponse>
                    ) {
                        if(response.isSuccessful){
                           doneTasks.postValue(response.body()?.data)
                        }
                        progress.postValue(false)
                    }

                    override fun onFailure(call: Call<TaskResponse>, t: Throwable) {
                        Log.d("TASKDEBUG",t.message.toString())
                        progress.postValue(false)
                    }

                })
        }
    }
    fun addTask(title:String?,status:String?) {
        progress.postValue(true)
        viewModelScope.launch {
            val retrofit = RetrofitService.getClient()
            val service = retrofit.create(TaskInterface::class.java)
            service.addTask(title,status?.toInt())
                .enqueue(object: Callback<GenericResponse>{
                    override fun onResponse(
                        call: Call<GenericResponse>,
                        response: Response<GenericResponse>
                    ) {
                        if(response.isSuccessful){
                            Log.d("TASKDEBUG","Tarea Agregada correctamente.")
                        }
                    }

                    override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                        Log.d("TASKDEBUG","Error: ${t.message}")
                        Log.d("TASKDEBUG","Error al agregar tarea")
                    }

                })
        }
    }

}