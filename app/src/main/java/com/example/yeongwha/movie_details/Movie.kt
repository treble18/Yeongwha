package com.example.yeongwha.movie_details

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.yeongwha.R
import com.example.yeongwha.data.api.POSTER_BASE_URL
import com.example.yeongwha.data.api.TMDBClient
import com.example.yeongwha.data.api.TMDBInterface
import com.example.yeongwha.data.repository.NetworkState
import com.example.yeongwha.data.value_object.MovieDetails
import kotlinx.android.synthetic.main.activity_movie_details.*
import java.text.NumberFormat
import java.util.*

class Movie : AppCompatActivity() {

    private lateinit var viewModel: ViewModel
    private lateinit var movieRepository: MovieDetailsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        val movieId: Int = intent.getIntExtra("id",1)

        val apiService : TMDBInterface = TMDBClient.getClient()
        movieRepository = MovieDetailsRepository(apiService)

        viewModel = getViewModel(movieId)

        viewModel.movieDetails.observe(this, Observer {
            bindUI(it)
        })

        viewModel.networkState.observe(this, Observer {
            progress_bar.visibility = if (it == NetworkState.LOADING) View.VISIBLE else View.GONE
            txt_error.visibility = if (it == NetworkState.ERROR) View.VISIBLE else View.GONE

        })
    }

    fun bindUI( it: MovieDetails){
        movie_title.text = it.title
        movie_release_date.text = it.releaseDate
        movie_rating.text = it.rating.toString()
        movie_runtime.text = it.runtime.toString() + " minutes"
        movie_overview.text = it.overview

        val formatCurrency = NumberFormat.getCurrencyInstance(Locale.US)
        movie_budget.text = formatCurrency.format(it.budget)
        movie_revenue.text = formatCurrency.format(it.revenue)

        val moviePosterURL = POSTER_BASE_URL + it.posterPath
        Glide.with(this)
            .load(moviePosterURL)
            .into(iv_movie_poster);

    }

    private fun getViewModel(movieId:Int): ViewModel {
        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ViewModel(movieRepository,movieId) as T
            }
        })[ViewModel::class.java]
    }
}