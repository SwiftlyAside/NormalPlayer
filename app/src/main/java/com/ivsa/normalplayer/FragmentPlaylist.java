package com.ivsa.normalplayer;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
    long pid;
    LinearLayout linear;
    ImageButton back;
    TextView playlisttitle;
    ListView listView, metaview;
    ArrayList<Playlist> plist =new ArrayList<>();
    ArrayList<Meta> metas = new ArrayList<>();
    ArrayAdapter<Playlist> adapter;
    ArrayAdapter<Meta> metaArrayAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View plView = inflater.inflate(R.layout.fragment_playlist, null);
        linear = plView.findViewById(R.id.linear);
        listView = plView.findViewById(R.id.playlist);
        back = plView.findViewById(R.id.iback);
        metaview = plView.findViewById(R.id.mplaylist);
        playlisttitle = plView.findViewById(R.id.tvtitle);
        initplaylist();
        initmembers();
        return plView;
    }

    //재생목록UI 초기화
    public void initplaylist(){
        getPlaylist();
        adapter = new ArrayAdapter<>(getContext(), R.layout.playlist_dropdown, plist);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (position == 0) {
                final EditText listname = new EditText(getContext());
                AlertDialog.Builder dlg = new AlertDialog.Builder(getContext());
                dlg.setIcon(R.drawable.plus)
                        .setTitle("재생목록 생성")
                        .setMessage("\n생성할 재생목록 이름을 입력하세요.")
                        .setCancelable(true)
                        .setView(listname)
                        .setPositiveButton("생성", (dialog, which) -> {
                            createPlaylist(listname.getText().toString());
                            getPlaylist();
                            adapter.notifyDataSetChanged();
                        })
                        .setNegativeButton("취소", null)
                        .show();
            } else {
                playlisttitle.setText(plist.get(position).getName());
                getPlaylistMember(plist.get(position));
                metaArrayAdapter.notifyDataSetChanged();
                listView.setVisibility(View.INVISIBLE);
                linear.setVisibility(View.VISIBLE);
            }
        });
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            if (position != 0) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(getContext());
                dlg.setTitle("재생목록 삭제")
                        .setIcon(R.drawable.delete)
                        .setMessage("재생목록 " + plist.get(position) + "을(를) 삭제합니다.\n계속하시겠습니까?")
                        .setCancelable(true)
                        .setPositiveButton("네", (dialog, which) -> {
                            deletePlaylist(plist.get(position));
                            getPlaylist();
                            adapter.notifyDataSetChanged();
                        })
                        .setNegativeButton("아니오", null)
                        .show();
            }
            return true;
        });
    }

    //재생목록 내용UI 초기화
    public void initmembers() {
        metaArrayAdapter = new ArrayAdapter<>(getContext(), R.layout.playlist_dropdown, metas);
        metaview.setAdapter(metaArrayAdapter);
        metaview.setOnItemClickListener((parent, view, position, id) -> {
            MusicApplication.getInstance().getManager().playlistset(metas);
            MusicApplication.getInstance().getManager().play(position);
        });
        metaview.setOnItemLongClickListener((parent, view, position, id) -> {
            AlertDialog.Builder dlg = new AlertDialog.Builder(getContext());
            dlg.setTitle("재생목록에서 음악 제거")
                    .setIcon(R.drawable.delete)
                    .setMessage("다음 음악을 재생목록에서 제거합니다.\n\n" +
                            metas.get(position) +
                            "\n\n계속하시겠습니까?")
                    .setCancelable(true)
                    .setPositiveButton("네", (dialog, which) -> {
                        deleteTrack(pid, metas.get(position));
                        getPlaylistMember(null);
                        metaArrayAdapter.notifyDataSetChanged();
                    })
                    .setNegativeButton("아니오", null)
                    .show();
            return true;
        });
        back.setOnClickListener(v -> {
            listView.setVisibility(View.VISIBLE);
            linear.setVisibility(View.INVISIBLE);
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
            }
        }
        cursor.close();
    }

    //재생목록에서 음악을 제거합니다.
    public void deleteTrack(long playlistid, Meta meta) {
        try {
            Uri puri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistid);
            String where = MediaStore.Audio.Playlists.Members._ID + " = ?";
            String audioid = meta.getMemberId();
            String[] arg = {audioid};
            appContext.getContentResolver().delete(puri, where, arg);
            Toast.makeText(getContext(), "제거되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "제거하지 못했습니다.\n"+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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

    //선택한 재생목록의 내용을 가져옵니다.
    public void getPlaylistMember(@Nullable Playlist pl) {
        metas.clear();
        if (pl != null) pid = pl.getId();
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", pid);
        String[] project = new String[]{
                MediaStore.Audio.Playlists.Members.AUDIO_ID, MediaStore.Audio.Playlists.Members._ID,
                MediaStore.Audio.Playlists.Members.ALBUM_ID,
                MediaStore.Audio.Playlists.Members.TITLE, MediaStore.Audio.Playlists.Members.ALBUM,
                MediaStore.Audio.Playlists.Members.ARTIST, MediaStore.Audio.Playlists.Members.DURATION};
        Cursor member = appContext.getContentResolver().query(uri, project, null, null, null);
        assert member != null;
        if (member.getCount() >= 1) {
            for (boolean exists = member.moveToFirst(); exists; exists = member.moveToNext()) {
                Meta meta = Meta.setByCursor(member);
                metas.add(meta);
            }
        }
        member.close();
    }

}
