package com.nicow.newplayer.ui.activity

import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.nicow.newplayer.R
import com.nicow.newplayer.data.Music
import com.nicow.newplayer.data.TopListMusic
import com.nicow.newplayer.ui.viewmodel.PlayingViewModel
import kotlinx.android.synthetic.main.activity_playing.*

class PlayingActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar

    private lateinit var ab: ActionBar


    private lateinit var viewModel: PlayingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playing)

        val music: Music?

        if (intent.extras == null) {
            music = PlayingViewModel.getCurrentMusic()
        } else {
            music = intent.extras?.get("Music") as TopListMusic
        }


        viewModel = ViewModelProviders.of(this).get(PlayingViewModel::class.java)

        toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        ab = supportActionBar!!
        ab.setDisplayHomeAsUpEnabled(true)
        ab.setHomeAsUpIndicator(R.drawable.actionbar_back)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        ab.title = music?.musicName
        ab.subtitle = music?.artist

        playing_play.setOnClickListener {
            PlayingViewModel.clickPlayBtn()
        }

    }
}