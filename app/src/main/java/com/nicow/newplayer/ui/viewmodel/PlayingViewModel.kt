package com.nicow.newplayer.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nicow.newplayer.data.Music
import com.nicow.newplayer.logic.Repository
import com.nicow.newplayer.logic.Repository.MediaPlayerController.currentPLayingMusicLiveData

object PlayingViewModel : ViewModel() {


    /* TODO
    *   1，重写PlayingViewModel
    *     用object写为单例类（✔） / 在多个activity中实例化
    *   2, 完善播放控制器
    *     a, 增加专辑图
    *     b, 增加needle动画
    *     c, 增加下载按钮
    *     d, 完善控制功能(Working)
    *   3, 完善动画
    *     a, activity切换动画
    *     b, menu下切换API后不收回menu
    *   4, 增加播放列表(Done)
    *   5, 增加歌单
    *   6, 增加搜索功能
    */

    fun changeMusicWithList(music: Music, list: ArrayList<Music>) {
        Repository.MediaPlayerController.changeMusicWithList(music, list)
    }

    fun getCurrentMusic(): Music? =
        currentPLayingMusicLiveData.value

    fun getCurrentMusicLiveData(): MutableLiveData<Music> =
        currentPLayingMusicLiveData

    fun getCurrentList(): ArrayList<Music> =
        Repository.MediaPlayerController.currentPlayingListLiveData.value!!

    fun clickPlayBtn() {
        Repository.MediaPlayerController.clickPlayBtn()
    }
}