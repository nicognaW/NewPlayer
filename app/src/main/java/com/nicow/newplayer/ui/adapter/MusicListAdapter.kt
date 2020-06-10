package com.nicow.newplayer.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.nicow.newplayer.NewPlayerApplication
import com.nicow.newplayer.R
import com.nicow.newplayer.data.TopListMusic
import com.nicow.newplayer.ui.activity.PlayingActivity
import com.nicow.newplayer.ui.viewmodel.PlayingViewModel

class MusicListAdapter(var musicList: ArrayList<TopListMusic>) :
    RecyclerView.Adapter<MusicListAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val musicName: TextView = view.findViewById(R.id.musicName)
        val artist: TextView = view.findViewById(R.id.artist)
        val count: TextView = view.findViewById(R.id.count)
        val more: ImageView = view.findViewById(R.id.more_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_music_list, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.more.setOnClickListener {
            val position = viewHolder.adapterPosition
            val item = musicList[position]
            Toast.makeText(parent.context, "This music is ${item.musicName}", Toast.LENGTH_SHORT)
                .show()
        }
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            val item = musicList[position]
            PlayingViewModel.playMusic(item)
        }
        viewHolder.itemView.setOnLongClickListener {
            val position = viewHolder.adapterPosition
            val item = musicList[position]
            val intent = Intent(NewPlayerApplication.context, PlayingActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("Music", item)
            if (PlayingViewModel.getCurrentMusic() != item) {
                PlayingViewModel.playMusic(item)
            }
            NewPlayerApplication.context.startActivity(intent)
            true
        }
        return viewHolder
    }

    override fun getItemCount(): Int = musicList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val music = musicList[position]
        holder.musicName.text = music.musicName
        holder.artist.text = music.artist
        holder.count.text = (position + 1).toString()
    }
}