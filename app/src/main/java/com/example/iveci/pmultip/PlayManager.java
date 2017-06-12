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
        if (isPlaying()) pService.setPause();
        else pService.setPlay();
    }

    public Meta getMeta() {
        if (pService != null) return pService.getMeta();
        return null;
    }

    //재생여부를 반환합니다.
    public boolean isPlaying() {
        return pService != null && pService.isPlaying();
    }

    //현재 위치를 반환합니다.
    public int getCurrent() {
        if (pService != null) return pService.getCurrent();
        return 0;
    }

    //음악목록을 불러옵니다.
    public void playList(ArrayList<Long> musics) {
        if (pService != null) pService.getList(musics);
    }

    public void play(int position) {
        if (pService != null) pService.setPlay(position);
    }

    //트래킹한 위치로 이동합니다.
    public void seekTo(int position) {
        if (pService != null) pService.seekTo(position);
    }

    public void next() {
        if (pService != null && pService.getMeta() != null) pService.setNext();
    }

    public void prev() {
        if (pService != null && pService.getMeta() != null) pService.setPrev();
    }
}
