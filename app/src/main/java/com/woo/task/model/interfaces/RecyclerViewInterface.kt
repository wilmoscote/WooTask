package com.woo.task.model.interfaces

interface RecyclerViewInterface {
    fun onLongClick(position:Int)
    fun moveItem(id:Int,state:Int)
    fun onClickDelete(id:Int)
}