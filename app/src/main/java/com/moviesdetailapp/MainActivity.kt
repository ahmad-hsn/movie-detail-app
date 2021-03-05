package com.moviesdetailapp

import android.content.*
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.material.snackbar.Snackbar
import com.jackandphantom.blurimage.BlurImage
import com.moviesdetailapp.fragment.PagerFragment
import com.moviesdetailapp.helper.Constants
import com.moviesdetailapp.helper.Status
import com.moviesdetailapp.helper.Utility
import com.moviesdetailapp.interfaces.OnBitmapLoad
import com.moviesdetailapp.interfaces.OnFilterItemSelectListener
import com.moviesdetailapp.interfaces.OnRecyclerItemClickListener
import com.moviesdetailapp.model.MovieModel
import com.moviesdetailapp.view_model.MoviesViewModel
import java.util.*
import com.moviesdetailapp.helper.Filter
import kotlin.Comparator
import kotlin.collections.ArrayList


class MainActivity : BaseActivity(), OnRecyclerItemClickListener, OnBitmapLoad,
    ViewPager.OnPageChangeListener, View.OnClickListener, OnFilterItemSelectListener {
    private lateinit var moviesViewModel: MoviesViewModel
    private lateinit var movieList: ArrayList<MovieModel>
    private var pagerAdapter: ScreenSlidePagerAdapter? = null
    private var filterType: Filter = Filter.NONE

    private lateinit var progressBar: ProgressBar
    private lateinit var imageView: ImageView
    private lateinit var titleTextView: TextView
    private lateinit var ratingYearTextView: TextView
    private lateinit var releaseDateTextView: TextView
    private lateinit var detailTextView: TextView
    private lateinit var linearLay: LinearLayout
    private lateinit var nextArrow: ImageView
    private lateinit var viewPager: ViewPager

    private var pagerPosition: Int = 0
    private var filterOption = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getSupportActionBar()?.setIcon(R.drawable.ic_filter)

        init()
    }

    fun init() {
        progressBar = findViewById(R.id.progress_bar)
        viewPager = findViewById(R.id.viewPager)
        imageView = findViewById(R.id.imageView)
        titleTextView = findViewById(R.id.title)
        ratingYearTextView = findViewById(R.id.rating_year)
        releaseDateTextView = findViewById(R.id.date_txt)
        detailTextView = findViewById(R.id.detail)
        linearLay = findViewById(R.id.linear_lay)
        nextArrow = findViewById(R.id.next_arrow)

        viewPager.setPadding(60, 0, 60, 0)
        viewPager.clipToPadding = false
        viewPager.pageMargin = 30
        ratingYearTextView.visibility = View.GONE

        movieList = ArrayList()

        viewPager.addOnPageChangeListener(this)
        linearLay.setOnClickListener(this)
        nextArrow.setOnClickListener(this)
        detailTextView.setOnClickListener(this)

        setupViewModel()
    }

    fun setupViewModel() {
        moviesViewModel = ViewModelProvider(this).get(MoviesViewModel::class.java)
        moviesViewModel.getMovies().observe(this, {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { movies ->
                        renderList(movies.results!!)
                    }
                }
                Status.LOADING -> {
                    activeDeactiveViews(false)
                }
                Status.FAILED -> {
                    activeDeactiveViews(true)
                    showSnackBar(findViewById(android.R.id.content), it.message!!)
                }
            }
        })

        if (isOnline()) moviesViewModel.fetchMoviesList((movieList.size / 20) + 1)
    }

    fun activeDeactiveViews(status: Boolean) {
        ratingYearTextView.visibility = if (status) View.VISIBLE else View.GONE
        nextArrow.visibility = if (status) View.VISIBLE else View.GONE
        progressBar.visibility = if (status) View.GONE else View.VISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.id_filter -> {
                Utility.createFilterDialog(this, arrayListOf("Ascending Order", "Descending Order"), this)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun renderList(movies: ArrayList<MovieModel>) {
        if (movies.isEmpty()) {
            showSnackBar(
                findViewById(android.R.id.content),
                resources.getString(R.string.no_data_found_msg)
            )
        }

        movieList.addAll(movies)
        filterData(movieList)

        if (pagerAdapter == null) {
            pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
            viewPager.adapter = pagerAdapter
            loadData(0)
        } else {
            pagerAdapter!!.notifyDataSetChanged()
        }

        activeDeactiveViews(true)
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
                showSnackBar(
                    findViewById(android.R.id.content),
                    resources.getString(R.string.no_internet_msg),
                    Snackbar.LENGTH_INDEFINITE
                )
            } else {
                if (movieList.isEmpty() && progressBar.visibility != View.VISIBLE) moviesViewModel.fetchMoviesList(
                    (movieList.size / 20) + 1
                )
                dismissSnackbar()
            }
        }
    }

    override fun itemClicked(position: Int) {
        showSnackBar(findViewById(android.R.id.content), "clicked")
    }

    override fun bitmapLoaded(bitmap: Bitmap) {
        BlurImage.with(this).load(bitmap).intensity(10.0f).Async(true).into(imageView)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        loadData(position)
        if (position == movieList.size - 1) {
            if (isOnline()) moviesViewModel.fetchMoviesList((movieList.size / 20) + 1)
        }
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    fun loadData(position: Int) {
        pagerPosition = position

        titleTextView.text = movieList.get(position).original_title
        ratingYearTextView.text = movieList.get(position).vote_average.toString()
        releaseDateTextView.text = Utility.getFormattedDate(movieList.get(position).release_date)
        detailTextView.text = movieList.get(position).overview
    }

    inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(
        fm,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {
        override fun getCount(): Int = movieList.size

        override fun getItem(position: Int): Fragment {
            val obj = PagerFragment()
            obj.onBitmapLoaded(this@MainActivity)
            val bundle = Bundle()
            bundle.putString(Constants.DATA_KEY, movieList.get(position).backdrop_path)
            obj.arguments = bundle

            return obj
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.linear_lay, R.id.next_arrow, R.id.detail -> {
                val obj = Intent(this, DetailActivity::class.java)
                val bundle = Bundle()
                bundle.putSerializable(Constants.DATA_KEY, movieList.get(pagerPosition).id)
                obj.putExtras(bundle)
                startActivity(obj)
            }
        }
    }

    override fun onFilterItemSelected(position: Int, item: String) {
        if(position == 0) {
            filterType = Filter.ASCE
        } else {
            filterType = Filter.DESC
        }

        filterData(movieList)
        pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
        viewPager.adapter = pagerAdapter
        loadData(0)
    }

    fun filterData(aList: ArrayList<MovieModel>) {
        if (filterType != Filter.NONE) {
            aList.sortWith(object : Comparator<MovieModel> {
                override fun compare(o1: MovieModel?, o2: MovieModel?): Int {
                    if (filterType == Filter.ASCE) {
                        return o1?.release_date?.compareTo(o2?.release_date!!)!!
                    } else {
                        return o2?.release_date?.compareTo(o1?.release_date!!)!!
                    }
                }
            })
        }
    }
}