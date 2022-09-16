package com.woo.task.model.room

import android.os.Parcelable
import androidx.room.*
import kotlinx.parcelize.Parcelize

@Entity(tableName = "Task")
@Parcelize
data class Task(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id: Int?,
    @ColumnInfo(name = "title") var title: String?,
    @ColumnInfo(name = "text") var text: String?,
    @ColumnInfo(name = "attachment") var attachment: String?,
    @ColumnInfo(name = "initialDate") var initialDate: String?,
    @ColumnInfo(name = "finalDate") var finalDate: String?,
    @ColumnInfo(name = "userId") var userId: String?,
    @ColumnInfo(name = "taskId") var taskId: String?,
    @ColumnInfo(name = "state") var state: Int?,
    @ColumnInfo(name = "projectId") var projectId: String?,
    @ColumnInfo(name = "color") var color: String?,
    @ColumnInfo(name = "createdAt") var createdAt: String?,
    @ColumnInfo(name = "updatedAt") var updatedAt: String?,
    @ColumnInfo(name = "deletedAt") var deletedAt: String?,
    @ColumnInfo(name = "tags", defaultValue = "") var tags: List<String?> = listOf<String>()
    ) : Parcelable

@Entity(tableName = "Tag")
@Parcelize
data class Tag(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id: Int?,
    @ColumnInfo(name = "tagName") var tagName: String?,
) : Parcelable
