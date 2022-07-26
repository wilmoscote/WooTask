package com.woo.task.view.adapters

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.woo.task.R
import com.woo.task.databinding.CardItemBinding
import com.woo.task.model.interfaces.RecyclerViewInterface
import com.woo.task.model.responses.TaskValues
import com.woo.task.model.room.Task
import com.woo.task.model.utils.AlarmReceiver
import java.text.SimpleDateFormat
import java.util.*


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

    override fun getItemCount(): Int = tasks.size

    inner class TaskViewHolder (view: View): RecyclerView.ViewHolder(view){
        private val binding = CardItemBinding.bind(view)
        private val context: Context? = view.context
        private lateinit var picker: MaterialTimePicker
        private var selectedTime = ""
       //private val tasksViewModel = TasksViewModel()

        fun bind(task: TaskValues){

            binding.title.text = task.title
            /*binding.icon.setImageResource(when(task.state){
                1 -> R.drawable.ic_pin
                2 -> R.drawable.ic_time
                3 -> R.drawable.ic_done
                else -> R.drawable.ic_pin
            })*/
            when(task.color){
                "1" -> binding.taskItemBody.setBackgroundColor(ContextCompat.getColor(context!!, R.color.bg_0))
                "2" -> binding.taskItemBody.setBackgroundColor(ContextCompat.getColor(context!!, R.color.bg_1))
                "3" -> binding.taskItemBody.setBackgroundColor(ContextCompat.getColor(context!!, R.color.bg_2))
                "4" -> binding.taskItemBody.setBackgroundColor(ContextCompat.getColor(context!!, R.color.bg_3))
                "5" -> binding.taskItemBody.setBackgroundColor(ContextCompat.getColor(context!!, R.color.bg_4))
                else -> binding.taskItemBody.setBackgroundColor(ContextCompat.getColor(context!!, R.color.bg_2))
            }

            binding.taskBody.setOnFocusChangeListener { _, b ->
                binding.layoutTool.isVisible = b
            }

            binding.btnEdit.setOnClickListener {
                val dialog = BottomSheetDialog(context!!, R.style.CustomBottomSheetDialog)
                val viewSheet = LayoutInflater.from(context).inflate(R.layout.new_task_sheet, null)
                dialog.setOnShowListener {
                    val bottomSheetDialog = it as BottomSheetDialog
                    val parentLayout =
                        bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                    parentLayout?.let { layout ->
                        setupFullHeight(layout)
                    }
                }
                val txtAdd = viewSheet.findViewById<EditText>(R.id.newTask)
                txtAdd.setText(task.text)
                val btnAdd = viewSheet.findViewById<Button>(R.id.btnSave)
                val btnCancel = viewSheet.findViewById<Button>(R.id.btnCancel)
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                val date = sdf.format(Date())
                btnCancel.setOnClickListener {
                    dialog.dismiss()
                }
                btnAdd.setOnClickListener {
                    recyclerViewInterface.updateTask(
                        Task(
                            task.id,
                            txtAdd.text.toString(),
                            txtAdd.text.toString(),
                            null,
                            task.initialDate,
                            task.finalDate,
                            task.userId,
                            task.taskId,
                            task.state,
                            task.projectId,
                            task.color,
                            task.createdAt,
                            date.toString(),
                            ""
                        )
                    )
                    dialog.dismiss()
                }
                dialog.setCancelable(true)
                dialog.setContentView(viewSheet)
                dialog.show()
            }

            binding.btnDelete.setOnClickListener {
                MaterialAlertDialogBuilder(context!!)
                    .setTitle(context.resources.getString(R.string.title_delete_dialog))
                    .setMessage(task.title)
                    .setPositiveButton(context.resources.getString(R.string.dialog_confirm)){_,_->
                        Log.d("TaskDebug","Delete task $task")
                        recyclerViewInterface.onClickDelete(task.id!!,task.state!!)
                        //onLongClick(task.id!!.toInt())
                    }
                    .setNegativeButton(context.resources.getString(R.string.dialog_cancel)){dialog,_->
                        dialog.dismiss()
                    }
                    .show()
            }

            binding.btnCheck.setOnClickListener {
                val popup = PopupMenu(context!!,binding.btnCheck)
                when(task.state){
                    1 -> popup.inflate(R.menu.task_menu)
                    2 -> popup.inflate(R.menu.task_menu_1)
                    3 -> popup.inflate(R.menu.task_menu_2)
                }
                popup.setOnMenuItemClickListener { p0 ->
                    when(p0.toString()){
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
                    }
                    true
                }
                popup.show()
            }

            binding.btnColor.setOnClickListener {
                val dialog = BottomSheetDialog(context!!, R.style.CustomBottomSheetDialog)
                val viewSheet = LayoutInflater.from(context).inflate(R.layout.color_sheet, null)
                dialog.setOnShowListener {
                    val bottomSheetDialog = it as BottomSheetDialog
                    val parentLayout =
                        bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                    parentLayout?.let { layout ->
                        //setupFullHeight(layout)
                    }
                }
                val btnColor0 = viewSheet.findViewById<Button>(R.id.btnColor0)
                val btnColor1 = viewSheet.findViewById<Button>(R.id.btnColor1)
                val btnColor2 = viewSheet.findViewById<Button>(R.id.btnColor2)
                val btnColor3 = viewSheet.findViewById<Button>(R.id.btnColor3)
                val btnColor4 = viewSheet.findViewById<Button>(R.id.btnColor4)

                btnColor0.setOnClickListener{changeItemColor(task,"1")}
                btnColor1.setOnClickListener{changeItemColor(task,"2")}
                btnColor2.setOnClickListener{changeItemColor(task,"3")}
                btnColor3.setOnClickListener{changeItemColor(task,"4")}
                btnColor4.setOnClickListener{changeItemColor(task,"5")}

                dialog.setCancelable(true)
                dialog.setContentView(viewSheet)
                dialog.show()
            }


            binding.btnTime.setOnClickListener {
                val dialog = BottomSheetDialog(context, R.style.CustomBottomSheetDialog)
                val viewSheet = LayoutInflater.from(context).inflate(R.layout.date_sheet, null)
                dialog.setOnShowListener {
                    val bottomSheetDialog = it as BottomSheetDialog
                    val parentLayout =
                        bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                    parentLayout?.let { layout ->
                        setupFullHeight(layout)

                    }
                }
                val datePicker = viewSheet.findViewById<DatePicker>(R.id.datePicker)
                val switchHour = viewSheet.findViewById<SwitchMaterial>(R.id.switchHour)
                val txtHour = viewSheet.findViewById<Button>(R.id.txtHour)

                datePicker.setOnDateChangedListener { datePicker, i, i2, i3 ->
                    val day = datePicker.dayOfMonth
                    val month = datePicker.month
                    val year = datePicker.year

                    val mes = if (month + 1 < 10) {
                        "0${month + 1}"
                    } else {
                        (month + 1).toString()
                    }
                    val dia = if(day < 10) {
                        "0$day"
                    }else{
                        day.toString()
                    }
                    //dateText.text = "$year-${mes}-$dia"
                }

                switchHour.setOnCheckedChangeListener { _, isChecked ->
                    txtHour.isVisible = isChecked
                }

                txtHour.setOnClickListener {
                    txtHour.text = showTimePicker()
                }

                dialog.setCancelable(true)
                dialog.setContentView(viewSheet)
                dialog.show()
            }

            binding.taskBody.setOnLongClickListener {
                MaterialAlertDialogBuilder(context!!)
                    .setTitle(context.resources.getString(R.string.title_delete_dialog))
                    .setMessage(task.title)
                    .setPositiveButton(context.resources.getString(R.string.dialog_confirm)){_,_->
                        Log.d("TaskDebug","Delete task $task")
                        recyclerViewInterface.onClickDelete(task.id!!,task.state!!)
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

        private fun setupFullHeight(bottomSheet: View) {
            val layoutParams = bottomSheet.layoutParams
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
            bottomSheet.layoutParams = layoutParams
        }

        private fun changeItemColor(task:TaskValues, color:String){
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
            val date = sdf.format(Date())
            recyclerViewInterface.updateTask(
                Task(
                    task.id,
                    task.text,
                    task.text,
                    null,
                    task.initialDate,
                    task.finalDate,
                    task.userId,
                    task.taskId,
                    task.state,
                    task.projectId,
                    color,
                    task.createdAt,
                    date.toString(),
                    ""
                )
            )
        }

        private fun showTimePicker():String {
            picker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText(context!!.getString(R.string.set_alarm_title))
                .build()
            val ft = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            picker.show(ft,"WooTask")

            picker.addOnPositiveButtonClickListener{
                selectedTime = if (picker.hour > 12){
                    String.format("%02d",picker.hour - 12) + " : " + String.format("%02d",picker.minute) + "PM"
                }else{
                    String.format("%02d",picker.hour) + " : " + String.format("%02d",picker.minute) + "AM"
                }

                /*val calendar = Calendar.getInstance()
                calendar[Calendar.HOUR_OF_DAY] = picker.hour
                calendar[Calendar.MINUTE] = picker.minute
                calendar[Calendar.SECOND] = 0
                calendar[Calendar.MILLISECOND] = 0

                val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
                val intent = Intent(context, AlarmReceiver::class.java)

                val pendingIntent = PendingIntent.getBroadcast(context,0,intent,0)

                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,pendingIntent
                )
                Toast.makeText(context,"Recordatorio Agregado",Toast.LENGTH_SHORT).show()*/

            }
            return selectedTime
        }
    }






























}