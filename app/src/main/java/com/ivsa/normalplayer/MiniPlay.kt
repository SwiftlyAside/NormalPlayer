package com.ivsa.normalplayer

import android.content.*
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso

/**
 * Miniplayer
 * Description:
 * 이 Fragment는 탐색화면 아래에 표시되는 미니플레이어의 UI입니다.
 */
class MiniPlay : Fragment() {
    var album: ImageView? = null
    var pp: ImageButton? = null
    var songname: TextView? = null
    //Service로부터 메시지를 받습니다.
    var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            refresh()
        }
    }

    //브로드캐스터를 등록합니다.
    fun registerBroadCast() {
        val filter = IntentFilter()
        filter.addAction(PlaybackService.CHANGE)
        activity!!.registerReceiver(broadcastReceiver, filter)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mini = inflater.inflate(R.layout.fragment_mini, null)
        album = mini.findViewById(R.id.imalbumart)
        songname = mini.findViewById(R.id.tvmsongn)
        pp = mini.findViewById(R.id.implay)
        registerBroadCast()
        refresh()
        return mini
    }

    //UI를 새로고칩니다.
    fun refresh() {
        if (MusicApplication.instance.manager.isPlaying) {
            pp!!.setImageResource(R.drawable.pause)
        } else {
            pp!!.setImageResource(R.drawable.play)
        }
        val meta = MusicApplication.instance.manager.meta
        if (meta != null) {
            val albumart = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), meta.albumId!!.toLong())
            Picasso.get().load(albumart).error(R.drawable.nothing).into(album)
            songname!!.text = meta.title
        } else {
            album!!.setImageResource(R.drawable.nothing)
            songname!!.text = "음악을 선택하면 재생합니다."
        }
    }

    override fun onDetach() {
        super.onDetach()
        activity!!.unregisterReceiver(broadcastReceiver)
    }
}