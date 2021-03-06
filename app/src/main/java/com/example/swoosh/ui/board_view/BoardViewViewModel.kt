package com.example.swoosh.ui.board_view

import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.swoosh.data.Repository
import com.example.swoosh.data.model.Board
import com.example.swoosh.data.model.BoardItem
import com.example.swoosh.data.model.FBItem
import com.example.swoosh.utils.Status
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*
import javax.net.ssl.SSLEngineResult
import kotlin.collections.HashMap

class BoardViewViewModel(private val boardID: String) : ViewModel() {
    private val _boardItems = MutableLiveData<SortedMap<String, BoardItem>>()
    private val _status = MutableLiveData(Status.LOADING)
    private val _members = MutableLiveData<HashMap<String, Board.Member>>()

    val boardItems : LiveData<SortedMap<String, BoardItem>>
        get() = _boardItems
    val status : LiveData<Status>
        get() = _status
    val members: LiveData<HashMap<String, Board.Member>>
        get() = _members

    fun fetchMembers(){
        Repository.getBoardsRef().child(boardID)
                .child("members")
                .get().addOnSuccessListener {
                    val to = object: GenericTypeIndicator<HashMap<String, Board.Member>>(){}

                    val membersQuery = it.getValue(to)

                    membersQuery?.let { members ->
                        _members.value = members
                    }
                }
    }

    fun fetchBoardItems(){
        _status.value = Status.LOADING
        Firebase.database.reference
                .child("itemStore")
                .child(boardID)
                .get().addOnSuccessListener {
                    if (it.exists()){
                        val to = object: GenericTypeIndicator<Map<String, FBItem>>(){}

                        val itemStoreQuery = it.getValue(to)
                        val itemStore = itemStoreQuery?.toSortedMap()

                        itemStore?.let {
                            _boardItems.value = Board.getActualItems(itemStore)
                            _status.value = Status.SUCCESS
                        }
                    }
                    else {
                        _status.value = Status.EMPTY
                    }
                }
                .addOnFailureListener{
                    _status.value = Status.FAILED
                }
    }
}

class BoardViewModelFactory(private val boardID: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BoardViewViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BoardViewViewModel(boardID) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}