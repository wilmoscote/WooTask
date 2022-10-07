package com.woo.task.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.woo.task.model.interfaces.TaskInterface
import com.woo.task.model.responses.GenericResponse
import com.woo.task.model.responses.TaskResponse
import com.woo.task.model.responses.TaskValues
import com.woo.task.model.room.Tag
import com.woo.task.model.room.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import com.woo.task.model.room.Task
import com.woo.task.model.utils.TaskModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val api: TaskInterface
) : ViewModel() {
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val user = auth.currentUser?.uid.toString()
    var progress = MutableLiveData<Boolean>()
    var allTasks = MutableLiveData<List<Task>>()
    var todoTasks = MutableLiveData<List<TaskValues>>()
    var doingTasks = MutableLiveData<List<TaskValues>>()
    var doneTasks = MutableLiveData<List<TaskValues>>()
    var editTask = MutableLiveData<Task>()
    var tags = MutableLiveData<List<Tag>>()

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

    fun getAllTasks() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                allTasks.postValue(taskDao.getAll())
            }
        }
    }

    fun newTask(task: Task) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                Log.d("TASKDEBUG", "TASK! ${task.state}")
                taskDao.newTask(task)
                when(task.state){
                    1 -> todoTasks.postValue(taskDao.getTodoTasks())
                    2 -> doingTasks.postValue(taskDao.getDoingTasks())
                    3 -> doneTasks.postValue(taskDao.getDoneTasks())
                }
            }
            Log.d("TASKDEBUG", "ADD TASK! ")
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                taskDao.updateTask(task)
                when(task.state){
                    1 -> todoTasks.postValue(taskDao.getTodoTasks())
                    2 -> doingTasks.postValue(taskDao.getDoingTasks())
                    3 -> doneTasks.postValue(taskDao.getDoneTasks())
                }
            }
            Log.d("TASKDEBUG", "Task Updated! ")
        }
    }

    fun addTag(tag:String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                taskDao.addTag(Tag(null,tag))
                getTags()
            }
            Log.d("TASKDEBUG", "Task Updated! ")
        }
    }

    fun removeTag(id:Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                taskDao.removeTag(id)
                getTags()
            }
            Log.d("TASKDEBUG", "Task Updated! ")
        }
    }

    fun getTags(){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                tags.postValue(taskDao.getTags())
            }
            Log.d("TASKDEBUG", "Tags Fetched!")
        }
    }

    fun getTaskById(id: Int?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                editTask.postValue(taskDao.getById(id!!))
            }
            Log.d("TASKDEBUG", "Task by Id! $editTask")
        }
    }

    fun removeTask(id: Int,state: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                taskDao.deleteTask(id)
                when(state){
                    1 -> {
                        Log.d("TASKDEBUG", "OBTAINING TODO TASK!")
                        todoTasks.postValue(taskDao.getTodoTasks())
                    }
                    2 -> doingTasks.postValue(taskDao.getDoingTasks())
                    3 -> doneTasks.postValue(taskDao.getDoneTasks())
                }
            }
            Log.d("TASKDEBUG", "Deleted Task Id! $id")
            //onCreate()
        }
    }

    fun moveTask(id: Int, state: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
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

    //---------------------- FUNCIONES EN LA NUBE CON FIRESTORE -----------------------------//
    private val TAGF = "FirestoreDebug"
    fun checkCloudTasks() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                db.collection("Task").document(user).get()
                    .addOnSuccessListener { tasks ->
                        if (tasks.exists()) {
                            Log.d(TAGF, "Document exists $tasks")
                            getCloudTasks()
                        } else {
                            Log.d(TAGF, "Document doesn't exists $tasks")
                            createTaskCloud()
                        }
                    }
            }
        }
    }

    private fun getCloudTasks() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                Log.d(TAGF, "Fetching Data")
                val taskRef = db.collection("Task").document(user)
                taskRef.collection("tasks").orderBy("createdAt", Query.Direction.ASCENDING).get()
                    .addOnSuccessListener { tasks ->
                        val taskList = tasks.toObjects(TaskModel::class.java)
                        Log.d(TAGF, "TASKS FETCHED: $taskList")
                    }
                    .addOnFailureListener {
                        Log.d(TAGF, "FAIL! ${it.message.toString()}")
                    }
            }
        }
    }

    private fun createTaskCloud() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val task = TaskModel(
                    createdAt = Timestamp.now(),
                    text = "",
                    uid = user,
                )
                val taskRef = db.collection("Task").document(user)
                taskRef.collection("tasks").document().set(task)
                Log.d(TAGF, "New Task Cloud Created!")
            }
        }
    }
}