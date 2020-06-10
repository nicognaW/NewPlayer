package com.nicow.newplayer.data

import java.io.Serializable

abstract class Music(
    open val musicName: String,
    open var artist: String = "未知",
    open var id: String
) :
    Serializable