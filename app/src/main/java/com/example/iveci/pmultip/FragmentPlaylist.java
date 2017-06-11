package com.example.iveci.pmultip;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    ListView listView;
    RecyclerView recyclerView;
    ArrayList<String> list = new ArrayList<>();
    ArrayAdapter<String> adapter;
    MusicAdapter playlistAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View plView = inflater.inflate(R.layout.fragment_playlist, null);
        listView = (ListView) plView.findViewById(R.id.playlist);
        recyclerView = (RecyclerView) plView.findViewById(R.id.mplaylist);
        list.add("새 재생목록 만들기");
        adapter = new ArrayAdapter<>(getActivity(), R.layout.playlist_dropdown, list);
        listView.setAdapter(adapter);
        //클릭시 재생목록 내용을 보여준다. Explorer
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        return plView;
    }
}
