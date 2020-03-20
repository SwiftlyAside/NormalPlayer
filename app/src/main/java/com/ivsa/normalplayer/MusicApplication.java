package com.ivsa.normalplayer;

import android.app.Application;

/**
 * Created by iveci on 2017-06-08.
 */

public class MusicApplication extends Application {
    static MusicApplication musicInstance;
    PlayManager manager;

    @Override
    public void onCreate() {
        super.onCreate();
        musicInstance = this;
        manager = new PlayManager(getApplicationContext());
    }

    public static MusicApplication getInstance() {
        return musicInstance;
    }

    public PlayManager getManager() {
        return manager;
    }
}
