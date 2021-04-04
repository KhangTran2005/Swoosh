package com.example.swoosh.ui.notes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.swoosh.R
import com.example.swoosh.data.model.NoteCollection
import kotlinx.android.synthetic.main.fragment_note.*

class NoteFragment(private val noteCollection: NoteCollection) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noteCol_name_tv.text = noteCollection.name
    }

    override fun toString(): String {
        return noteCollection.name
    }
}