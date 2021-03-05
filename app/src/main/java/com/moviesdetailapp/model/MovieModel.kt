package com.moviesdetailapp.model


data class MovieModel(
    val title: String? = "",
    val release_date: String? = "",
    val poster_path: String? = "",
    val id: Int? = 0,
    val vote_average: Double? = 0.0,
    val original_title: String? = "",
    val backdrop_path: String? = "",
    val overview: String? = "",
) : Comparable<MovieModel> {
    override fun compareTo(other: MovieModel): Int {
        return release_date?.compareTo(other.release_date!!)!!
    }
}
