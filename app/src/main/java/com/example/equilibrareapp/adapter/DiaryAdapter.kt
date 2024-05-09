package com.example.equilibrareapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.equilibrareapp.databinding.NoteLayoutBinding
import com.example.equilibrareapp.fragments.HomeFragmentDirections
import com.example.equilibrareapp.model.Diary


class DiaryAdapter : RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder>(){

    class DiaryViewHolder(val itemBinding: NoteLayoutBinding): RecyclerView.ViewHolder(itemBinding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Diary>(){
        override fun areItemsTheSame(oldItem: Diary, newItem: Diary): Boolean {
            return oldItem.id == newItem.id &&
                    oldItem.noteDesc == newItem.noteDesc &&
                    oldItem.noteTitle == newItem.noteTitle
        }

        override fun areContentsTheSame(oldItem: Diary, newItem: Diary): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, differCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        return DiaryViewHolder(
            NoteLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        val currentDiary = differ.currentList[position]
        holder.itemBinding.noteTitle.text = currentDiary.noteTitle
        holder.itemBinding.noteDesc.text = currentDiary.noteDesc
        holder.itemBinding.noteDate.text = currentDiary.noteDate

        holder.itemView.setOnClickListener {
            val direction = HomeFragmentDirections.actionHomeFragmentToEditNoteFragment(currentDiary)
            it.findNavController().navigate(direction)
        }

    }

}