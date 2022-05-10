package com.woo.task.model.responses

import com.google.gson.annotations.SerializedName

data class GenericResponse(
    @SerializedName("success") var success:Boolean? = null,
    @SerializedName("message") var message: String? = null,
)
