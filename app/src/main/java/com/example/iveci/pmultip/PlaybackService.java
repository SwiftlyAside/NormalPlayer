package com.example.iveci.pmultip;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;

import java.io.IOException;
import java.util.ArrayList;

/*
* PlaybackService
*
* Description:
* 음악을 재생하는 기본 서비스입니다.
*
* */

public class PlaybackService extends Service {
    private final IBinder ibinder = new playbackServicebinder();
    private MediaPlayer playback = new MediaPlayer();
    private ArrayList<Long> m_musics = new ArrayList<>();
    private Meta meta;
    private int pos = 0;
    private boolean ready = false;
    public static String READY = "READY", CHANGE = "CHANGED";

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
        playback.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                ready = true;
                mp.start();
                sendBroadcast(new Intent(READY));
            }
        });
        playback.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (pos < m_musics.size() - 1) {
                    setPlay(++pos);
                }
                else {
                    ready = false;
                    sendBroadcast(new Intent(CHANGE));
                }
            }
        });
    }

    //재생준비여부를 반환합니다.
    public boolean isReady() {
        return ready;
    }

    //현재 음악정보를 반환합니다.
    public Meta getMeta() {
        return meta;
    }

    //메타데이터로 재생합니다.
    public void Play() {
        try {
            Uri musicuri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, meta.getId());
            playback.setDataSource(this, musicuri);
            playback.setAudioStreamType(AudioManager.STREAM_MUSIC);
            playback.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //플레이어를 정지합니다. 현재 재생중인 자원은 반환합니다.
    public void setStop() {
        playback.stop();
        playback.reset();
    }

    //그냥 재생합니다. 플레이어가 준비돼야만 재생합니다.
    public void setPlay() {
        if (isReady()) {
            playback.start();
            sendBroadcast(new Intent(CHANGE));
        }
    }

    //선택한 위치에 있는 음악을 재생합니다. 플레이어 준비 여부에 관계없이 작동합니다.
    public void setPlay(int position) {
        queryMusic(position);
        setStop();
        Play();
    }

    //일시 정지합니다.
    public void setPause() {
        if (isReady()) {
            playback.pause();
            sendBroadcast(new Intent(CHANGE));
        }
    }

    //이전 곡 또는 처음위치로 갑니다.
    public void setPrev() {
        if ((float) (playback.getCurrentPosition())/(float)(playback.getDuration()) < 0.15) {
            if (pos > 0) pos--;
            else pos = m_musics.size() - 1;
            setPlay(pos);
        }
        else playback.seekTo(0);
    }

    //다음 곡으로 갑니다.
    public void setNext() {
        if (pos < m_musics.size() - 1) pos++;
        else pos = 0;
        setPlay(pos);
    }


    //재생할 음악의 메타데이터를 쿼리합니다.
    private void queryMusic(int position) {
        long musicid = m_musics.get(position);
        String[] proj = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION};
        String select = MediaStore.Audio.Media._ID + " = ?";
        String[] args = {String.valueOf(musicid)};
        Cursor cursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj, select, args, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            meta = Meta.setByCursor(cursor);
        }
        cursor.close();
    }

    //음악의 목록을 변수로 복사합니다.
    public void getList(ArrayList<Long> musics) {
        if (!m_musics.equals(musics)) {
            m_musics.clear();
            m_musics.addAll(musics);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return ibinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ready = false;
        if(playback != null) {
            playback.stop();
            playback.release();
            playback = null;
        }
    }
}
