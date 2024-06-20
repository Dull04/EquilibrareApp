package com.example.equilibrareapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.equilibrareapp.database.user.UserDatabase
import com.example.equilibrareapp.databinding.ActivityLandingBinding
import com.example.equilibrareapp.model.Article
import com.example.equilibrareapp.model.User
import com.example.equilibrareapp.preference.PreferenceHelper
import com.example.equilibrareapp.repository.UserRepository
import com.example.equilibrareapp.viewmodel.ArticleViewModel
import com.example.equilibrareapp.viewmodel.UserViewModel
import com.example.equilibrareapp.viewmodel.UserViewModelFactory

class LandingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLandingBinding
    private lateinit var preferenceHelper: PreferenceHelper
    private lateinit var userViewModel: UserViewModel
    private lateinit var articleViewModel: ArticleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        preferenceHelper = PreferenceHelper(this)

        // Create ArticleViewModel instance with correct parameters
        articleViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application)).get(ArticleViewModel::class.java)

        articleViewModel.article.observe(this) { article ->
            article?.let { displayArticle(it) }
        }
        fetchUserProfile()
        binding.pbCard.visibility = View.VISIBLE
        articleViewModel.checkAndFetchDailyNews(BuildConfig.KEY_NEWS)
        binding.pbCard.visibility = View.GONE
        setupOnClickListener()
    }

    private fun displayArticle(article: Article) {
        with(binding) {
            tvNewsTitle.text = article.title
            tvDescNews.text = article.description
            Glide.with(root.context)
                .load(article.urlToImage)
                .into(ivNews)
        }

        binding.cvTips.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
            startActivity(browserIntent)
        }
    }

    private fun setupOnClickListener() {
        binding.btnProfile.setOnClickListener {
            startActivity(Intent(this@LandingActivity, ProfileActivity::class.java))
        }

        binding.btnWrite.setOnClickListener {
            startActivity(Intent(this@LandingActivity, MainActivity::class.java))
        }
    }

    private fun fetchUserProfile() {
        showLoading(true)
        val userDao = UserDatabase.getDatabase(application).userDao()
        val repository = UserRepository(userDao)
        val factory = UserViewModelFactory(repository)

        userViewModel = ViewModelProvider(this, factory).get(UserViewModel::class.java)
        preferenceHelper.getUserUid()?.let {
            userViewModel.getUserByUid(it){ user ->
                user?.let{
                    displayUserData(user)
                }
            }
        }
        showLoading(false)
    }

    private fun displayUserData(user: User) {
        binding.tvWelcome.text = "Hi, Welcome Back ${user.displayName}!"
        Glide.with(this)
            .load(user.photoURL)
            .into(binding.btnProfile)
    }
    private fun showLoading(isLoading: Boolean) {
        with(binding) {
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            btnWrite.isEnabled = !isLoading
        }
    }
}
