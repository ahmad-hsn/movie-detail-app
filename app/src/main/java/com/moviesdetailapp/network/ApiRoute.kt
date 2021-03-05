package com.moviesdetailapp.network

import com.moviesdetailapp.helper.Constants
import com.moviesdetailapp.model.MovieDetailResponse
import com.moviesdetailapp.model.MovieModel
import com.moviesdetailapp.model.MovieResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiRoute {
    @GET("movie/popular?api_key=${Constants.API_KEY}&language=en-US&append_to_response=videos,images")
    fun getMoviesData(@Query("page") page: Int): Call<MovieResponse>

    @GET("movie/{movie_id}?page=1&api_key=${Constants.API_KEY}&language=en-US&append_to_response=videos,images") // movies detail
    fun getMovieDetail(@Path("movie_id") movie_id: Int): Call<MovieDetailResponse>
}