package com.example.iveci.pmultip;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;

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
