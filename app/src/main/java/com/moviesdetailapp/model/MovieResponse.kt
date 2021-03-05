package com.moviesdetailapp.model

data class MovieResponse(val page: Int? = 0, val results: ArrayList<MovieModel>? = null, val total_pages: Int? = 0,
                         val total_results: Int? = 0)
