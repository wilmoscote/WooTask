package com.woo.task.model.responses

import com.google.gson.annotations.SerializedName

data class TaskResponse (
    @SerializedName("success") var success:Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("result") var result: List<TaskValues> = listOf()
)

data class TaskValues(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("text") var text: String? = null,
    @SerializedName("attachment") var attachment : String? = null,
    @SerializedName("initial_date") var initialDate: String? = null,
    @SerializedName("final_date") var finalDate: String? = null,
    @SerializedName("user_id" ) var userId: String? = null,
    @SerializedName("task_id") var taskId: String? = null,
    @SerializedName("state") var state: Int?    = null,
    @SerializedName("project_id") var projectId: String? = null,
    @SerializedName("color") var color: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("deleted_at") var deletedAt: String? = null

)