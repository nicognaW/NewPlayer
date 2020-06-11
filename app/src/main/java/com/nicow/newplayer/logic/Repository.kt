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

        enum class PlayMode {
            SINGLELOOP, LISTLOOP, LISTRANDOM, ALLRANDOM
        }

        enum class ChangeMethod {
            CLEAR, STAY, APPEND
        }

        private val mediaPlayer: MediaPlayer = MediaPlayer()

        var currentPLayingMusicLiveData: MutableLiveData<Music> = MutableLiveData()

        var currentPlayingListLiveData: MutableLiveData<ArrayList<Music>> = MutableLiveData()

        var currentPlayMode: PlayMode = PlayMode.LISTLOOP

        var srcPath: String? = null

        private fun initPlayer() {
            srcPath?.run {
                mediaPlayer.apply {
                    setDataSource(srcPath)
                    isLooping = false
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
            currentPlayingListLiveData.observeForever {
                if (!it.contains(currentPLayingMusicLiveData.value)) {
                    currentPLayingMusicLiveData.value = it[0]
                }
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

            mediaPlayer.setOnCompletionListener {
                when (currentPlayMode) {
                    PlayMode.LISTLOOP -> {
                        playNext()
                    }
                    else -> {
                        return@setOnCompletionListener
                    }
                }

            }
        }

        fun seekTo(i: Int) {
            mediaPlayer.seekTo(i)
        }

        fun getDuration(): Int = mediaPlayer.duration

        fun playNext() {
            when (currentPlayMode) {
                PlayMode.LISTLOOP -> {
                    if (currentPlayingListLiveData.value == null || currentPlayingListLiveData.value!!.isEmpty()) return

                    val currentPosition = currentPlayingListLiveData.value!!.indexOf(
                        currentPLayingMusicLiveData.value!!
                    )

                    if (currentPosition == currentPlayingListLiveData.value!!.lastIndex) {
                        changeList(currentPlayingListLiveData.value!!)
                    } else {
                        val nextPosition = currentPosition + 1
                        changeMusicWithNoList(
                            currentPlayingListLiveData.value!![nextPosition],
                            ChangeMethod.STAY
                        )
                    }
                }
                else -> return
            }
        }

        fun playPre() {
            when (currentPlayMode) {
                PlayMode.LISTLOOP -> {
                    if (currentPlayingListLiveData.value == null || currentPlayingListLiveData.value!!.isEmpty()) return

                    val currentPosition = currentPlayingListLiveData.value!!.indexOf(
                        currentPLayingMusicLiveData.value!!
                    )

                    if (currentPosition == 0) {
                        changeMusicWithNoList(
                            currentPlayingListLiveData.value!!.last(),
                            ChangeMethod.STAY
                        )
                    } else {
                        val prePosition = currentPosition + -1
                        changeMusicWithNoList(
                            currentPlayingListLiveData.value!![prePosition],
                            ChangeMethod.STAY
                        )
                    }
                }
                else -> return
            }
        }

        fun setListLoop() {
            currentPlayMode = PlayMode.LISTLOOP
        }

        fun changeList(list: ArrayList<Music>) {
            currentPlayingListLiveData.value = list
        }

        fun changeMusicWithNoList(music: Music, method: ChangeMethod = ChangeMethod.STAY) {
            when (method) {
                ChangeMethod.STAY -> {
                    currentPLayingMusicLiveData.value = music
                }

                ChangeMethod.APPEND -> {
                    currentPlayingListLiveData.value?.add(music)
                    currentPLayingMusicLiveData.value = music
                }

                ChangeMethod.CLEAR -> {
                    changeMusicWithList(music, ArrayList())
                }
            }
        }

        fun changeMusicWithList(music: Music, list: ArrayList<Music>) {
            currentPLayingMusicLiveData.value = music
            currentPlayingListLiveData.value = list
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