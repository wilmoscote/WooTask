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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val api: TaskInterface
) : ViewModel() {
    var progress = MutableLiveData<Boolean>()
    var todoTasks = MutableLiveData<List<TaskValues>>()
    var doingTasks = MutableLiveData<List<TaskValues>>()
    var doneTasks = MutableLiveData<List<TaskValues>>()
    var editTask = MutableLiveData<Task>()

    // ----------------------------- FUNCIONES LOCALES EN ROOM -------------------------------------//
    fun onCreate() {
        viewModelScope.launch {
            //Log.d("TASKDEBUG", "All Tasks: ${taskDao.getAll()}")
            withContext(Dispatchers.IO) {
                todoTasks.postValue(taskDao.getTodoTasks())
                doingTasks.postValue(taskDao.getDoingTasks())
                doneTasks.postValue(taskDao.getDoneTasks())
            }
        }
    }

    fun newTask(task: Task) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                taskDao.newTask(task)
            }
            Log.d("TASKDEBUG", "ADD TASK! ")
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                taskDao.updateTask(task)
            }
            Log.d("TASKDEBUG", "Task Updated! ")
        }
    }

    fun getTaskById(id: Int?) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                editTask.postValue(taskDao.getById(id!!))
            }
            Log.d("TASKDEBUG", "Task by Id! $editTask")
        }
    }

    fun removeTask(id: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                taskDao.deleteTask(id)
            }
            Log.d("TASKDEBUG", "Deleted Task Id! $id")
            onCreate()
        }
    }

    fun moveTask(id: Int, state: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                taskDao.moveTask(id, state)
            }
            Log.d("TASKDEBUG", "Moved Task Id! $id")
            onCreate()
        }
    }


    //----------------------------- FUNCIONES EN LA NUBE CON RETROFIT ---------------------------------//

    fun getTodoTasks(status: Int?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                api.getTasks(status)
                    .enqueue(object : Callback<TaskResponse> {
                        override fun onResponse(
                            call: Call<TaskResponse>,
                            response: Response<TaskResponse>
                        ) {
                            if (response.isSuccessful) {
                                //todoTasks.postValue(response.body()!!.data)
                                Log.d("TASKDEBUG", "TODO: ${response.body()!!}")
                            }
                        }

                        override fun onFailure(call: Call<TaskResponse>, t: Throwable) {
                            Log.d("TASKDEBUG", t.message.toString())
                        }
                    })
            }
        }
    }


    fun deleteToDoTask(id: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                api.deleteTask(id)
                    .enqueue(object : Callback<GenericResponse> {
                        override fun onResponse(
                            call: Call<GenericResponse>,
                            response: Response<GenericResponse>
                        ) {
                            if (response.isSuccessful) {
                                Log.d("TaskDebug", "Deleted from server")
                            }
                            Log.d("TaskDebug", response.body()!!.toString())
                        }

                        override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                            Log.d("TaskDebug", t.message.toString())
                        }
                    })
            }
        }
    }

    fun getDoingTasks(status: String?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                api.getTasks(status?.toInt())
                    .enqueue(object : Callback<TaskResponse> {
                        override fun onResponse(
                            call: Call<TaskResponse>,
                            response: Response<TaskResponse>
                        ) {
                            if (response.isSuccessful) {
                               //doingTasks.postValue(response.body()?.data)
                                Log.d("TASKDEBUG", "DOING: ${response.body()!!}")
                            }
                        }

                        override fun onFailure(call: Call<TaskResponse>, t: Throwable) {
                            Log.d("TASKDEBUG", t.message.toString())
                        }
                    })
            }
        }
    }

    fun getDoneTasks(status: String?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                api.getTasks(status?.toInt())
                    .enqueue(object : Callback<TaskResponse> {
                        override fun onResponse(
                            call: Call<TaskResponse>,
                            response: Response<TaskResponse>
                        ) {
                            if (response.isSuccessful) {
                                //doneTasks.postValue(response.body()?.data)
                                Log.d("TASKDEBUG", "DONE: ${response.body()!!}")
                            }
                        }

                        override fun onFailure(call: Call<TaskResponse>, t: Throwable) {
                            Log.d("TASKDEBUG", t.message.toString())
                        }

                    })
            }
        }
    }

    fun addTask(title: String?, status: String?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                api.addTask(title, status?.toInt())
                    .enqueue(object : Callback<GenericResponse> {
                        override fun onResponse(
                            call: Call<GenericResponse>,
                            response: Response<GenericResponse>
                        ) {
                            if (response.isSuccessful) {
                                Log.d("TaskDebug", "Tarea Agregada correctamente.")
                            }
                        }

                        override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                            Log.d("TASKDEBUG", "Error: ${t.message}")
                            Log.d("TASKDEBUG", "Error al agregar tarea")
                        }
                    })
            }
        }
    }
}