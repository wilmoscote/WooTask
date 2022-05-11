package com.woo.task.view.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.woo.task.R
import com.woo.task.model.responses.TaskValues

class TaskAdapter(val tasks: List<TaskValues>):RecyclerView.Adapter<TaskViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return TaskViewHolder(layoutInflater.inflate(R.layout.card_item,parent,false))
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val item = tasks[position]
        holder.bind(item)

    }

    fun removeItem(position: Int) {
        tasks.drop(position)
        notifyItemChanged(position)
    }

    override fun getItemCount(): Int = tasks.size

}