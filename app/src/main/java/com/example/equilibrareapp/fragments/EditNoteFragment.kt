package com.example.equilibrareapp.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.equilibrareapp.MainActivity
import com.example.equilibrareapp.R
import com.example.equilibrareapp.databinding.FragmentEditNoteBinding
import com.example.equilibrareapp.model.Diary
import com.example.equilibrareapp.preference.PreferenceHelper
import com.example.equilibrareapp.service.ApiConfig.Companion.getApiService
import com.example.equilibrareapp.service.PredictRequest
import com.example.equilibrareapp.service.PredictResponse
import com.example.equilibrareapp.viewmodel.DiaryViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class EditNoteFragment : Fragment(R.layout.fragment_edit_note), MenuProvider {

    private lateinit var preferenceHelper: PreferenceHelper

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
        editNoteBinding = FragmentEditNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun getCurrentDateAsString(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferenceHelper = PreferenceHelper(requireContext())

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

            if (diaryTitle.isNotEmpty()) {
                val diary = Diary(currentDiary.id, diaryTitle, diaryDesc, diaryDate)
                diaryViewModel.updateDiary(diary)
                view.findNavController().popBackStack(R.id.homeFragment, false)
            } else {
                Toast.makeText(context, "Please Enter Diary Title", Toast.LENGTH_SHORT).show()
            }
        }
        binding.ButtonHasilAnalisis.setOnClickListener {
            showLoading(true)

            val diaryTitle = binding.editNoteTitle.text.toString()
            val diaryDesc = binding.editNoteDesc.text.toString()
            val predReq = PredictRequest(diaryTitle, diaryDesc)
            val token = "Bearer ${preferenceHelper.getUserToken().toString()}"

            val client = getApiService().predict(token, predReq)
            client.enqueue(object : Callback<PredictResponse> {
                override fun onResponse(
                    call: Call<PredictResponse>,
                    response: Response<PredictResponse>
                ) {
                    showLoading(false)
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            val pred = "%.2f%%".format(responseBody.prediction * 100)
                            val label = responseBody.label
                            binding.tvResult.setText("Hasil prediksi menunjukan: $label \nDengan persentase: $pred")
                            binding.ButtonHasilAnalisis.visibility = View.GONE
                            binding.tvResult.visibility = View.VISIBLE
                            Toast.makeText(
                                requireContext(),
                                "Prediksi Berhasil",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Prediksi Gagal: ${responseBody?.message ?: "Unknown error"}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Log.e("Predict", "Token: ${preferenceHelper.getUserToken().toString()}")
                        Toast.makeText(
                            requireContext(),
                            "Prediksi Gagal: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<PredictResponse>, t: Throwable) {
                    showLoading(false)
                    Toast.makeText(
                        requireContext(),
                        "Prediksi Gagal: ${t.message.toString()}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    Log.e("TAG", "onFailure: ${t.message}")
                }
            })
        }
    }

    private fun deleteDiary() {
        AlertDialog.Builder(activity).apply {
            setTitle("Delete Diary")
            setMessage("Do You Want To Delete This Diary ?")
            setPositiveButton("Delete") { _, _ ->
                diaryViewModel.deleteDiary(currentDiary)
                Toast.makeText(context, "Diary Deleted", Toast.LENGTH_SHORT).show()
                view?.findNavController()?.popBackStack(R.id.homeFragment, false)
            }
            setNegativeButton("Cancel", null)
        }.create().show()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.menu_edit_note, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.deleteMenu -> {
                deleteDiary()
                true
            }

            else -> false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        editNoteBinding = null
    }

    private fun showLoading(isLoading: Boolean) {
        with(binding) {
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            ButtonHasilAnalisis.isEnabled = !isLoading
            editNoteTitle.isEnabled = !isLoading
            editNoteDesc.isEnabled = !isLoading
            editNoteFab.isEnabled = !isLoading
        }
    }

}