package com.ivsa.normalplayer

import android.app.Application
import android.content.Context

/**
 * Created by iveci on 2017-06-08.
 */
class MusicApplication : Application() {
    var manager: PlayManager? = null
    override fun onCreate() {
        super.onCreate()
        instance = this
        manager = PlayManager(applicationContext)
    }

    companion object {
        var instance: MusicApplication? = null
    }
}