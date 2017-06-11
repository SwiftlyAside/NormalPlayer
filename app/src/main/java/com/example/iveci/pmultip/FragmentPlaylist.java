package com.example.iveci.pmultip;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
    LinearLayout linear;
    ImageButton back;
    ListView listView;
    RecyclerView recyclerView;
    ArrayList<String> list = new ArrayList<>();
    ArrayAdapter<String> adapter;
    MusicAdapter playlistAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View plView = inflater.inflate(R.layout.fragment_playlist, null);
        linear = (LinearLayout) plView.findViewById(R.id.linear);
        listView = (ListView) plView.findViewById(R.id.playlist);
        back = (ImageButton) plView.findViewById(R.id.iback);
        recyclerView = (RecyclerView) plView.findViewById(R.id.mplaylist);
        list.add("새 재생목록 만들기");
        adapter = new ArrayAdapter<>(getActivity(), R.layout.playlist_dropdown, list);
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
}
