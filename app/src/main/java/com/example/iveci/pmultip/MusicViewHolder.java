package com.example.iveci.pmultip;

import android.content.ContentUris;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * Created by iveci on 2017-06-08.
 */

public class MusicViewHolder extends RecyclerView.ViewHolder {
    private final Uri uri = Uri.parse("content://media/external/audio/albumart/");
    private TextView song, artist;
    private ImageView aAlbumart;
    Meta meta;
    int viewpos;

    MusicViewHolder(View itemView) {
        super(itemView);
        song = (TextView) itemView.findViewById(R.id.tsongname);
        artist = (TextView) itemView.findViewById(R.id.tartist);
        aAlbumart = (ImageView) itemView.findViewById(R.id.listalbart);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void setItem(Meta m_meta, int position) {
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
