package com.nicow.newplayer.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nicow.newplayer.data.TopListMusic
import com.nicow.newplayer.logic.Repository

class TopListViewModel : ViewModel() {

    var currentApi = MutableLiveData<Repository.API>()
    var currentListIdLiveData = MutableLiveData<Repository.TOPLIST>()
    var topListLiveData = MutableLiveData<ArrayList<TopListMusic>>()
    var topListMusicLiveData: MutableLiveData<TopListMusic> = MutableLiveData<TopListMusic>()


    init {
        currentApi.observeForever {
            Repository.setApi(it)
        }
        currentApi.value = Repository.API.MUSICAPI
        currentListIdLiveData.observeForever {
            getTopList(it)
        }
        currentListIdLiveData.value = Repository.TOPLIST.HOTSONG
        topListLiveData.value = arrayListOf()
    }


    private fun getTopList(TOPlIST_: Repository.TOPLIST = currentListIdLiveData.value!!) {
        Repository.getMusicList(TOPlIST_, topListMusicLiveData)
    }

    fun switchApi(API_: Repository.API = currentApi.value!!) {
        if (API_ != currentApi.value) {
            currentApi.value = API_
        } else {
            return
        }
    }

    fun switchTopList(TOPlIST_: Repository.TOPLIST = currentListIdLiveData.value!!) {
        if (TOPlIST_ != currentListIdLiveData.value) {
            currentListIdLiveData.value = TOPlIST_
        } else {
            return
        }
    }

    fun getArtistByUrl(
        _url: String,
        callback: (String?) -> Unit
    ) {
        Repository.getArtistByUrl(_url, callback)
    }
}
