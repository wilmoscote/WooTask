package com.woo.task.view.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.woo.task.R
import com.woo.task.databinding.CardItemBinding
import com.woo.task.databinding.ItemTodoBinding
import com.woo.task.model.responses.TaskValues
import com.woo.task.view.ui.activity.EditActivity

class TaskViewHolder (view: View): RecyclerView.ViewHolder(view){
    private val binding = CardItemBinding.bind(view)
    private val context: Context? = view.context

    fun bind(task: TaskValues){
        binding.title.text = task.title
        binding.icon.setImageResource(when(task.state){
            21 -> R.drawable.ic_pin
            22 -> R.drawable.ic_time
            23 -> R.drawable.ic_done
            else -> R.drawable.ic_pin
        })
       binding.taskBody.setOnClickListener {
            val intent = Intent(context, EditActivity::class.java)
            intent.putExtra("id",task.id.toString())
            intent.putExtra("text",task.title.toString())
            context!!.startActivity(intent)
        }

    }
}