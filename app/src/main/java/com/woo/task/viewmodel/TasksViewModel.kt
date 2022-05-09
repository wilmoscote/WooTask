package com.woo.task.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.woo.task.model.apliclient.RetrofitService
import com.woo.task.model.apliclient.RetrofitServiceLenient
import com.woo.task.model.interfaces.TaskInterface
import com.woo.task.model.responses.TaskResponse
import com.woo.task.model.responses.TaskValues
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TasksViewModel : ViewModel() {
    var progress = MutableLiveData<Boolean>()
    var todoTasks = MutableLiveData<List<TaskValues>>()
    var doingTasks = MutableLiveData<List<TaskValues>>()
    var doneTasks = MutableLiveData<List<TaskValues>>()

    fun getTodoTasks(status:String?){
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
                        todoTasks.postValue(response.body()!!.result)
                        Log.d("TASKDEBUG","TODO: ${response.body()!!.result}")
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
            service.getTasks(status)
                .enqueue(object: Callback<TaskResponse> {
                    override fun onResponse(
                        call: Call<TaskResponse>,
                        response: Response<TaskResponse>
                    ) {
                        if(response.isSuccessful){
                            doingTasks.postValue(response.body()?.result)
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
            service.getTasks(status)
                .enqueue(object: Callback<TaskResponse> {
                    override fun onResponse(
                        call: Call<TaskResponse>,
                        response: Response<TaskResponse>
                    ) {
                        if(response.isSuccessful){
                           doneTasks.postValue(response.body()?.result)
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
}