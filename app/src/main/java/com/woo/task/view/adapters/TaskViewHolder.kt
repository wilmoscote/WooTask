package com.woo.task.view.adapters

/*
class TaskViewHolder (view: View): RecyclerView.ViewHolder(view){
    private val binding = CardItemBinding.bind(view)
    private val context: Context? = view.context

    private val tasksViewModel = TasksViewModel()

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

        binding.taskBody.setOnLongClickListener {
            MaterialAlertDialogBuilder(context!!)
                .setTitle(context.resources.getString(R.string.title_delete_dialog))
                .setMessage(task.title)
                .setPositiveButton(context.resources.getString(R.string.dialog_confirm)){_,_->
                    //Log.d("TaskDebug","Delete task ${task.id}")
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
}*/
