package com.example.iveci.pmultip;

import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

/**
 * Created by iveci on 2017-06-11.
 */

public class Playlist {
    private String id;
    private String name;

    public Playlist(){

    }

    public Playlist(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
        Log.d("Dur", cursor.getPosition()+"");
        playlist.setId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists._ID)));
        playlist.setName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.NAME)));
        return playlist;
    }

    @Override
    public String toString() {
        return name;
    }
}
