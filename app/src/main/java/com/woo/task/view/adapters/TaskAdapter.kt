package com.woo.task.view.adapters

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.content.res.ColorStateList
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
import androidx.core.view.size
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.woo.task.R
import com.woo.task.databinding.CardItemBinding
import com.woo.task.model.interfaces.RecyclerViewInterface
import com.woo.task.model.responses.TaskValues
import com.woo.task.model.room.Tag
import com.woo.task.model.room.Task
import com.woo.task.model.utils.AlarmReceiver
import com.woo.task.view.ui.activities.LoginActivity
import java.text.SimpleDateFormat
import java.util.*


class TaskAdapter(
    private val tasks: List<TaskValues>, val recyclerViewInterface: RecyclerViewInterface
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskAdapter.TaskViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return TaskViewHolder(layoutInflater.inflate(R.layout.card_item, parent, false))
    }

    override fun onBindViewHolder(holder: TaskAdapter.TaskViewHolder, position: Int) {
        val item = tasks[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = tasks.size

    inner class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = CardItemBinding.bind(view)
        private val context: Context? = view.context
        private lateinit var picker: MaterialTimePicker
        private var selectedTime = ""
        private var showingSheet = false
        var hour = 0
        var minute = 0
        var day = 0
        var month = 0
        var year = 0
        //private val tasksViewModel = TasksViewModel()

        fun bind(task: TaskValues) {
            binding.layoutTool.isVisible = false

            binding.title.text = task.title
            /*binding.icon.setImageResource(when(task.state){
                1 -> R.drawable.ic_pin
                2 -> R.drawable.ic_time
                3 -> R.drawable.ic_done
                else -> R.drawable.ic_pin
            })*/
            val myColorStateList = ColorStateList(
                arrayOf(intArrayOf(android.R.attr.textAppearance), intArrayOf()), intArrayOf(
                    context!!.resources.getColor(R.color.white),
                    context!!.resources.getColor(R.color.black)
                )
            )

            if (task.tags.isNotEmpty()) {
                for (tag in task.tags) {
                    val chip = Chip(context)
                    chip.text = tag
                    chip.setChipBackgroundColorResource(
                        when (task.color) {
                            "1" -> R.color.bg_0
                            "2" -> R.color.bg_1
                            "3" -> R.color.bg_2
                            "4" -> R.color.bg_3
                            "5" -> R.color.bg_4
                            else -> R.color.bg_2
                        }
                    )
                    chip.isClickable = false
                    chip.chipStrokeWidth = 2f
                    chip.setChipStrokeColorResource(R.color.black)
                    // chip.chipStrokeColor = ColorStateList.valueOf(ContextCompat.getColor(context!!,ContextCompat.getColor(context!!, R.color.black)))
                    chip.setTextColor(ContextCompat.getColor(context!!, R.color.black))
                    binding.tagsGroup.addView(chip)
                }
            }

            when (task.color) {
                "1" -> binding.taskItemBody.setBackgroundColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.bg_0
                    )
                )
                "2" -> binding.taskItemBody.setBackgroundColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.bg_1
                    )
                )
                "3" -> binding.taskItemBody.setBackgroundColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.bg_2
                    )
                )
                "4" -> binding.taskItemBody.setBackgroundColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.bg_3
                    )
                )
                "5" -> binding.taskItemBody.setBackgroundColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.bg_4
                    )
                )
                else -> binding.taskItemBody.setBackgroundColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.bg_2
                    )
                )
            }

            binding.taskBody.setOnFocusChangeListener { _, b ->
                binding.layoutTool.isVisible = b
                Log.d("ToolsDebug", b.toString())
            }

            binding.btnEdit.setOnClickListener {
                if (showingSheet) return@setOnClickListener
                showingSheet = true
                val dialog = BottomSheetDialog(context, R.style.CustomBottomSheetDialog)
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
                            "",
                            listOf<String>()
                        )
                    )
                    dialog.dismiss()
                }
                dialog.setOnDismissListener {
                    showingSheet = false
                }
                dialog.setCancelable(true)
                dialog.setContentView(viewSheet)
                dialog.show()
            }

            binding.btnDelete.setOnClickListener {
                MaterialAlertDialogBuilder(context)
                    .setTitle(context.resources.getString(R.string.title_delete_dialog))
                    .setMessage(task.title)
                    .setPositiveButton(context.resources.getString(R.string.dialog_confirm)) { _, _ ->
                        Log.d("TaskDebug", "Delete task $task")
                        recyclerViewInterface.onClickDelete(task.id!!, task.state!!)
                        //onLongClick(task.id!!.toInt())
                    }
                    .setNegativeButton(context.resources.getString(R.string.dialog_cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }

            binding.btnCheck.setOnClickListener {
                val popup = PopupMenu(context, binding.btnCheck)
                when (task.state) {
                    1 -> popup.inflate(R.menu.task_menu)
                    2 -> popup.inflate(R.menu.task_menu_1)
                    3 -> popup.inflate(R.menu.task_menu_2)
                }
                popup.setOnMenuItemClickListener { p0 ->
                    when (p0.toString()) {
                        //MOVER A HACER
                        context.getString(R.string.menu_option_1) -> {
                            recyclerViewInterface.moveItem(task.id!!, 1)
                        }

                        //MOVER A HACIENDO
                        context.getString(R.string.menu_option_2) -> {
                            recyclerViewInterface.moveItem(task.id!!, 2)
                        }

                        //MOVER A HECHO
                        context.getString(R.string.menu_option_3) -> {
                            recyclerViewInterface.moveItem(task.id!!, 3)
                        }
                    }
                    true
                }
                popup.show()
            }

            binding.btnTag.setOnClickListener {
                if (showingSheet) return@setOnClickListener
                showingSheet = true
                val tags = getTags()
                val dialog = BottomSheetDialog(context, R.style.CustomBottomSheetDialog)
                val viewSheet = LayoutInflater.from(context).inflate(R.layout.tag_sheet, null)
                dialog.setOnShowListener {
                    val bottomSheetDialog = it as BottomSheetDialog
                    val parentLayout =
                        bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                    parentLayout?.let { layout ->
                        setupFullHeight(layout)
                    }
                }

                val btnSave = viewSheet.findViewById<Button>(R.id.btnSave)
                val btnCancel = viewSheet.findViewById<Button>(R.id.btnCancel)
                val btnAdd = viewSheet.findViewById<Button>(R.id.btnAddTag)
                val txtTag = viewSheet.findViewById<EditText>(R.id.txtTag)
                val tagGroup = viewSheet.findViewById<ChipGroup>(R.id.tagGroup)
                val taskTagGroup = viewSheet.findViewById<ChipGroup>(R.id.taskTagGroup)
                val notTag = viewSheet.findViewById<TextView>(R.id.noTags)
                val notTagGeneral = viewSheet.findViewById<TextView>(R.id.noTagsGeneral)

                Log.d("TASKDEBUG", "TAGS: ${task.tags.toString()}")
                if (task.tags.isNotEmpty()) {
                    for (tag in task.tags) {
                        val chip = Chip(context)
                        chip.text = tag
                        chip.isCloseIconVisible = true
                        chip.isCheckable = true
                        chip.isClickable = true
                        chip.setOnCloseIconClickListener {
                            MaterialAlertDialogBuilder(this.context)
                                .setTitle(context.resources.getString(R.string.delete_tag_title))
                                .setMessage(context.getString(R.string.delete_tag_message))
                                .setPositiveButton(context.resources.getString(R.string.dialog_confirm)) { _, _ ->
                                    taskTagGroup.removeView(chip)
                                    if (taskTagGroup.size <= 0) notTag.isVisible = true
                                }
                                .setNegativeButton(context.resources.getString(R.string.dialog_cancel)) { dialog, _ ->
                                    dialog.dismiss()
                                }
                                .show()
                        }
                        taskTagGroup.addView(chip)
                    }
                    taskTagGroup.isVisible = true
                } else {
                    notTag.isVisible = true
                }

                if (tags.isNotEmpty()) {
                    for (tag in tags) {
                        val chip = Chip(context)
                        chip.text = tag.tagName
                        chip.isCloseIconVisible = true
                        chip.isCheckable = true
                        chip.isClickable = true
                        chip.setOnCloseIconClickListener {
                            MaterialAlertDialogBuilder(this.context)
                                .setTitle(context.resources.getString(R.string.delete_tag_title))
                                .setMessage(context.getString(R.string.delete_tag_message))
                                .setPositiveButton(context.resources.getString(R.string.dialog_confirm)) { _, _ ->
                                    tagGroup.removeView(chip)
                                    tag.id?.let { id -> removeTag(id) }
                                    if (tagGroup.size <= 0) notTagGeneral.isVisible = true
                                }
                                .setNegativeButton(context.resources.getString(R.string.dialog_cancel)) { dialog, _ ->
                                    dialog.dismiss()
                                }
                                .show()
                        }
                        tagGroup.addView(chip)
                    }
                    tagGroup.isVisible = true
                } else {
                    notTagGeneral.isVisible = true
                }

                btnCancel.setOnClickListener {
                    dialog.dismiss()
                }

                btnSave.setOnClickListener {
                    val tagsSelected = mutableListOf<String>()
                    tagsSelected.clear()
                    if (tagGroup.checkedChipIds.size > 3) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.max_tags_by_task),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (tagGroup.checkedChipIds.size in 1..3) {
                        tagGroup.checkedChipIds.forEach {
                            val chipSelected = viewSheet.findViewById<Chip>(it).text.toString()
                            tagsSelected.add(chipSelected)
                            Log.d("TASKDEBUG", "TAG CHECKED! $chipSelected")
                            updateTags(task, tagsSelected)
                        }
                        dialog.dismiss()
                    } else {
                        taskTagGroup.checkedChipIds.forEach {
                            val chipSelected = viewSheet.findViewById<Chip>(it).text.toString()
                            tagsSelected.add(chipSelected)
                            Log.d("TASKDEBUG", "TAG CHECKED! $chipSelected")
                            updateTags(task, tagsSelected)
                        }
                        dialog.dismiss()
                    }
                    Log.d("TASKDEBUG", "TAG CHECKED! $tagsSelected")
                }

                btnAdd.setOnClickListener {
                    if (txtTag.text.isNotEmpty()) {
                        addTag(txtTag.text.toString())
                        val chip = Chip(context)
                        chip.text = txtTag.text.toString()
                        chip.isCloseIconVisible = true
                        chip.isCheckable = true
                        chip.isClickable = true
                        chip.setOnCloseIconClickListener {
                            MaterialAlertDialogBuilder(this.context)
                                .setTitle(context.resources.getString(R.string.delete_tag_title))
                                .setMessage(context.getString(R.string.delete_tag_message))
                                .setPositiveButton(context.resources.getString(R.string.dialog_confirm)) { _, _ ->
                                    tagGroup.removeView(chip)
                                    getTags()[getTags().size - 1].id?.let { newId -> removeTag(newId) }
                                }
                                .setNegativeButton(context.resources.getString(R.string.dialog_cancel)) { dialog, _ ->
                                    dialog.dismiss()
                                }
                                .show()
                        }
                        tagGroup.addView(chip)
                        tagGroup.isVisible = true
                        notTagGeneral.isVisible = false
                        txtTag.text.clear()
                    } else {
                        txtTag.error = context.getString(R.string.empty_field)
                        txtTag.requestFocus()
                    }
                }

                dialog.setOnDismissListener {
                    showingSheet = false
                }
                dialog.setCancelable(true)
                dialog.setContentView(viewSheet)
                dialog.show()
            }

            binding.btnColor.setOnClickListener {
                if (showingSheet) return@setOnClickListener
                showingSheet = true
                val dialog = BottomSheetDialog(context, R.style.CustomBottomSheetDialog)
                val viewSheet = LayoutInflater.from(context).inflate(R.layout.color_sheet, null)
                dialog.setOnShowListener {
                    val bottomSheetDialog = it as BottomSheetDialog
                    val parentLayout =
                        bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                    parentLayout?.let { _ ->
                        //setupFullHeight(layout)
                    }
                }
                val btnColor0 = viewSheet.findViewById<Button>(R.id.btnColor0)
                val btnColor1 = viewSheet.findViewById<Button>(R.id.btnColor1)
                val btnColor2 = viewSheet.findViewById<Button>(R.id.btnColor2)
                val btnColor3 = viewSheet.findViewById<Button>(R.id.btnColor3)
                val btnColor4 = viewSheet.findViewById<Button>(R.id.btnColor4)

                btnColor0.setOnClickListener { changeItemColor(task, "1"); dialog.dismiss() }
                btnColor1.setOnClickListener { changeItemColor(task, "2"); dialog.dismiss() }
                btnColor2.setOnClickListener { changeItemColor(task, "3"); dialog.dismiss() }
                btnColor3.setOnClickListener { changeItemColor(task, "4"); dialog.dismiss() }
                btnColor4.setOnClickListener { changeItemColor(task, "5"); dialog.dismiss() }
                dialog.setOnDismissListener {
                    showingSheet = false
                }
                dialog.setCancelable(true)
                dialog.setContentView(viewSheet)
                dialog.show()
            }


            binding.btnTime.setOnClickListener {
                if (showingSheet) return@setOnClickListener
                showingSheet = true
                val calendar = Calendar.getInstance()
                val dialog = BottomSheetDialog(context, R.style.CustomBottomSheetDialog)
                val viewSheet = LayoutInflater.from(context).inflate(R.layout.date_sheet, null)
                dialog.setOnShowListener {
                    val bottomSheetDialog = it as BottomSheetDialog
                    val parentLayout =
                        bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                    parentLayout?.let { _ ->
                        //setupFullHeight(layout)
                    }
                }
                day = calendar[Calendar.DAY_OF_MONTH]
                month = calendar[Calendar.MONTH]
                year = calendar[Calendar.YEAR]
                val datePicker = viewSheet.findViewById<DatePicker>(R.id.datePicker)
                val switchHour = viewSheet.findViewById<SwitchMaterial>(R.id.switchHour)
                val txtHour = viewSheet.findViewById<Button>(R.id.txtHour)
                val btnCancel = viewSheet.findViewById<Button>(R.id.btnCancel)
                val btnSave = viewSheet.findViewById<Button>(R.id.btnSave)
                btnSave.setOnClickListener {
                    if (day != 0 && month != 0 && year != 0) {

                        calendar[Calendar.HOUR_OF_DAY] = hour
                        calendar[Calendar.MINUTE] = minute
                        calendar[Calendar.SECOND] = 0
                        calendar[Calendar.MILLISECOND] = 0
                        calendar[Calendar.MONTH] = month
                        calendar[Calendar.DAY_OF_MONTH] = day
                        calendar[Calendar.YEAR] = year

                        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
                        val intent = Intent(context, AlarmReceiver::class.java)

                        val pendingIntent =
                            PendingIntent.getBroadcast(context, task.id!!, intent, PendingIntent.FLAG_NO_CREATE)

                        /*
                        CANCELAR ALARMA
                         val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
                         val pendingIntent = PendingIntent.getService(context, requestId, intent,
                            PendingIntent.FLAG_NO_CREATE)
                        if (pendingIntent != null && alarmManager != null) {
                          alarmManager.cancel(pendingIntent)
                        }

                        HACER QUE LA ALARMA SE REPITA
                        alarmManager.setRepeating(
                            AlarmManager.RTC_WAKEUP,calendar.timeInMillis,
                            AlarmManager.INTERVAL_DAY,pendingIntent
                        )*/

                        alarmManager.set(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            pendingIntent
                        );
                        Toast.makeText(context, "Recordatorio Agregado", Toast.LENGTH_SHORT).show()
                        Toast.makeText(
                            context,
                            "Fecha: $year-${month}-$day Tiempo: $hour : $minute",
                            Toast.LENGTH_SHORT
                        ).show()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(context, "Fecha no Valida", Toast.LENGTH_SHORT).show()
                    }

                }

                btnCancel.setOnClickListener {
                    dialog.dismiss()
                }

                datePicker.setOnDateChangedListener { date, _, _, _ ->
                    day = date.dayOfMonth
                    month = date.month
                    year = date.year

                    val mes = if (month + 1 < 10) {
                        "0${month + 1}"
                    } else {
                        (month + 1).toString()
                    }
                    val dia = if (day < 10) {
                        "0$day"
                    } else {
                        day.toString()
                    }
                    //dateText.text = "$year-${mes}-$dia"
                    Toast.makeText(
                        context,
                        "Fecha: $year-${mes}-$dia Tiempo: $hour : $minute",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                switchHour.setOnCheckedChangeListener { _, isChecked ->
                    txtHour.isVisible = isChecked
                }

                txtHour.setOnClickListener {
                    showTimePicker(txtHour)
                }
                dialog.setOnDismissListener {
                    showingSheet = false
                }
                dialog.setOnDismissListener {
                    showingSheet = false
                }
                dialog.setCancelable(false)
                dialog.setContentView(viewSheet)
                dialog.show()
            }

            binding.taskBody.setOnLongClickListener {
                MaterialAlertDialogBuilder(context)
                    .setTitle(context.resources.getString(R.string.title_delete_dialog))
                    .setMessage(task.title)
                    .setPositiveButton(context.resources.getString(R.string.dialog_confirm)) { _, _ ->
                        Log.d("TaskDebug", "Delete task $task")
                        recyclerViewInterface.onClickDelete(task.id!!, task.state!!)
                        //onLongClick(task.id!!.toInt())
                    }
                    .setNegativeButton(context.resources.getString(R.string.dialog_cancel)) { dialog, _ ->
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

        private fun changeItemColor(task: TaskValues, color: String) {
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
                    "",
                    task.tags
                )
            )
        }

        private fun updateTags(task: TaskValues, tags: List<String>) {
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
                    task.color,
                    task.createdAt,
                    date.toString(),
                    "",
                    tags
                )
            )
        }

        private fun addTag(tag: String) {
            recyclerViewInterface.addTag(
                tag
            )
        }

        private fun removeTag(id: Int) {
            recyclerViewInterface.removeTag(
                id
            )
        }

        private fun getTags(): List<Tag> {
            return recyclerViewInterface.getTags()
        }

        private fun showTimePicker(view: TextView) {
            picker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText(context!!.getString(R.string.set_alarm_title))
                .build()
            val ft = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            picker.show(ft, "WooTask")

            picker.addOnPositiveButtonClickListener {
                selectedTime = if (picker.hour > 12) {
                    String.format("%02d", picker.hour - 12) + " : " + String.format(
                        "%02d",
                        picker.minute
                    ) + "PM"
                } else {
                    String.format("%02d", picker.hour) + " : " + String.format(
                        "%02d",
                        picker.minute
                    ) + "AM"
                }
                hour = picker.hour
                minute = picker.minute
                view.text = selectedTime
            }
        }
    }


}