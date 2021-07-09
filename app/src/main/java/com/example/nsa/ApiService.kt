package com.example.nsa

import com.example.nsa.model.ResponseWrapper
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("search/v2/articlesearch.json")
    fun getResponse(
        @Query("q") query: String?,
        @Query("begin_date") beginDate: String?,
        @Query("sort") sort: String?,
        @Query("fq") newsDesk: String?,
        @Query("page") page: Int,
        @Query("api-key") key: String
    ): Call<ResponseWrapper>
}