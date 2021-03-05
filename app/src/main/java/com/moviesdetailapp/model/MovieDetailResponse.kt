package com.moviesdetailapp.model

data class MovieDetailResponse(val adult: Boolean? = false, val backdrop_path: String? = "", val genres: ArrayList<GenreData>? = null,
                               val id: Int, val original_title: String? = "", val overview: String? = "", val poster_path: String? = "",
                                val release_date: String? = "", val tagline: String? = "", val vote_average: Double? = 0.0,
                                val homepage: String? = "")
