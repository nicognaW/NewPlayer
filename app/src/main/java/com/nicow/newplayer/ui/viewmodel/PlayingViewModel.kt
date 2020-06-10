package com.nicow.newplayer.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nicow.newplayer.data.Music
import com.nicow.newplayer.logic.Repository
import com.nicow.newplayer.logic.Repository.MediaPlayerController.currentPLayingMusicLiveData

class PlayingViewModel : ViewModel() {


    /* TODO
    *   1，重写PlayingViewModel
    *     用object写为单例类 / 在多个activity中实例化
    *   2, 完善播放控制器
    *     a, 增加专辑图
    *     b, 增加needle动画
    *     c, 增加下载按钮
    *     d, 完善控制功能
    *   3, 完善动画
    *     a, activity切换动画
    *     b, menu下切换API后不收回menu
    *   4, 增加播放列表
    *   5, 增加歌单
    *   6, 增加搜索功能
    *   8, 创建 本地/github Git仓库
    */

    companion object {
        fun playMusic(music: Music) {
            if (music == currentPLayingMusicLiveData.value) {
                return
            }
            currentPLayingMusicLiveData.value = music
        }

        fun getCurrentMusic(): Music? =
            currentPLayingMusicLiveData.value

        fun getCurrentMusicLiveData(): MutableLiveData<Music> =
            currentPLayingMusicLiveData

        fun clickPlayBtn() {
            Repository.MediaPlayerController.clickPlayBtn()
        }
    }
}