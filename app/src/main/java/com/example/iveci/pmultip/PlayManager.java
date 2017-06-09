package com.example.iveci.pmultip;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.util.ArrayList;


/**
 * PlayManager
 * Description:
 * PlaybackService와 다른 Activity간 중계역할을 수행합니다.
 */

public class PlayManager {
    ServiceConnection serviceConnection;
    PlaybackService pService;
    public PlayManager(Context context) {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                pService = ((PlaybackService.playbackServicebinder) service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                serviceConnection = null;
                pService = null;
            }
        };
        context.bindService(new Intent(context, PlaybackService.class)
                .setPackage(context.getPackageName()), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    //재생과 일시정지를 전환합니다.
    public void toggle() {
        if (isReady()) pService.setPause();
        else pService.setPlay();
    }

    public Meta getMeta() {
        if (pService != null) return pService.getMeta();
        return null;
    }

    //재생준비여부를 반환합니다.
    public boolean isReady() {
        return pService != null && pService.isReady();
    }

    public void playList(ArrayList<Long> musics) {
        if (pService != null) pService.getList(musics);
    }

    public void play(int position) {
        if (pService != null) pService.setPlay(position);
    }

    public void play() {
        if (pService != null) pService.setPlay();
    }

    public void pause() {
        if (pService != null) pService.setPause();
    }

    public void next() {
        if (pService != null) pService.setNext();
    }

    public void prev() {
        if (pService != null) pService.setPrev();
    }
}
