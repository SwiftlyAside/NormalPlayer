package com.example.iveci.pmultip;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/* *
 * Created by iveci on 2017-06-11.
 * Explorer
 * Description:
 * 이 Activity는 재생목록 탐색과 생성, 삭제를 담당합니다.
 *
 * Functions:
 * 재생목록 표시
 *
 * */

public class FragmentPlaylist extends Fragment {
    Context appContext = Tab.getContextOfApplication();
    private final static int LOAD = 0x501;
    long pid;
    LinearLayout linear;
    ImageButton back;
    TextView playlisttitle;
    ListView listView;
    RecyclerView recyclerView;
    ArrayList<Playlist> plist =new ArrayList<>();
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
        playlisttitle = (TextView) plView.findViewById(R.id.tvtitle);
        initplaylist();
        initmembers();
        return plView;
    }

    //재생목록UI 초기화
    public void initplaylist(){
        getPlaylist();
        adapter = new ArrayAdapter<>(getContext(), R.layout.playlist_dropdown, plist);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    final EditText listname = new EditText(getContext());
                    AlertDialog.Builder dlg = new AlertDialog.Builder(getContext());
                    dlg.setIcon(R.drawable.plus)
                            .setTitle("재생목록 생성")
                            .setMessage("\n생성할 재생목록 이름을 입력하세요.")
                            .setCancelable(true)
                            .setView(listname)
                            .setPositiveButton("생성", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    createPlaylist(listname.getText().toString());
                                    getPlaylist();
                                    adapter.notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("취소", null)
                            .show();
                }
                else {
                    playlisttitle.setText(plist.get(position).getName());
                    getPlaylistMember(plist.get(position));
                    playlistAdapter.notifyDataSetChanged();
                    Log.d("갱신보냄","");
                    listView.setVisibility(View.INVISIBLE);
                    linear.setVisibility(View.VISIBLE);
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(getContext());
                dlg.setTitle("재생목록 삭제")
                        .setIcon(R.drawable.delete)
                        .setMessage("이 재생목록을 삭제합니다. 계속하시겠습니까?")
                        .setCancelable(true)
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deletePlaylist(plist.get(position));
                                getPlaylist();
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("아니오", null)
                        .show();
                return true;
            }
        });
    }

    //재생목록 내용UI 초기화
    public void initmembers(){
        playlistAdapter = new MusicAdapter(getContext(), null);
        recyclerView.setAdapter(playlistAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.setVisibility(View.VISIBLE);
                linear.setVisibility(View.INVISIBLE);
            }
        });
    }

    //모든 재생목록을 가져옵니다.
    public void getPlaylist() {
        plist.clear();
        plist.add(new Playlist(-1,"새 재생목록 만들기"));
        String[] proj = {
                MediaStore.Audio.Playlists._ID, MediaStore.Audio.Playlists.NAME};
        String order = MediaStore.Audio.Playlists.NAME + " COLLATE LOCALIZED ASC";
        Cursor cursor = appContext.getContentResolver().query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI
                ,proj,null,null,order);
        if (cursor.getCount() >= 1) {
            for (boolean exists = cursor.moveToFirst(); exists; exists = cursor.moveToNext()) {
                Playlist pl = Playlist.setByCursor(cursor);
                plist.add(pl);
                Log.d("NAME: ",cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.NAME)));
            }
        }
        cursor.close();
    }

    //재생목록을 삭제합니다.
    public void deletePlaylist(Playlist pl) {
        Uri puri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        String where = MediaStore.Audio.Playlists._ID + " = ?";
        String[] arg = {pl.getId()+""};
        appContext.getContentResolver().delete(puri, where, arg);
        Toast.makeText(getContext(),"삭제되었습니다.", Toast.LENGTH_SHORT).show();
    }

    //재생목록을 생성합니다. 생성 성공여부를 메시지로 출력합니다.
    public void createPlaylist(String name) {
        try {
            for (Playlist p : plist) {
                if (p.getName().equalsIgnoreCase(name)) throw new Exception("동일한 이름 존재.");
            }
            ContentValues values = new ContentValues();
            values.put(MediaStore.Audio.Playlists.NAME, name);
            values.put(MediaStore.Audio.Playlists.DATE_ADDED, System.currentTimeMillis());
            values.put(MediaStore.Audio.Playlists.DATE_MODIFIED, System.currentTimeMillis());
            appContext.getContentResolver().insert(
                    MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values);
            Toast.makeText(appContext, "재생목록 "+name+" 을 생성했습니다.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(appContext, "재생목록 생성에 실패했습니다.\n" +
                    e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    //선택한 재생목록의 내용을 가져옵니다. 어댑터로 전송.
    public void getPlaylistMember(Playlist pl) {
        pid = pl.getId();
        if (!getLoaderManager().hasRunningLoaders()) getLoaderManager().initLoader(LOAD, null, plload);
    }

    LoaderManager.LoaderCallbacks<Cursor> plload = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", pid);
            String[] proj0 = new String[] {MediaStore.Audio.Playlists.Members.AUDIO_ID};
            Cursor member = appContext.getContentResolver().query(uri, proj0,null,null,null);
            if (member.moveToFirst()) {
                String[] proj = new String[] {
                        MediaStore.Audio.Playlists.Members._ID, MediaStore.Audio.Playlists.Members.ALBUM_ID,
                        MediaStore.Audio.Playlists.Members.TITLE, MediaStore.Audio.Playlists.Members.ALBUM,
                        MediaStore.Audio.Playlists.Members.ARTIST, MediaStore.Audio.Playlists.Members.DURATION};
                String select = MediaStore.Audio.Media._ID + " = ?";
                long aid = member.getLong(member.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID));
                String[] arg = {""+aid};
                return new CursorLoader(getActivity(), MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        ,proj,select,arg, null);
            }
            return null;

        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            playlistAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            playlistAdapter.swapCursor(null);
        }
    };
}
