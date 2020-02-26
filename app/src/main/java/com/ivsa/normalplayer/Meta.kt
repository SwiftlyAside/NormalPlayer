package com.ivsa.normalplayer

import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import java.io.Serializable

/**
 * Created by iveci on 2017-06-05.
 * Refactored by iveci on 2020-02-20.
 * Desctiption: 음악의 메타데이터를 가지는 데이터클래스입니다.
 */
class Meta : Serializable {
    var id: String? = null
    var memberid: String? = null
    var albumId: String? = null
    var title: String? = null
    var album: String? = null
    var artist: String? = null
    var duration = 0

    override fun toString(): String {
        return "$title - $artist"
    }

    companion object {
        // TODO: API 하한 버전 체크할것. (MINIMUM TO Q)
        @RequiresApi(Build.VERSION_CODES.Q)
        @JvmStatic
        fun setByCursor(cursor: Cursor): Meta {
            val meta = Meta()
            if (cursor.getColumnIndex(MediaStore.Audio.Playlists.Members._ID) != -1) {
                meta.memberid = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members._ID))
            }
            if (cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID) == -1) {
                meta.id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
            } else meta.id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID))
            meta.albumId = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
            meta.title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
            meta.album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
            meta.artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
            meta.duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)).toInt()
            return meta
        }
    }
}