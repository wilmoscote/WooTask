package com.woo.task.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.okhttp.OkHttpClient
import com.woo.task.model.apliclient.RetrofitService
import com.woo.task.model.apliclient.RetrofitServiceLenient
import com.woo.task.model.interfaces.RecyclerViewInterface
import com.woo.task.model.interfaces.TaskInterface
import com.woo.task.model.responses.GenericResponse
import com.woo.task.model.responses.TaskResponse
import com.woo.task.model.responses.TaskValues
import com.woo.task.model.room.TaskApp
import com.woo.task.model.room.TaskDao
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.launch
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import javax.inject.Inject
import com.woo.task.model.room.Task

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskDao: TaskDao
): ViewModel() {
    var progress = MutableLiveData<Boolean>()
    var todoTasks = MutableLiveData<List<TaskValues>>()
    var doingTasks = MutableLiveData<List<TaskValues>>()
    var doneTasks = MutableLiveData<List<TaskValues>>()
    var editTask = MutableLiveData<Task>()
    val logRequest = HttpLoggingInterceptor()

    fun onCreate() {
        viewModelScope.launch {
            Log.d("TASKDEBUG","All Tasks: ${taskDao.getAll()}")
            todoTasks.postValue(taskDao.getTodoTasks())
            doingTasks.postValue(taskDao.getDoingTasks())
            doneTasks.postValue(taskDao.getDoneTasks())
        }
    }

    fun newTask(task:Task){
        viewModelScope.launch {
            taskDao.newTask(task)
            Log.d("TASKDEBUG","ADD TASK! ")
        }
    }

    fun updateTask(task:Task){
        viewModelScope.launch {
            taskDao.updateTask(task)
            Log.d("TASKDEBUG","Task Updated! ")
        }
    }

    fun getTaskById(id: Int?){
        viewModelScope.launch {
            editTask.postValue(taskDao.getById(id!!))
            Log.d("TASKDEBUG","Task by Id! $editTask")
        }
    }

    fun removeTask(id:Int){
        viewModelScope.launch {
            taskDao.deleteTask(id)
            Log.d("TASKDEBUG","Deleted Task Id! $id")
            onCreate()
        }
    }

    fun moveTask(id:Int,state:Int){
        viewModelScope.launch {
            taskDao.moveTask(id,state)
            Log.d("TASKDEBUG","Moved Task Id! $id")
            onCreate()
        }
    }

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



    fun deleteToDoTask(index:Int){
        progress.postValue(true)
        Log.d("TaskDebug","Deleting in ViewModel: $index")
        viewModelScope.launch {
            val pos = todoTasks.value!![index].id
            val items = todoTasks.value!!.toMutableList().apply {
                removeAt(index)
            }.toList()
            todoTasks.postValue(items)

            val retrofit = RetrofitService.getClient()
            val service = retrofit.create(TaskInterface::class.java)
            service.deleteTask(pos)
                .enqueue(object: Callback<GenericResponse> {
                    override fun onResponse(
                        call: Call<GenericResponse>,
                        response: Response<GenericResponse>
                    ) {
                        if(response.isSuccessful){
                            Log.d("TaskDebug","Deleted from server")
                        }
                        Log.d("TaskDebug",response.body()!!.toString())
                        progress.postValue(false)
                    }

                    override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                        Log.d("TaskDebug",t.message.toString())
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
            /*when(status){
                "21" -> {
                    val items = todoTasks.value!!.toMutableList().apply {
                        add(TaskValues(0,title,"","","","","","",status.toInt(),"","","","",""))
                    }.toList()
                    todoTasks.postValue(items)
                }
                "22" -> {}
                "23" -> {}
            }*/
            val retrofit = RetrofitService.getClient()
            val service = retrofit.create(TaskInterface::class.java)
            service.addTask(title,status?.toInt())
                .enqueue(object: Callback<GenericResponse>{
                    override fun onResponse(
                        call: Call<GenericResponse>,
                        response: Response<GenericResponse>
                    ) {
                        if(response.isSuccessful){
                            Log.d("TaskDebug","Tarea Agregada correctamente.")
                        }
                    }

                    override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                        Log.d("TASKDEBUG","Error: ${t.message}")
                        Log.d("TASKDEBUG","Error al agregar tarea")
                    }

                })
        }
    }

    fun deleteTask(){

    }


}