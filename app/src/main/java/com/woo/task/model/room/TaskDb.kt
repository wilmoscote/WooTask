package com.woo.task.model.room

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    entities = [Task::class],
    version = 1,
    exportSchema = false
)

abstract class TaskDb :RoomDatabase(){
    abstract fun taskDao():TaskDao
}