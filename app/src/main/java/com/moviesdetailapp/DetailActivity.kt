package com.moviesdetailapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.moviesdetailapp.fragment.PagerFragment
import com.moviesdetailapp.helper.Constants
import com.moviesdetailapp.helper.GlideHelper
import com.moviesdetailapp.helper.Status
import com.moviesdetailapp.helper.Utility
import com.moviesdetailapp.model.MovieDetailResponse
import com.moviesdetailapp.model.MovieModel
import com.moviesdetailapp.view_model.MoviesViewModel


class DetailActivity : BaseActivity() {
    private lateinit var posterImg: ImageView
    private var movieId: Int? = null
    private lateinit var poster: ImageView
    private lateinit var movieName: TextView
    private lateinit var genre: TextView
    private lateinit var tagLine: TextView
    private lateinit var homePageUrl: TextView
    private lateinit var body: TextView
    private lateinit var year: TextView
    private lateinit var rating: TextView
    private lateinit var progressBar: ProgressBar

    private lateinit var moviesViewModel: MoviesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        movieId = intent.extras?.getInt(Constants.DATA_KEY)

        posterImg = findViewById(R.id.poster_img)
        poster = findViewById(R.id.poster)
        movieName = findViewById(R.id.movie_name)
        genre = findViewById(R.id.genre)
        tagLine = findViewById(R.id.tag_line)
        homePageUrl = findViewById(R.id.home_page_url)
        body = findViewById(R.id.body)
        year = findViewById(R.id.year)
        rating = findViewById(R.id.rating)
        progressBar = findViewById(R.id.progressBar)

        setupViewModel()
    }

    fun loadData(movieDetail: MovieDetailResponse) {
        val genreBuilder = StringBuilder()
        movieDetail.genres?.let {
            for(value in movieDetail.genres!!) {
                genreBuilder.append(value.name).append("   ")
            }
        }

        year.text = Utility.getFormattedDate(movieDetail.release_date)
        movieName.text = movieDetail.original_title
        genre.text = genreBuilder
        tagLine.text = movieDetail.tagline
        rating.visibility = View.VISIBLE
        rating.text = "${movieDetail.vote_average}"
        homePageUrl.text = movieDetail.homepage
        body.text = movieDetail.overview

        GlideHelper.loadImg(this, Constants.IMG_BASE_URL + movieDetail.poster_path, poster, null)
        GlideHelper.loadImg(this, Constants.IMG_BASE_URL + movieDetail.backdrop_path, posterImg, null)

        progressBar.visibility = View.GONE
    }

    fun setupViewModel() {
        moviesViewModel = ViewModelProvider(this).get(MoviesViewModel::class.java)
        moviesViewModel.getMovieDetail().observe(this, {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let {
                        loadData(it)
                    }
                }
                Status.LOADING -> {
                    progressBar.visibility = View.VISIBLE
                }
                Status.FAILED -> {
                    progressBar.visibility = View.GONE
                    showSnackBar(findViewById(android.R.id.content), it.message!!)
                }
            }
        })

        if(isOnline()) moviesViewModel.fetchMovieDetail(movieId!!)
    }

    override fun onStart() {
        super.onStart()

        val filter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(networkBroadcast, filter)
    }

    override fun onStop() {
        super.onStop()

        unregisterReceiver(networkBroadcast)
    }

    private val networkBroadcast = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (!isOnline()) {
                showSnackBar(findViewById(android.R.id.content), resources.getString(R.string.no_internet_msg), Snackbar.LENGTH_INDEFINITE)
            } else {
                dismissSnackbar()
            }
        }
    }
}