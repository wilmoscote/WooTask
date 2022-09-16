package com.woo.task.model.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.woo.task.model.responses.TaskValues

@Dao
interface TaskDao {

    @Query("DELETE FROM Task")
    fun clean()

    @Query("SELECT * FROM Task")
    suspend fun getAll(): List<Task>

    @Query("SELECT * FROM Task WHERE id = :id")
    suspend fun getById(id:Int): Task

    @Query("SELECT * FROM Task WHERE state = 1 ORDER BY createdAt ASC")
    suspend fun getTodoTasks(): List<TaskValues>

    @Query("SELECT * FROM Task WHERE state = 2 ORDER BY createdAt ASC")
    suspend fun getDoingTasks(): List<TaskValues>

    @Query("SELECT * FROM Task WHERE state = 3 ORDER BY createdAt ASC")
    suspend fun getDoneTasks(): List<TaskValues>

    @Update
    suspend fun updateTask(task: Task)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun newTask(task: Task)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun backupTasks(task: List<Task>)

    @Query("DELETE FROM Task WHERE id = :id")
    suspend fun deleteTask(id: Int)

    @Query("UPDATE Task SET state = :state WHERE id = :id")
    suspend fun moveTask(id: Int,state:Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTag(tag: Tag)

    @Query("DELETE FROM Tag WHERE id = :id")
    suspend fun removeTag(id: Int)

    @Query("SELECT * FROM Tag")
    suspend fun getTags(): List<Tag>

}