package com.example.iveci.pmultip;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;


/**
 * Created by iveci on 2017-06-07.
 * PlaybackService와 다른 Activity간 매개역할을 수행합니다.
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

}
