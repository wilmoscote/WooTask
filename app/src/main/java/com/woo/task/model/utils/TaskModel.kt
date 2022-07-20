package com.woo.task.model.utils

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class TaskModel(
    @ServerTimestamp
    var createdAt: Timestamp? = null,
    var text:String? = null,
    var uid: String? = null
)
