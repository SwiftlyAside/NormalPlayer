package com.ivsa.normalplayer;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


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
    RecyclerView rView;
    MusicAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View explorer = inflater.inflate(R.layout.fragment_explorer, null);
        getMeta();
        rView = explorer.findViewById(R.id.mlist);
        adapter = new MusicAdapter(getActivity(), null);
        rView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rView.setLayoutManager(layoutManager);
        return explorer;
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
}
