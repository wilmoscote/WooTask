package com.woo.task.model.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Task::class,Tag::class], version = 1, exportSchema = true,
)
@TypeConverters(Converters::class)

abstract class TaskDb :RoomDatabase(){
    abstract fun taskDao():TaskDao
}