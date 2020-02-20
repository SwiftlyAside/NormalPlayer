package com.ivsa.normalplayer

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.os.PowerManager
import android.provider.MediaStore
import com.ivsa.normalplayer.Meta.Companion.setByCursor
import java.io.IOException
import java.util.*

/*
* PlaybackService
*
* Description:
* 음악을 재생하는 기본 서비스입니다.
*
* */
class PlaybackService : Service() {
    private val ibinder: IBinder = playbackServicebinder()
    private var player: MediaPlayer? = MediaPlayer()
    private val musicsOnList = ArrayList<Long>()
    private val musicsOnPlaylist = ArrayList<Meta>()
    //현재 음악정보를 반환합니다.
    var meta: Meta? = null
        private set
    private var pos = 0
    private var ready = false
    //재생목록으로 플레이중인지 여부를 반환합니다.
    var isPlaymode = false
        private set

    internal inner class playbackServicebinder : Binder() {
        val service: PlaybackService
            get() = this@PlaybackService
    }

    override fun onCreate() {
        super.onCreate()
        player!!.setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
        player!!.setOnPreparedListener { mp ->
            ready = true
            mp.start()
            sendBroadcast(Intent(CHANGE))
        }
        player!!.setOnCompletionListener {
            if (isPlaymode) {
                when (pos) {
                    musicsOnPlaylist.size - 1 -> setPlay(++pos)
                    else -> sendBroadcast(Intent(CHANGE))
                }
            } else {
                when (pos) {
                    musicsOnList.size - 1 -> setPlay(++pos)
                    else -> sendBroadcast(Intent(CHANGE))
                }
            }
        }
        player!!.setOnErrorListener { mp, what, extra ->
            ready = false
            false
        }
    }

    //
//재생여부를 반환합니다.
    val isPlaying: Boolean
        get() = player!!.isPlaying

    //현재 재생위치를 반환합니다.
    val current: Int
        get() = player!!.currentPosition

    //메타데이터로 재생합니다. (서비스 내에서만 사용함)
    private fun Play() {
        try {
            val musicuri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, meta!!.id)
            player!!.setDataSource(this, musicuri)
            player!!.setAudioAttributes(AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build())
            player!!.prepareAsync()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    //플레이어를 정지합니다. 현재 재생중인 자원은 반환합니다.
    fun setStop() {
        player!!.stop()
        player!!.reset()
    }

    //그냥 재생합니다. 플레이어가 준비돼야만 재생합니다.
    fun setPlay() {
        if (ready) {
            player!!.start()
            sendBroadcast(Intent(CHANGE))
        }
    }

    //선택한 위치에 있는 음악을 재생합니다. 플레이어 준비 여부에 관계없이 작동합니다.
    fun setPlay(position: Int) {
        if (isPlaymode) {
            meta = musicsOnPlaylist[position]
            pos = position
        } else {
            queryMusic(position)
        }
        setStop()
        Play()
        sendBroadcast(Intent(CHANGE))
    }

    //일시 정지합니다.
    fun setPause() {
        if (isPlaying) {
            player!!.pause()
            sendBroadcast(Intent(CHANGE))
        }
    }

    //트래킹한 위치로 이동합니다.
    fun seekTo(position: Int) {
        player!!.seekTo(position)
    }

    //이전 곡 또는 처음위치로 갑니다.
    fun setPrev() {
        if (player!!.currentPosition.toFloat() / player!!.duration.toFloat() < 0.15) {
            if (pos > 0) pos-- else if (isPlaymode) pos = musicsOnPlaylist.size - 1 else pos = musicsOnList.size - 1
            setPlay(pos)
        } else player!!.seekTo(0)
    }

    //다음 곡으로 갑니다.
    fun setNext() {
        if (isPlaymode) {
            if (pos < musicsOnPlaylist.size - 1) pos++ else pos = 0
        } else {
            if (pos < musicsOnList.size - 1) pos++ else pos = 0
        }
        setPlay(pos)
    }

    //재생목록으로 재생할때 목록을 가져옵니다. 호출시 재생목록모드로 변경됩니다.
    fun setPl(metas: ArrayList<out Meta?>) {
        if (musicsOnPlaylist != metas) {
            musicsOnPlaylist.clear()
            val addAll = musicsOnPlaylist.addAll(metas)
        }
        isPlaymode = true
    }

    //재생할 음악의 메타데이터를 쿼리합니다.
    private fun queryMusic(position: Int) {
        val musicid = musicsOnList[position]
        pos = position
        val proj = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION)
        val select = MediaStore.Audio.Media._ID + " = ?"
        val args = arrayOf(musicid.toString())
        val cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj, select, args, null)
        if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()
            meta = setByCursor(cursor)
        }
        cursor!!.close()
    }

    //음악의 목록을 변수로 복사합니다. 호출시 일반재생모드로 변경됩니다.
    fun getList(musics: ArrayList<out Long?>) {
        if (musicsOnList != musics) {
            musicsOnList.clear()
            musicsOnList.addAll(musics)
        }
        isPlaymode = false
    }

    override fun onBind(intent: Intent): IBinder {
        return ibinder
    }

    override fun onDestroy() {
        super.onDestroy()
        ready = false
        if (player != null) {
            player!!.stop()
            player!!.release()
            player = null
        }
    }

    companion object {
        @JvmField
        var CHANGE = "CHANGED"
    }
}