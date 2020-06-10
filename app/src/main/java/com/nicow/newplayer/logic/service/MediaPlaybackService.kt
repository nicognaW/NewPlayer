package com.nicow.newplayer.logic.service

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import androidx.media.MediaBrowserServiceCompat


class MediaPlaybackService : MediaBrowserServiceCompat() {
    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
//        TODO("Not yet implemented")
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
//        TODO("Not yet implemented")
        return null
    }
}