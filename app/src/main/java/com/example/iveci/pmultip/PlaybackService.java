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

public class PlaybackService extends Service {
    private final IBinder ibinder = new playbackServicebinder();
    MediaPlayer playback = new MediaPlayer();
    private ArrayList<Meta> m_musics;
    private ContentResolver resolver;
    private int pos = 0;
    boolean ready = false;

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
        playback.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                ready = true;
                mp.start();
            }
        });
        playback.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (pos < m_musics.size() - 1)
                    setPlay(m_musics.get(++pos));
                else ready = false;
            }
        });
        setPlay(m_musics.get(pos));
    }

    //메타데이터로 재생합니다.
    public void setPlay(Meta meta) {
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
        if (ready) playback.start();
    }

    //선택한 위치에 있는 음악을 재생합니다. 플레이어 준비 여부에 관계없이 작동합니다.
    public void setPlayAt(int position) {
        setStop();
        setPlay(m_musics.get(position));
    }

    //일시 정지합니다.
    public void setPause() {
        if (ready) playback.pause();
    }

    //이전 곡 또는 처음위치로 갑니다.
    public void setPrev() {
        if ((float) (playback.getCurrentPosition())/(float)(playback.getDuration()) < 0.15) {
            if (pos > 0) pos--;
            else pos = m_musics.size() - 1;
            setPlay(m_musics.get(pos));
        }
        else playback.seekTo(0);
    }

    //다음 곡으로 갑니다.
    public void setNext() {
        if (pos < m_musics.size() - 1) pos++;
        else pos = 0;
        setPlay(m_musics.get(pos));
    }


    //음악의 메타데이터를 가져와 목록을 생성합니다.
    private void getMeta() {
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
            meta.setDuration(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
            m_musics.add(meta);
        }
        cursor.close();
    }

    //음악의 목록을 변수로 복사합니다.
    public void getList(ArrayList<Meta> musics) {
        if (!m_musics.equals(musics)) {
            musics.clear();
            musics.addAll(m_musics);
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
