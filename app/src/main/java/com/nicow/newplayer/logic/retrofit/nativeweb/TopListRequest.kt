package com.nicow.newplayer.logic.retrofit.nativeweb

import android.util.Log
import com.nicow.newplayer.data.TopListMusic
import com.nicow.newplayer.logic.Repository
import com.nicow.newplayer.logic.retrofit.abstract.AbstractTopListRequest
import com.nicow.newplayer.logic.retrofit.nativeweb.ServiceCreator.create
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TopListRequest : AbstractTopListRequest() {

    private val service = create(TopListService::class.java)

    override fun getTopListData(
        TOPLIST_: Repository.TOPLIST,
        callback: (TopListMusic?) -> Unit
    ) {
        service.getTopList(TOPLIST_.id).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable): Unit = throw t

            override fun onResponse(call: Call<String>, response: Response<String>) {
                val body = response.body() ?: ""
                val document: Document = Jsoup.parse(body)
                val targetElements: Elements = document.select("#song-list-pre-cache > ul")
                if (targetElements.size == 0) {
                    val element404judge = document.select("p.note")
                    if (element404judge.size >= 1) {
                        callback(TopListMusic("加载失败", "应该是IP被反爬了", list = TOPLIST_))
                        return
                    } else {
                        callback(TopListMusic("加载失败", "但是程序并不知道是为什么", list = TOPLIST_))
                        return
                    }
                }
                val targetElement = targetElements[0]
                Log.d(this::class.java.toString(), "目标元素内容: $targetElement")
                // 构造数据模型集合
                targetElement.children().forEach { element ->
                    val url = element.childNode(0).attr("href")
                    val id = Regex("/song\\?id=.*").find(url)!!.value.substring(9)
                    callback(
                        TopListMusic(
                            element.text(),
                            "↺",
                            id = id,
                            url = url,
                            list = TOPLIST_
                        )
                    )
                }
            }
        })
    }

    override fun getArtistByUrl(
        _url: String,
        callback: (String?) -> Unit
    ) {
        val url = _url
        Log.d(this::class.java.toString(), "Fetch the artist of $url")
        service.getArtistByUrl(url).enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                throw t
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                val body = response.body() ?: ""
                val document: Document = Jsoup.parse(body)
                val targetElements: Elements =
                    document.select("div.g-bd4.f-cb > div.g-mn4 > div > div > div.m-lycifo > div.f-cb > div.cnt > p:nth-child(2) > span > a")
                if (targetElements.size == 0) {
                    return
                }
                val result: String = with(StringBuilder()) {
                    for (element in targetElements) {
                        append(element.text())
                        append("  ")
                    }
                    toString()
                }
                callback(result)
            }

        })
    }
}