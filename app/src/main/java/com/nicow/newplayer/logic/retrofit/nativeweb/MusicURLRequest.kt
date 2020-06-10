package com.nicow.newplayer.logic.retrofit.nativeweb

object MusicURLRequest {

    fun getMusicURLById(id_: String): String =
        "https://music.163.com/song/media/outer/url?id=$id_.mp3"
}