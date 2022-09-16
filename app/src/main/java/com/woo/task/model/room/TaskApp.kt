package com.woo.task.model.room

import android.app.Application
import androidx.room.Room
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TaskApp {

    @Singleton
    @Provides
    fun provideRoom(@ApplicationContext context:Context) = Room
        .databaseBuilder(context, TaskDb::class.java,"task")
        .allowMainThreadQueries()
        .build()

    @Singleton
    @Provides
    fun provideTaskDao(db:TaskDb):TaskDao{
        return db.taskDao()
    }

}