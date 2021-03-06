package com.example.swoosh.ui.dialog_fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import com.example.swoosh.R
import com.example.swoosh.data.Repository
import com.example.swoosh.data.model.FBItem
import com.example.swoosh.data.model.Todolist
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.todo_action_dialog.*

class TodoActionsDialog(
        private val todo: Todolist.Todo,
        private val todolist: Todolist,
        private val boardID: String
) : BottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.todo_action_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        delete_todo_btn.setOnClickListener {

            Repository.deleteFromFBItem(
                    FBItem.parseToFBItem(todolist),
                    FBItem.Containable.parseToContainable(todo),
                    boardID
            )

            Toast.makeText(requireContext(), "Todo Deleted", Toast.LENGTH_SHORT).show()

            dismiss()
        }
    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }

    companion object{
        val TAG = "bottom_dialog_todo_deletion"
    }
}