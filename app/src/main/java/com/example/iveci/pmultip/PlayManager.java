package com.example.iveci.pmultip;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.util.ArrayList;

/**
 * Created by iveci on 2017-06-07.
 * 사용하지 않을 가능성 있음. 서비스 설계 실험용.
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
