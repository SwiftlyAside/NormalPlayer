package com.example.iveci.pmultip;

import java.io.Serializable;

/**
 * Created by iveci on 2017-06-05.
 */

public class Meta implements Serializable {
    private String id;
    private String albumId;
    private String title;
    private String album;
    private String artist;
    private String mtype;

    public Meta() {
    }

    public Meta(String id, String albumId, String title, String album, String artist, String mtype) {
        this.id = id;
        this.albumId = albumId;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.mtype = mtype;
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

    public String getMtype() {
        return mtype;
    }

    public void setMtype(String mtype) {
        this.mtype = mtype;
    }

    @Override
    public String toString() {
        return "Meta{" +
                "id='" + id + '\'' +
                ", albumId='" + albumId + '\'' +
                ", title='" + title + '\'' +
                ", album='" + album + '\'' +
                ", artist='" + artist + '\'' +
                ", mtype='" + mtype + '\'' +
                '}';
    }
}
