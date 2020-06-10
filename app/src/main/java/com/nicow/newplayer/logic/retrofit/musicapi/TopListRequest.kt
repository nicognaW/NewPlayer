package com.nicow.newplayer.logic.retrofit.musicapi

import com.google.gson.internal.LinkedTreeMap
import com.nicow.newplayer.data.TopListMusic
import com.nicow.newplayer.logic.Repository
import com.nicow.newplayer.logic.retrofit.abstract.AbstractTopListRequest
import com.nicow.newplayer.logic.retrofit.musicapi.ServiceCreator.create
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat

class TopListRequest : AbstractTopListRequest() {
    private val service = create(TopListService::class.java)

    override fun getTopListData(
        TOPLIST_: Repository.TOPLIST,
        callback: (TopListMusic?) -> Unit
    ) {
        service.getTopList(TOPLIST_.idx).enqueue(object : Callback<Map<Any, Any>> {
            override fun onFailure(call: Call<Map<Any, Any>>, t: Throwable): Unit = throw t

            @Suppress("UNCHECKED_CAST")
            override fun onResponse(call: Call<Map<Any, Any>>, response: Response<Map<Any, Any>>) {
                val body = response.body()
                val code = body?.get("code")
                if (code != 200.0) {
                    callback(TopListMusic("API请求失败", "我觉得有可能是API挂了", list = TOPLIST_))
                    return
                }

                fun double2String(double: Double): String {
                    val decimalFormat: DecimalFormat =
                        DecimalFormat("###################.###########")
                    return decimalFormat.format(double)
                }

                val playlist: LinkedTreeMap<String, Any> =
                    body.get("playlist") as LinkedTreeMap<String, Any>
                val tracks: ArrayList<LinkedTreeMap<String, Any>> =
                    playlist.get("tracks") as ArrayList<LinkedTreeMap<String, Any>>
                tracks.forEach { it: LinkedTreeMap<String, Any> ->

                    lateinit var musicName: String
                    lateinit var artist: String
                    lateinit var url: String

                    musicName = it.get("name") as String
                    val id: String = double2String(it["id"] as Double)
                    url = "/song?id=$id"

                    val ar: ArrayList<LinkedTreeMap<Any, Any>> =
                        it.get("ar") as ArrayList<LinkedTreeMap<Any, Any>>
                    ar.forEach {
                        artist = with(StringBuilder()) {
                            append(it["name"])
                            append("  ")
                            toString()
                        }
                    }
                    callback(TopListMusic(musicName, artist, id, url, TOPLIST_))
                }
            }
        })
    }

    override fun getArtistByUrl(_url: String, callback: (String?) -> Unit) {
        return
    }

}