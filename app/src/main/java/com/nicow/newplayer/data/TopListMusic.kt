package com.nicow.newplayer.data

import com.nicow.newplayer.logic.Repository

data class TopListMusic(
    override val musicName: String,
    override var artist: String = "未知",
    override var id: String = "0",
    val url: String = "",
    val list: Repository.TOPLIST = Repository.TOPLIST.HOTSONG
) : Music(musicName, artist, id)