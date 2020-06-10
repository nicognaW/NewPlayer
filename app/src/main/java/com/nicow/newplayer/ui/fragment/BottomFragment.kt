package com.nicow.newplayer.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nicow.newplayer.NewPlayerApplication
import com.nicow.newplayer.R
import com.nicow.newplayer.ui.activity.PlayingActivity
import com.nicow.newplayer.ui.viewmodel.PlayingViewModel
import kotlinx.android.synthetic.main.fragment_bottom.*


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val musicName_ = "Music Name"
private const val artist_ = "Artist"

/**
 * A simple [Fragment] subclass.
 * Use the [BottomFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BottomFragment : Fragment() {
    private var musicName: String? = null
    private var artist: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            musicName = it.getString(musicName_)
            artist = it.getString(artist_)
        }
        PlayingViewModel.getCurrentMusicLiveData().observeForever {
            bottom_music_name?.text = it.musicName
            bottom_artist?.text = it.artist
        }
    }

    override fun onStart() {
        super.onStart()
        bottom_layout?.setOnClickListener {
            val intent = Intent(NewPlayerApplication.context, PlayingActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            NewPlayerApplication.context.startActivity(intent)
        }
        bottom_pause?.setOnClickListener {
            PlayingViewModel.clickPlayBtn()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param musicName Parameter 1.
         * @param artist Parameter 2.
         * @return A new instance of fragment BottomFragment.
         */
        @JvmStatic
        fun newInstance(musicName: String, artist: String) =
            BottomFragment().apply {
                arguments = Bundle().apply {
                    putString(musicName_, musicName)
                    putString(artist_, artist)
                }
            }
    }
}