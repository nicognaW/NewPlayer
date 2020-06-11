package com.nicow.newplayer.ui.activity

import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.nicow.newplayer.R
import com.nicow.newplayer.data.Music
import com.nicow.newplayer.data.TopListMusic
import com.nicow.newplayer.logic.Repository
import com.nicow.newplayer.logic.Repository.MediaPlayerController.playStateLiveData
import com.nicow.newplayer.ui.viewmodel.PlayingViewModel
import kotlinx.android.synthetic.main.activity_playing.*

class PlayingActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar

    private lateinit var ab: ActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playing)

        val music: Music?

        if (intent.extras == null) {
            music = PlayingViewModel.getCurrentMusic()
        } else {
            music = intent.extras?.get("Music") as TopListMusic
        }

        toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        ab = supportActionBar!!
        ab.setDisplayHomeAsUpEnabled(true)
        ab.setHomeAsUpIndicator(R.drawable.actionbar_back)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        PlayingViewModel.getCurrentMusicLiveData().observeForever {
            ab.title = it.musicName
            ab.subtitle = it.artist
        }

        playStateLiveData.observeForever {
            when (it) {
                true -> {
                    playing_play.setImageResource(R.drawable.play_rdi_btn_pause)
                }
                false, null -> {
                    playing_play.setImageResource(R.drawable.play_rdi_btn_play)
                }
            }
        }

        playing_play.setOnClickListener {
            PlayingViewModel.clickPlayBtn()
        }
        play_seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val duration = Repository.MediaPlayerController.getDuration()
                val target =
                    (progress * duration / 100)
                if (fromUser) {
                    Repository.MediaPlayerController.seekTo(target)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        playing_pre.setOnClickListener {
            Repository.MediaPlayerController.playPre()
        }

        playing_next.setOnClickListener {
            Repository.MediaPlayerController.playNext()
        }

    }
}