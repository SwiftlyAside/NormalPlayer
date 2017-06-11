package com.example.iveci.pmultip;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by iveci on 2017-06-11.
 */

public class FragmentPlaylist extends Fragment {
    ListView listView;
    ArrayList<String> list = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View plView = inflater.inflate(R.layout.fragment_playlist, null);
        listView = (ListView) plView.findViewById(R.id.playlist);
        list.add("구 현 중");
        adapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, list);
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
