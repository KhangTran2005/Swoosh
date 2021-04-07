package com.example.swoosh.ui.board_view

import android.graphics.Color
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.transition.TransitionManager
import androidx.viewpager2.widget.ViewPager2
import com.example.swoosh.R
import com.example.swoosh.data.Repository
import com.example.swoosh.data.model.Board
import com.example.swoosh.data.model.BoardItem
import com.example.swoosh.ui.base.BoardItemFragment
import com.example.swoosh.ui.dialog_fragments.BoardItemCreationDialog
import com.example.swoosh.utils.BoardUtils
import com.example.swoosh.utils.PolySeri
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.transition.MaterialContainerTransform
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.board_item_overflow.*
import kotlinx.android.synthetic.main.fragment_board_view.*
import kotlinx.android.synthetic.main.fragment_note.*
import kotlinx.android.synthetic.main.fragment_todolist.*
import kotlinx.serialization.decodeFromString
import java.util.*
import kotlin.collections.ArrayList

class BoardView : Fragment() {

    private val args: BoardViewArgs by navArgs()
    private val board: Board by lazy{
        PolySeri.json.decodeFromString(args.board)
    }
    private lateinit var viewModel: BoardViewViewModel
    private val valueEventListener : ValueEventListener by lazy{
        object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                viewModel.fetchBoardItems()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        viewModel = BoardViewModelFactory(board.id).create(BoardViewViewModel::class.java)

        return inflater.inflate(R.layout.fragment_board_view, container, false)
    }

    override fun onResume() {
        super.onResume()
        Repository.getItemRef(board.id).addValueEventListener(valueEventListener)
    }

    override fun onStop() {
        super.onStop()
        Repository.getItemRef(board.id).removeEventListener(valueEventListener)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        board_view_toolbar.setupWithNavController(findNavController())
        board_view_toolbar.title = board.name

        viewModel.boardItems.observe(viewLifecycleOwner) {
            updateBoardFragments(it)
        }
    }

    fun pushTodolist(){
        BoardItemCreationDialog(true, board).show(childFragmentManager, BoardItemCreationDialog.TAG)
    }

    fun pushNoteCollection(){
        BoardItemCreationDialog(false, board).show(childFragmentManager, BoardItemCreationDialog.TAG)
    }

    private fun updateBoardFragments(boardItems: SortedMap<String, BoardItem>?){
        val boardItemsFragments = boardItems?.let { BoardUtils.getBoardItemFragments(it, board.id) }
                ?: arrayListOf()

        if (board_content_viewpager.adapter != null){
            (board_content_viewpager.adapter as BoardPagerAdapter).submitList(boardItemsFragments)
        }
        else{
            board_content_viewpager.adapter = BoardPagerAdapter(requireActivity()).apply { submitList(boardItemsFragments) }
            TabLayoutMediator(board_view_dot_indicator, board_content_viewpager){_, _ -> }.attach()
            board_view_progress_bar.isVisible = false
        }

        //funny debug things
        Log.d("debug", "updateBoardFragments: ${board_content_viewpager.currentItem} out of ${(board_content_viewpager.adapter?.itemCount ?: 0)}")
        Log.d("debug", "this fragment is: ${(board_content_viewpager.adapter as BoardPagerAdapter).fragments[board_content_viewpager.currentItem]}")
        if (board_content_viewpager.currentItem >= 1){
            Log.d("debug", "previous fragment is: ${(board_content_viewpager.adapter as BoardPagerAdapter).fragments[board_content_viewpager.currentItem - 1]}")
        }
    }

}