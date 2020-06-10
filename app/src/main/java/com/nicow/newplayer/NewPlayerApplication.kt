package com.nicow.newplayer

import android.app.Application
import android.content.Context

class NewPlayerApplication : Application() {

    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }


}