package com.example.iveci.pmultip;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by iveci on 2017-06-05.
 *
 * MusicAdapter
 *
 * Description:
 * RecyclerView의 어댑터입니다. Cursor를 이용해 리스트정보를 주고받습니다.
 * 이 Adapter는 CursorRecyclerViewAdapter를 이용하였습니다.
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class MusicAdapter extends CursorRecyclerViewAdapter<RecyclerView.ViewHolder> {

    public MusicAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.music, parent, false);
        return new MusicViewHolder(v);
    }

    //RecyclerView 정보를 갱신합니다.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        Meta meta = Meta.setByCursor(cursor);
        ((MusicViewHolder) viewHolder).setItem(meta, cursor.getPosition(), false);
    }

    public ArrayList<Long> getMusicIds() {
        int count = getItemCount();
        ArrayList<Long> musicids =  new ArrayList<>();
        for (int i = 0; i < count; i++) {
            musicids.add(getItemId(i));
        }
        return musicids;
    }

    public class MusicViewHolder extends RecyclerView.ViewHolder {
        private final Uri uri = Uri.parse("content://media/external/audio/albumart/");
        private TextView song, artist;
        private ImageView aAlbumart;
        Meta meta;
        int viewpos;

        MusicViewHolder(final View itemView) {
            super(itemView);
            song = (TextView) itemView.findViewById(R.id.tsongname);
            artist = (TextView) itemView.findViewById(R.id.tartist);
            aAlbumart = (ImageView) itemView.findViewById(R.id.listalbart);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MusicApplication.getInstance().getManager().playList(getMusicIds());
                    MusicApplication.getInstance().getManager().play(viewpos);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder dlg = new AlertDialog.Builder(itemView.getContext());
                    dlg.setTitle("음악 삭제")
                            .setIcon(R.drawable.delete)
                            .setMessage("이 음악을 삭제합니다. 계속하시겠습니까?")
                            .setCancelable(true)
                            .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MusicApplication.getInstance().getManager().delete(viewpos);
                                }
                            })
                            .setNegativeButton("아니오", null)
                            .show();
                    return true;
                }
            });
        }

        public void setItem(Meta m_meta, int position, boolean playing) {
            meta = m_meta;
            viewpos = position;
            song.setText(m_meta.getTitle());
            artist.setText(m_meta.getArtist());
            Uri albumart = ContentUris.withAppendedId(uri, Long.parseLong(m_meta.getAlbumId()));
            Picasso.with(itemView.getContext())
                    .load(albumart)
                    .error(R.drawable.nothing)
                    .into(aAlbumart);
        }
    }
}
