package com.example.iveci.pmultip;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

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
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
