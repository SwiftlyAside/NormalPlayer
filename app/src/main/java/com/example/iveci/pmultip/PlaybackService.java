package com.example.iveci.pmultip;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.IntDef;

import java.io.IOException;
import java.util.ArrayList;

public class PlaybackService extends Service {
    private final IBinder ibinder = new playbackServicebinder();
    MediaPlayer playback = new MediaPlayer();
    private ArrayList<Meta> m_musics;
    private ContentResolver resolver;
    private int pos = 0;
    boolean play = false;

    public class playbackServicebinder extends Binder {
        PlaybackService getService() {
            return PlaybackService.this;
        }
    }
    public PlaybackService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        playback.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        playback.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (pos < m_musics.size() - 1)
                    setPlay(m_musics.get(++pos));
            }
        });
        setPlay(m_musics.get(pos));
    }

    public void setPlay(Meta meta) { //메타데이터로 재생합니다.
        try {
            Uri musicuri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, meta.getId());
            playback.reset();
            playback.setDataSource(this, musicuri);
            playback.prepare();
            play = true;
            playback.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getMeta() { //음악의 메타데이터를 가져옵니다.
        m_musics = new ArrayList<>();
        String[] column = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION};

        Cursor cursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, column, null, null, null);

        while (cursor.moveToNext()) {
            Meta meta = new Meta();
            meta.setId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
            meta.setAlbumId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
            meta.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            meta.setAlbum(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
            meta.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
            meta.setDuration(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))); //컬럼인식불가능.
            m_musics.add(meta);
        }
        cursor.close();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        pos = intent.getIntExtra("pos", 0);
        m_musics = (ArrayList<Meta>) intent.getSerializableExtra("playlist");
        return ibinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        play = false;
        if(playback != null) {
            playback.stop();
            playback.release();
            playback = null;
        }
    }
}
