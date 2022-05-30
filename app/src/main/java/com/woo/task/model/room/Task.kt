package com.woo.task.model.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id: Int?,
    @ColumnInfo(name = "title")var title: String?,
    @ColumnInfo(name = "text")var text: String?,
    @ColumnInfo(name = "attachment")var attachment : String?,
    @ColumnInfo(name = "initialDate")var initialDate: String?,
    @ColumnInfo(name = "finalDate")var finalDate: String?,
    @ColumnInfo(name = "userId")var userId: String?,
    @ColumnInfo(name = "taskId")var taskId: String?,
    @ColumnInfo(name = "state")var state: Int?,
    @ColumnInfo(name = "projectId")var projectId: String?,
    @ColumnInfo(name = "color")var color: String?,
    @ColumnInfo(name = "createdAt")var createdAt: String?,
    @ColumnInfo(name = "updatedAt")var updatedAt: String?,
    @ColumnInfo(name = "deletedAt")var deletedAt: String?
)
