package com.nicow.newplayer.logic.retrofit.abstract

import com.nicow.newplayer.data.TopListMusic
import com.nicow.newplayer.logic.Repository

abstract class AbstractTopListRequest {
    private val service = null

    abstract fun getTopListData(
        TOPLIST_: Repository.TOPLIST,
        callback: (TopListMusic?) -> Unit
    )

    abstract fun getArtistByUrl(_url: String, callback: (String?) -> Unit)

}