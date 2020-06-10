package com.nicow.newplayer.logic.retrofit.musicapi

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TopListService {

    @GET("/top/list")
    fun getTopList(@Query("idx") topListIdx: String): Call<Map<Any, Any>>

}