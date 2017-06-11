package com.example.iveci.pmultip;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by iveci on 2017-06-11.
 */

public class FragmentStreaming extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View stream = inflater.inflate(R.layout.fragment_streaming, null);

        return stream;
    }
}
