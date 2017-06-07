package com.example.iveci.pmultip;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class playbackService extends Service {
    private final IBinder ibinder = new playbackServicebinder();
    MediaPlayer playback = new MediaPlayer();
    private ArrayList<Meta> m_musics;
    private ContentResolver resolver;
    private int pos = 0;
    boolean play = false;

    public class playbackServicebinder extends Binder {
        playbackService getService() {
            return playbackService.this;
        }
    }
    public playbackService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        resolver = getContentResolver();
        setPlay(m_musics.get(pos));
    }

    public void setPlay(Meta meta) { //메타데이터로 재생합니다.
        try {
            Uri musicuri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, meta.getId());
            playback.reset();
            playback.setDataSource(this, musicuri);
            playback.prepare();
            int epos = playback.getDuration();
            play = true;
            playback.start();
            Bitmap bitmap = BitmapFactory.decodeFile(getAlbumart(Long.parseLong(meta.getAlbumId()),getApplicationContext()));
            new Playback.mps().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getAlbumart(long albumid, Context context) {
        Cursor album = context.getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums._ID + " = ?",
                new String[]{Long.toString(albumid)},
                null);
        String result = null;
        if (album.moveToFirst())
            result = album.getString(0);
        album.close();
        return result;
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
