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

    /**
     *  Singleton class to control the MediaPlayer instance
     *  this class will be deprecated in the future.
     */
    object MediaPlayerController {

        enum class PlayMode {
            SINGLELOOP, LISTLOOP, LISTRANDOM, ALLRANDOM
        }

        /**
         * Enum class used to define what to do when the current playing music changes with no list
         * changes.
         */
        enum class listChangeMethod {
            CLEAR, STAY, APPEND
        }

        var srcPath: String? = null

        private val mediaPlayer: MediaPlayer = MediaPlayer()

        var currentPLayingMusicLiveData: MutableLiveData<Music> = MutableLiveData()

        var currentPlayingListLiveData: MutableLiveData<ArrayList<Music>> = MutableLiveData()

        var currentPlayMode: PlayMode = PlayMode.LISTLOOP

        val playStateLiveData = MutableLiveData<Boolean>()

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
                playStateLiveData.value = true
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
                playStateLiveData.value = false
            }
        }

        private fun initPlayer() {
            srcPath?.run {
                mediaPlayer.apply {
                    setDataSource(srcPath)
                    isLooping = false
                    prepare()
                }
            }
        }

        fun getDuration(): Int = mediaPlayer.duration

        fun getPlayState(): Boolean = mediaPlayer.isPlaying

        fun seekTo(i: Int) {
            mediaPlayer.seekTo(i)
        }

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
                            listChangeMethod.STAY
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
                            listChangeMethod.STAY
                        )
                    } else {
                        val prePosition = currentPosition + -1
                        changeMusicWithNoList(
                            currentPlayingListLiveData.value!![prePosition],
                            listChangeMethod.STAY
                        )
                    }
                }
                else -> return
            }
        }

        fun setPlayMode(playMode: PlayMode) {
            currentPlayMode = playMode
            when (currentPlayMode) {
                PlayMode.SINGLELOOP -> mediaPlayer.isLooping = true
                else -> return
            }
        }

        /**
         * Change the play list.
         * This function usually changes the current playing music in currentPlayingListLiveData's
         * observer.
         */
        fun changeList(list: ArrayList<Music>) {
            currentPlayingListLiveData.value = list
        }

        /**
         * This method will change the current playing music with the given music and lisgchangemethod.
         * @param music Music to change
         * @param method listChangeMethod of the operate, for STAY, it will only change the music to
         * livedata, for APPEND, it will add the music param to the current playing music list, for
         * clear, it will call the changeMusicWithList method with an empty list.
         */
        fun changeMusicWithNoList(music: Music, method: listChangeMethod = listChangeMethod.STAY) {
            when (method) {
                listChangeMethod.STAY -> {
                    currentPLayingMusicLiveData.value = music
                }

                listChangeMethod.APPEND -> {
                    currentPlayingListLiveData.value?.add(music)
                    currentPLayingMusicLiveData.value = music
                }

                listChangeMethod.CLEAR -> {
                    changeMusicWithList(music, ArrayList())
                }
            }
        }

        /**
         * Sync change the current playing music and list, usually called to play a new music from
         * another list.
         * @param music Music to play.
         * @param list ArrayList<Music> of music.
         */
        fun changeMusicWithList(music: Music, list: ArrayList<Music>) {
            currentPLayingMusicLiveData.value = music
            currentPlayingListLiveData.value = list
        }

        /**
         * Only called when user click the play/pause button.
         */
        fun clickPlayBtn() {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                playStateLiveData.value = false
            } else {
                mediaPlayer.start()
                playStateLiveData.value = true
            }
        }
    }

    private var TopListRequest: AbstractTopListRequest? = MTopListRequest()

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