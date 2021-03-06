package com.example.swoosh.ui.dialog_fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.swoosh.R
import com.example.swoosh.data.Repository
import com.example.swoosh.data.model.Board
import com.example.swoosh.data.model.FBItem
import com.example.swoosh.data.model.Todolist
import kotlinx.android.synthetic.main.board_creation_dialog.*
import kotlinx.android.synthetic.main.board_creation_dialog.create_board_btn
import kotlinx.android.synthetic.main.todo_creation_dialog.*

class TodoCreationDialog(
        private val todolist: Todolist,
        private val boardID: String
) : DialogFragment() {
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
            dialog?.window?.setWindowAnimations(R.style.dialog_animation)
        }

        return inflater.inflate(R.layout.todo_creation_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        create_todo_btn.setOnClickListener{
            val todoName = todo_name_et.text.toString()
            val dueDate = due_date_et.text.toString()
            val selectedID = radio_group_todo_creation.checkedRadioButtonId
            val priority = Todolist.parsePriority(selectedID)

            if (TextUtils.isEmpty(todoName) || TextUtils.isEmpty(dueDate)){
                Toast.makeText(requireContext(), "Empty Fields!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val todo = Todolist.Todo(todoName, dueDate, priority)

            Repository.pushToFBItem(FBItem.parseToFBItem(todolist), FBItem.Containable.parseToContainable(todo), boardID)

            dismiss()
        }

    }

    companion object{
        val TAG = "todo_creation_dialog"
    }
}