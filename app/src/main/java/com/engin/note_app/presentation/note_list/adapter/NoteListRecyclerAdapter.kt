package com.engin.note_app.presentation.note_list.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.engin.note_app.databinding.ItemNoteListBinding
import com.engin.note_app.domain.model.Note
import com.engin.note_app.presentation.note_list.NoteListListener

class NoteListRecyclerAdapter(
    private var list : List<Note>,
    private var listener : NoteListListener
) : RecyclerView.Adapter<NoteListRecyclerAdapter.NoteListViewHolder>() {

    inner class NoteListViewHolder( val binding : ItemNoteListBinding) :  RecyclerView.ViewHolder( binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteListViewHolder {
        val binding = ItemNoteListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteListViewHolder, position: Int) {
        val currentItem = list[position]
        holder.binding.item = currentItem
        holder.binding.listener = listener
    }

    override fun getItemCount(): Int  = list.size

    /**
     *
     * Updating all item with [newList]
     *
     */
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList : List<Note>){
        list = newList
        notifyDataSetChanged()
    }
}