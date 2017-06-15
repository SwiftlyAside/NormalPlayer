package com.ivsa.normalplayer;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.WriteMode;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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

class MusicAdapter extends CursorRecyclerViewAdapter<RecyclerView.ViewHolder> {
    private static final String DROP_BOX_APP_KEY = "31r0voiyhzeaq73";
    private static final String DROP_BOX_APP_SECRET = "eshhoazadkmxl8r";
    private static final String ACCESS_TOKEN = "qNhWX_R5yuYAAAAAAABAeOW8WMF47obUq70jLSRe9Ye41C_GH0VJ2BpxoeMcB7yY";
    private Context appContext = Tab.getContextOfApplication();

    MusicAdapter(Context context, Cursor cursor) {
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
        ((MusicViewHolder) viewHolder).setItem(meta, cursor.getPosition());
    }

    private ArrayList<Long> getMusicIds() {
        int count = getItemCount();
        ArrayList<Long> musicids =  new ArrayList<>();
        for (int i = 0; i < count; i++) {
            musicids.add(getItemId(i));
        }
        return musicids;
    }

    private class MusicViewHolder extends RecyclerView.ViewHolder {
        private final Uri uri = Uri.parse("content://media/external/audio/albumart/");
        private TextView song, artist;
        private ImageView aAlbumart;
        ArrayList<Playlist> plist = new ArrayList<>();
        Meta meta;
        int viewpos;

        MusicViewHolder(final View itemView) {
            super(itemView);
            song = itemView.findViewById(R.id.tsongname);
            artist = itemView.findViewById(R.id.tartist);
            aAlbumart = itemView.findViewById(R.id.listalbart);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MusicApplication.getInstance().getManager().playList(getMusicIds());
                    MusicApplication.getInstance().getManager().play(viewpos);
                }
            });
            //항목을 길게 누르면 나오는 메뉴 추가.
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View v) {
                    PopupMenu p = new PopupMenu(v.getContext(), v);
                    p.getMenuInflater().inflate(R.menu.admenu, p.getMenu());
                    p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            //재생목록에 추가
                            if (item.getItemId() == R.id.addpl) {
                                getPlaylist();
                                AlertDialog.Builder dlg = new AlertDialog.Builder(itemView.getContext());
                                final ArrayAdapter<Playlist> adapter = new ArrayAdapter<>(itemView.getContext(),
                                        R.layout.support_simple_spinner_dropdown_item,plist);
                                dlg.setTitle("음악을 추가할 재생목록 선택.")
                                        .setAdapter(adapter, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Playlist pl = adapter.getItem(which);
                                                addToPlaylist(getMusicIds().get(viewpos), pl.getId());
                                                Toast.makeText(appContext,pl.getName()+"에 추가했습니다.", Toast.LENGTH_SHORT).show();
                                            }})
                                        .setNegativeButton("취소",null)
                                        .show();
                            }
                            else if (item.getItemId() == R.id.uploaddbx) {
                                AlertDialog.Builder dlg = new AlertDialog.Builder(itemView.getContext());
                                dlg.setTitle("Dropbox로 업로드")
                                        .setMessage("이 파일을 업로드합니다. 계속하시겠습니까?")
                                        .setCancelable(true)
                                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                uploadItem();
                                                Toast.makeText(appContext,"업로드 요청을 보냈습니다.\n 네트워크 환경에 따라 시간이 걸릴 수 있습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .setNegativeButton("아니오",null)
                                        .show();

                            }
                            //삭제
                            else {
                                AlertDialog.Builder dlg = new AlertDialog.Builder(itemView.getContext());
                                dlg.setTitle("음악 삭제")
                                        .setIcon(R.drawable.delete)
                                        .setMessage("이 음악을 삭제합니다. 계속하시겠습니까?")
                                        .setCancelable(true)
                                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                deleteItem(getMusicIds().get(viewpos));
                                                notifyDataSetChanged();

                                            }
                                        })
                                        .setNegativeButton("아니오", null)
                                        .show();
                            }
                            return false;
                        }
                    });
                    p.show();
                    return true;
                }
            });
        }

        //재생목록에 음악을 추가합니다.
        private void addToPlaylist(long musicid, long playlistid) {
            Uri puri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistid);
            String[] proj0 = new String[] {MediaStore.Audio.Playlists.Members.PLAY_ORDER};
            Cursor member = appContext.getContentResolver().query(puri, proj0,null,null,null);
            int position = member.getCount();
            ContentValues values = new ContentValues();
            values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, position);
            values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, musicid);
            appContext.getContentResolver().insert(puri, values);
            appContext.getContentResolver().notifyChange(Uri.parse("content://media"),null);
            member.close();
        }

        //모든 재생목록을 가져옵니다.
        private void getPlaylist() {
            plist.clear();
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


        //음악을 삭제합니다.
        private void deleteItem(long id) {
            Cursor cursor = null;
            try {
                Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
                String[] proj = {MediaStore.Audio.Media.DATA};
                cursor = appContext.getContentResolver().query(uri, proj,null,null,null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                    File file = new File(path);
                    appContext.getContentResolver().delete(uri,null,null);
                    file.delete();
                    Toast.makeText(appContext,"삭제하였습니다.", Toast.LENGTH_SHORT).show();
                }
                else throw new NullPointerException();
            } catch (IllegalArgumentException | NullPointerException e) {
                Toast.makeText(appContext,"삭제하지 못했습니다.\n"+e.getMessage(), Toast.LENGTH_SHORT).show();
            } finally {
                if (cursor != null) cursor.close();
            }
        }

        //음악을 드롭박스에 업로드합니다.
        private void uploadItem() {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Cursor cursor = null;
                    final long id = getMusicIds().get(viewpos);
                    try {
                        DbxRequestConfig config = new DbxRequestConfig("dropbox/java-tutorial");
                        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
                        Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
                        String[] proj = {MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DISPLAY_NAME};
                        cursor = appContext.getContentResolver().query(uri, proj,null,null,null);
                        if (cursor != null) {
                            cursor.moveToFirst();
                            String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                            String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                            InputStream in = new FileInputStream(path);
                            client.files().uploadBuilder("/"+name)
                                    .withMode(WriteMode.OVERWRITE)
                                    .uploadAndFinish(in);
                        }
                    } catch (DbxException | IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (cursor != null) cursor.close();
                    }
                }
            });
            thread.start();


        }

        private void setItem(Meta m_meta, int position) {
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
