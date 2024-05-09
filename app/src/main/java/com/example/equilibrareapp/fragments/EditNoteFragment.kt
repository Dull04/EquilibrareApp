package com.example.equilibrareapp.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.equilibrareapp.MainActivity
import com.example.equilibrareapp.R
import com.example.equilibrareapp.databinding.FragmentEditNoteBinding
import com.example.equilibrareapp.model.Diary
import com.example.equilibrareapp.viewmodel.DiaryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class EditNoteFragment : Fragment(R.layout.fragment_edit_note), MenuProvider {

    private var editNoteBinding: FragmentEditNoteBinding? = null
    private val binding get() = editNoteBinding!!

    private lateinit var diaryViewModel: DiaryViewModel
    private lateinit var currentDiary: Diary

    private val args: EditNoteFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        editNoteBinding = FragmentEditNoteBinding.inflate(inflater,container,false)
        return binding.root
    }

    private fun getCurrentDateAsString(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        diaryViewModel = (activity as MainActivity).diaryViewModel
        currentDiary = args.diary!!

        binding.editNoteTitle.setText(currentDiary.noteTitle)
        binding.editNoteDesc.setText(currentDiary.noteDesc)

        binding.editNoteFab.setOnClickListener {
            val diaryTitle = binding.editNoteTitle.text.toString().trim()
            val diaryDesc = binding.editNoteDesc.text.toString().trim()
            val diaryDate = getCurrentDateAsString()

            if(diaryTitle.isNotEmpty()){
                val diary = Diary(currentDiary.id, diaryTitle, diaryDesc, diaryDate)
                diaryViewModel.updateDiary(diary)
                view.findNavController().popBackStack(R.id.homeFragment,false)
            } else{
                Toast.makeText(context, "Please Enter Diary Title", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteDiary(){
        AlertDialog.Builder(activity).apply {
            setTitle("Delete Diary")
            setMessage("Do You Want To Delete This Diary ?")
            setPositiveButton("Delete"){_,_ ->
                diaryViewModel.deleteDiary(currentDiary)
                Toast.makeText(context, "Diary Deleted", Toast.LENGTH_SHORT).show()
                view?.findNavController()?.popBackStack(R.id.homeFragment, false)
            }
            setNegativeButton("Cancel",null)
        }.create().show()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.menu_edit_note,menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when(menuItem.itemId){
            R.id.deleteMenu -> {
                deleteDiary()
                true
            } else -> false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        editNoteBinding = null
    }

}