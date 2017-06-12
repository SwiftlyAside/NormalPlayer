package com.example.iveci.pmultip;

import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;


/*
* Explorer
* Description:
* 이 Activity는 음악(곡명순) 탐색 UI입니다.
*
* Functions:
* 음악목록 표시
*
* */

public class FragmentExplorer extends Fragment {
    private final static int LOAD = 0x907;
    ImageView album;
    ImageButton pp;
    TextView songname;
    RecyclerView rView;
    MusicAdapter adapter;

    //Service로부터 메시지를 받습니다.
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh();
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
        View explorer = inflater.inflate(R.layout.fragment_explorer, null);
        getMeta();
        album = (ImageView) explorer.findViewById(R.id.imalbumart);
        songname = (TextView) explorer.findViewById(R.id.tvmsongn);
        pp = (ImageButton) explorer.findViewById(R.id.implay);
        rView = (RecyclerView) explorer.findViewById(R.id.mlist);
        adapter = new MusicAdapter(getActivity(), null);
        rView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rView.setLayoutManager(layoutManager);
        registerBroadCast();
        refresh();
        return explorer;
    }

    //UI를 새로고칩니다.
    public void refresh() {
        if (MusicApplication.getInstance().getManager().isPlaying()) {
            pp.setImageResource(R.drawable.pause);
        }
        else {
            pp.setImageResource(R.drawable.play);

        }
        Meta meta = MusicApplication.getInstance().getManager().getMeta();
        if (meta != null) {
            Uri albumart = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), Long.parseLong(meta.getAlbumId()));
            Picasso.with(getContext()).load(albumart).error(R.drawable.nothing).into(album);
            songname.setText(meta.getTitle());
        }
        else {
            album.setImageResource(R.drawable.nothing);
            songname.setText("음악을 선택하면 재생합니다.");
        }
    }

    public void getMeta() { //로컬 미디어 데이터베이스에서 음악의 메타데이터를 가져옵니다. 어댑터로 전송
        getLoaderManager().initLoader(LOAD, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] proj = {
                        MediaStore.Audio.Media._ID,     MediaStore.Audio.Media.ALBUM_ID,
                        MediaStore.Audio.Media.TITLE,   MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.ARTIST,  MediaStore.Audio.Media.DURATION};
                String select = MediaStore.Audio.Media.IS_MUSIC + " = 1";
                String order  = MediaStore.Audio.Media.TITLE + " COLLATE LOCALIZED ASC";
                return new CursorLoader(getContext(),
                        uri, proj, select, null, order);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                adapter.swapCursor(data);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                adapter.swapCursor(null);
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().unregisterReceiver(broadcastReceiver);
    }
}
