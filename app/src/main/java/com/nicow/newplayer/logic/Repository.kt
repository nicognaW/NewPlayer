package com.nicow.newplayer.logic

import android.media.MediaPlayer
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.nicow.newplayer.NewPlayerApplication
import com.nicow.newplayer.data.Music
import com.nicow.newplayer.data.TopListMusic
import com.nicow.newplayer.logic.retrofit.abstract.AbstractTopListRequest
import com.nicow.newplayer.logic.retrofit.nativeweb.MusicURLRequest
import com.nicow.newplayer.logic.retrofit.musicapi.TopListRequest as MTopListRequest
import com.nicow.newplayer.logic.retrofit.nativeweb.TopListRequest as NTopListRequest

object Repository {

    enum class TOPLIST(val id: String, val idx: String) {

        NEWSONG("3779629", "0"),
        BILLBOARD("60198", "6"),
        TICKTOCK("2250011882", "26"),
        HOTSONG("3778678", "1"),
        ACG("71385702", "22"),
        BEATPORT("3812895", "21")
    }

    enum class API {
        NATIVEWEB,
        MUSICAPI
    }

    private var TopListRequest: AbstractTopListRequest? = MTopListRequest()

    object MediaPlayerController {

        // TODO: Use MediaPlaybackService

        private val mediaPlayer: MediaPlayer = MediaPlayer()

        var currentPLayingMusicLiveData: MutableLiveData<Music> = MutableLiveData()
        var srcPath: String? = null

        fun initPlayer() {
            srcPath?.run {
                mediaPlayer.apply {
                    setDataSource(srcPath)
                    isLooping = true
                    prepare()
                }
            }
        }

        init {
            currentPLayingMusicLiveData.observeForever {
                srcPath = MusicURLRequest.getMusicURLById(it.id)
                mediaPlayer.reset()
                initPlayer()
            }
            mediaPlayer.setOnPreparedListener {
                Toast.makeText(
                    NewPlayerApplication.context,
                    "播放器准备完成\n 开始播放:${currentPLayingMusicLiveData.value?.musicName} ",
                    Toast.LENGTH_SHORT
                )
                    .show()
                it.start()
            }
        }

        fun clickPlayBtn() {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            } else {
                mediaPlayer.start()
            }
        }
    }

    /**
     * Set the api for backend data
     * @param api API
     */
    fun setApi(api: API) {
        when (api) {
            API.MUSICAPI -> {
                if (TopListRequest!!::class.java != MTopListRequest::class.java) {
                    TopListRequest = null
                    TopListRequest = MTopListRequest()
                }
            }
            API.NATIVEWEB -> {
                if (TopListRequest!!::class.java != NTopListRequest::class.java) {
                    TopListRequest = null
                    TopListRequest = NTopListRequest()
                }
            }
        }
    }

    fun getMusicList(
        TOPLIST_: TOPLIST,
        liveData_: MutableLiveData<TopListMusic>
    ) {
        TopListRequest!!.getTopListData(
            TOPLIST_
        ) {
            it?.let { it1 ->
                liveData_.value = it1
            }
        }
    }

    fun getArtistByUrl(
        _url: String,
        callback: (String?) -> Unit
    ) {
        TopListRequest!!.getArtistByUrl(
            _url,
            callback
        )
    }
}