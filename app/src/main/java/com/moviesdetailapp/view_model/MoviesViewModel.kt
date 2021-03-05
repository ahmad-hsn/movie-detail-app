package com.moviesdetailapp.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.moviesdetailapp.helper.Resource
import com.moviesdetailapp.model.MovieDetailResponse
import com.moviesdetailapp.model.MovieModel
import com.moviesdetailapp.model.MovieResponse
import com.moviesdetailapp.network.ApiClient
import com.moviesdetailapp.network.ApiRoute
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MoviesViewModel: ViewModel() {
    private val movies = MutableLiveData<Resource<MovieResponse>>()
    private val movieDetail = MutableLiveData<Resource<MovieDetailResponse>>()
    private var apiRoute: ApiRoute = ApiClient().getAPIClient()

    fun fetchMoviesList(page: Int) {
        movies.postValue(Resource.loading(null))
        val call: Call<MovieResponse> = apiRoute.getMoviesData(page)
        call.enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>?, response: Response<MovieResponse?>) {
                movies.postValue(Resource.success(response.body()))
            }

            override fun onFailure(call: Call<MovieResponse?>?, t: Throwable) {
                movies.postValue(Resource.error(t.message ?: "Something went wrong", null))
            }
        })
    }

    fun fetchMovieDetail(movieId: Int) {
        movieDetail.postValue(Resource.loading(null))
        val call: Call<MovieDetailResponse> = apiRoute.getMovieDetail(movieId)
        call.enqueue(object : Callback<MovieDetailResponse> {
            override fun onResponse(call: Call<MovieDetailResponse>?, response: Response<MovieDetailResponse?>) {
                movieDetail.postValue(Resource.success(response.body()))
            }

            override fun onFailure(call: Call<MovieDetailResponse?>?, t: Throwable) {
                movieDetail.postValue(Resource.error(t.message ?: "Something went wrong", null))
            }
        })
    }

    fun getMovies(): LiveData<Resource<MovieResponse>> {
        return movies
    }

    fun getMovieDetail(): LiveData<Resource<MovieDetailResponse>> {
        return movieDetail
    }
}