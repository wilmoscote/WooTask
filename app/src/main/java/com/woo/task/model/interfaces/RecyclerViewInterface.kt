package com.woo.task.model.interfaces

import com.woo.task.model.room.Task

interface RecyclerViewInterface {
    fun onLongClick(position:Int)
    fun moveItem(id:Int?,state:Int)
    fun onClickDelete(id:Int,state: Int)
    fun updateTask(task: Task)
}