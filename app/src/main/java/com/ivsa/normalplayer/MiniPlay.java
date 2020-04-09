package com.ivsa.normalplayer;

import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

/**
 * Miniplayer
 * Description:
 * 이 Fragment는 탐색화면 아래에 표시되는 미니플레이어의 UI입니다.
 */

public class MiniPlay extends Fragment {
    ImageView album;
    ImageButton pp;
    TextView songname;

    //Service로부터 메시지를 받습니다.
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateMiniPlayer();
        }
    };

    //브로드캐스터를 등록합니다.
    public void registerBroadCast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(PlaybackService.CHANGE);
        getActivity().registerReceiver(broadcastReceiver, filter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mini = inflater.inflate(R.layout.fragment_mini, null);
        album = mini.findViewById(R.id.imalbumart);
        songname = mini.findViewById(R.id.tvmsongn);
        pp = mini.findViewById(R.id.implay);
        registerBroadCast();
        updateMiniPlayer();
        return mini;
    }


    //UI를 새로고칩니다.
    public void updateMiniPlayer() {
        if (MusicApplication.getInstance().getManager().isPlaying()) {
            pp.setImageResource(R.drawable.pause);
        } else {
            pp.setImageResource(R.drawable.play);

        }
        Meta meta = MusicApplication.getInstance().getManager().getMeta();
        if (meta != null) {
            Uri albumart = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), Long.parseLong(meta.getAlbumId()));
            Picasso.get().load(albumart).error(R.drawable.nothing).into(album);
            songname.setText(meta.getTitle());
        } else {
            album.setImageResource(R.drawable.nothing);
            songname.setText("음악을 선택하면 재생합니다.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().unregisterReceiver(broadcastReceiver);
    }
}
