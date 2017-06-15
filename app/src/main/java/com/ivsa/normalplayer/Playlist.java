package com.ivsa.normalplayer;

import android.database.Cursor;
import android.provider.MediaStore;

/**
 * Created by iveci on 2017-06-11.
 */

public class Playlist {
    private long id;
    private String name;

    public Playlist(){

    }

    public Playlist(long id, String name) {
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

    public static Playlist setByCursor(Cursor cursor) {
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
