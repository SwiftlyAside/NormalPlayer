package com.ivsa.normalplayer;

import android.database.Cursor;
import android.provider.MediaStore;

/**
 * Playlist
 * 재생목록 정보를 담는 데이터클래스입니다.
 *
 */

class Playlist {
    private long id;
    private String name;

    private Playlist(){

    }

    Playlist(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    static Playlist setByCursor(Cursor cursor) {
        Playlist playlist = new Playlist();
        playlist.setId(Long.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists._ID))));
        playlist.setName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.NAME)));
        return playlist;
    }

    @Override
    public String toString() {
        return name;
    }
}
