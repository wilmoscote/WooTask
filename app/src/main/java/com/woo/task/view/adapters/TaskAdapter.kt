package com.woo.task.view.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.woo.task.R
import com.woo.task.databinding.CardItemBinding
import com.woo.task.model.interfaces.RecyclerViewInterface
import com.woo.task.model.responses.TaskValues
import com.woo.task.model.room.TaskDao
import com.woo.task.view.ui.activity.EditActivity
import com.woo.task.viewmodel.TasksViewModel
import dagger.assisted.AssistedInject
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.FragmentScoped
import dagger.hilt.components.SingletonComponent
import javax.annotation.meta.When
import javax.inject.Inject


class TaskAdapter (val tasks: List<TaskValues>, val recyclerViewInterface: RecyclerViewInterface
                   ) :RecyclerView.Adapter<TaskAdapter.TaskViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskAdapter.TaskViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return TaskViewHolder(layoutInflater.inflate(R.layout.card_item,parent,false))
    }

    override fun onBindViewHolder(holder: TaskAdapter.TaskViewHolder, position: Int) {
        val item = tasks[position]
        holder.bind(item)
    }

    fun removeItem(position: Int) {
        tasks.drop(position)
        notifyItemChanged(position)
    }

    override fun getItemCount(): Int = tasks.size

    inner class TaskViewHolder (view: View): RecyclerView.ViewHolder(view){
        private val binding = CardItemBinding.bind(view)
        private val context: Context? = view.context

       //private val tasksViewModel = TasksViewModel()

        fun bind(task: TaskValues){

            binding.title.text = task.title
            binding.icon.setImageResource(when(task.state){
                1 -> R.drawable.ic_pin
                2 -> R.drawable.ic_time
                3 -> R.drawable.ic_done
                else -> R.drawable.ic_pin
            })
            binding.taskBody.setOnClickListener {
                val popup = PopupMenu(context!!,binding.taskBody)
                when(task.state){
                    1 -> popup.inflate(R.menu.task_menu)
                    2 -> popup.inflate(R.menu.task_menu_1)
                    3 -> popup.inflate(R.menu.task_menu_2)
                }
                popup.setOnMenuItemClickListener { p0 ->
                    when(p0.toString()){
                        //EDITAR
                        context.getString(R.string.menu_option_0) -> {
                            val intent = Intent(context, EditActivity::class.java)
                            intent.putExtra("id",task.id.toString())
                            intent.putExtra("text",task.title.toString())
                            context.startActivity(intent)
                        }
                        //MOVER A HACER
                        context.getString(R.string.menu_option_1) -> {
                            recyclerViewInterface.moveItem(task.id!!,1)
                        }

                        //MOVER A HACIENDO
                        context.getString(R.string.menu_option_2) -> {
                            recyclerViewInterface.moveItem(task.id!!,2)
                        }

                        //MOVER A HECHO
                        context.getString(R.string.menu_option_3) -> {
                            recyclerViewInterface.moveItem(task.id!!,3)
                        }

                        //ELIMINAR
                        context.getString(R.string.menu_option_4) -> {
                            MaterialAlertDialogBuilder(context)
                                .setTitle(context.resources.getString(R.string.title_delete_dialog))
                                .setMessage(task.title)
                                .setPositiveButton(context.resources.getString(R.string.dialog_confirm)){_,_->
                                    Log.d("TaskDebug","Delete task $task")
                                    recyclerViewInterface.onClickDelete(task.id!!)
                                    //onLongClick(task.id!!.toInt())
                                }
                                .setNegativeButton(context.resources.getString(R.string.dialog_cancel)){dialog,_->
                                    dialog.dismiss()
                                }
                                .show()
                        }
                    }
                    true
                }
                popup.show()
            }

            binding.taskBody.setOnLongClickListener {
                MaterialAlertDialogBuilder(context!!)
                    .setTitle(context.resources.getString(R.string.title_delete_dialog))
                    .setMessage(task.title)
                    .setPositiveButton(context.resources.getString(R.string.dialog_confirm)){_,_->
                        Log.d("TaskDebug","Delete task $task")
                        recyclerViewInterface.onClickDelete(task.id!!)
                        //onLongClick(task.id!!.toInt())
                    }
                    .setNegativeButton(context.resources.getString(R.string.dialog_cancel)){dialog,_->
                        dialog.dismiss()
                    }
                    .show()
                // Add customization options here
                return@setOnLongClickListener true
            }
        }
    }


}