package com.example.equilibrareapp

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.equilibrareapp.database.DiaryDatabase
import com.example.equilibrareapp.repository.DiaryRepository
import com.example.equilibrareapp.viewmodel.DiaryViewModel
import com.example.equilibrareapp.viewmodel.DiaryViewModelFactory

class MainActivity : AppCompatActivity() {
    lateinit var  diaryViewModel: DiaryViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupViewModel()

    }

    private fun setupViewModel(){
        val diaryRepository = DiaryRepository(DiaryDatabase(this))
        val viewModelProviderFactory = DiaryViewModelFactory(application, diaryRepository)
        diaryViewModel = ViewModelProvider(this, viewModelProviderFactory)[DiaryViewModel::class.java]
    }
}