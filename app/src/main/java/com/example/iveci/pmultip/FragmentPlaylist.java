package com.example.iveci.pmultip;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

/* *
 * Created by iveci on 2017-06-11.
 * Explorer
 * Description:
 * 이 Activity는 재생목록 탐색과 생성을 담당합니다.
 *
 * Functions:
 * 재생목록 표시
 *
 * */

public class FragmentPlaylist extends Fragment {
    private final static int LOAD = 0x501;
    LinearLayout linear;
    ImageButton back;
    ListView listView;
    RecyclerView recyclerView;
    ArrayList<Playlist> plist = new ArrayList<>();
    ArrayList<String> list = new ArrayList<>();
    ArrayAdapter<Playlist> adapter;
    MusicAdapter playlistAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View plView = inflater.inflate(R.layout.fragment_playlist, null);
        linear = (LinearLayout) plView.findViewById(R.id.linear);
        listView = (ListView) plView.findViewById(R.id.playlist);
        back = (ImageButton) plView.findViewById(R.id.iback);
        recyclerView = (RecyclerView) plView.findViewById(R.id.mplaylist);
        plist.add(new Playlist(null,"새 재생목록 만들기"));
        adapter = new ArrayAdapter<>(getActivity(), R.layout.playlist_dropdown, plist);
        listView.setAdapter(adapter);
        //클릭시 재생목록 내용을 보여준다. Explorer
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    final EditText listname = new EditText(getContext());
                    AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
                    dlg.setIcon(R.drawable.plus)
                            .setTitle("재생목록 생성")
                            .setMessage("\n생성할 재생목록 이름을 입력하세요.")
                            .setCancelable(true)
                            .setView(listname)
                            .setPositiveButton("생성", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
//생성
                                }
                            })
                            .setNegativeButton("취소", null)
                            .show();
                }
                else {
                    listView.setVisibility(View.INVISIBLE);
                    linear.setVisibility(View.VISIBLE);
                    //내용 쿼리후 보여줄것
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.setVisibility(View.VISIBLE);
                linear.setVisibility(View.INVISIBLE);
            }
        });
        playlistAdapter = new MusicAdapter(getActivity(), null);
        recyclerView.setAdapter(playlistAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        return plView;
    }

    public void getPlaylist() { //재생목록을 가져옵니다.
        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        String[] proj = {
                MediaStore.Audio.Playlists._ID, MediaStore.Audio.Playlists.NAME};
        String order = MediaStore.Audio.Playlists.NAME + " COLLATE LOCALIZED ASC";
        Cursor cursor = getActivity().getContentResolver().query(uri,proj,null,null,order);
        while (cursor.moveToNext()) {
            plist.add(Playlist.setByCursor(cursor));
        }
        cursor.close();
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
                playlistAdapter.swapCursor(data);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                playlistAdapter.swapCursor(null);
            }
        });
    }
}
