package com.woo.task.model.responses

import com.google.gson.annotations.SerializedName

data class TaskResponse (
    @SerializedName("success") var success:Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: List<TaskValues> = listOf()
)

data class TaskValues(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("text") var text: String? = null,
    @SerializedName("attachment") var attachment : String? = null,
    @SerializedName("initialDate") var initialDate: String? = null,
    @SerializedName("finalDate") var finalDate: String? = null,
    @SerializedName("userId" ) var userId: String? = null,
    @SerializedName("taskId") var taskId: String? = null,
    @SerializedName("state") var state: Int?    = null,
    @SerializedName("projectId") var projectId: String? = null,
    @SerializedName("color") var color: String? = null,
    @SerializedName("createdAt") var createdAt: String? = null,
    @SerializedName("updatedAt") var updatedAt: String? = null,
    @SerializedName("deletedAt") var deletedAt: String? = null

)