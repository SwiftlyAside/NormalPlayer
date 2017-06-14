package com.example.iveci.pmultip;

import android.database.Cursor;
import android.provider.MediaStore;

import java.io.Serializable;

/**
 * Created by iveci on 2017-06-05.
 * Desctiption: 음악의 메타데이터를 가지는 데이터클래스입니다.
 */

public class Meta implements Serializable {
    private String id;



    private String memberid;
    private String albumId;
    private String title;
    private String album;
    private String artist;
    private int duration;

    public static Meta setByCursor(Cursor cursor) {
        Meta meta = new Meta();
        if (cursor.getColumnIndex(MediaStore.Audio.Playlists.Members._ID) != -1) {
            meta.setMemberid(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members._ID)));
        }
        if (cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID) == -1) {
            meta.setId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
        }
        else meta.setId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID)));
        meta.setAlbumId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
        meta.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
        meta.setAlbum(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
        meta.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
        meta.setDuration(Integer.parseInt(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))));
        return meta;
    }
    public String getMemberid() {
        return memberid;
    }

    public void setMemberid(String memberid) {
        this.memberid = memberid;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return  title+" - " +artist;
    }
}
