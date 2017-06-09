package com.example.iveci.pmultip;

import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;


/*
* Explorer
* Description:
* 이 Activity는 재생목록 탐색과 생성을 담당합니다.
*
* Functions:
* 재생목록 표시
* 재생목록 생성(미구현)
* 재생목록 검색(미구현)
* 분류화(앨범/아티스트) (미구현)
*
* */

public class Explorer extends AppCompatActivity {
    private final static int LOAD = 0x907;
    ImageView album;
    ImageButton pp;
    TextView songname;
    RecyclerView rView;
    MusicAdapter adapter;

    //Service로부터 메시지를 받습니다.
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh();
        }
    };

    public void registerBroadCast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(PlaybackService.CHANGE);
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer);
        //권한체크
        int permissioninfo = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissioninfo == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                Toast.makeText(getApplicationContext(),
                        "SDCard 쓰기 권한이 필요합니다. \n" + "설정에서 수동으로 활성화해주세요.",Toast.LENGTH_SHORT).show();
            }
            else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            }
        }
        else {
            getMeta();
            album = (ImageView) findViewById(R.id.imalbumart);
            songname = (TextView) findViewById(R.id.tvmsongn);
            pp = (ImageButton) findViewById(R.id.implay);
            rView = (RecyclerView) findViewById(R.id.mlist);
            adapter = new MusicAdapter(this, null);
            rView.setAdapter(adapter);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            rView.setLayoutManager(layoutManager);
            registerBroadCast();
            refresh();
        }

    }

    //UI를 새로고칩니다.
    public void refresh() {
        if (MusicApplication.getInstance().getManager().isPlaying()) {
            pp.setImageResource(R.drawable.pause);
        }
        else {
            pp.setImageResource(R.drawable.play);

        }
        Meta meta = MusicApplication.getInstance().getManager().getMeta();
        if (meta != null) {
            Uri albumart = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), Long.parseLong(meta.getAlbumId()));
            Picasso.with(getApplicationContext()).load(albumart).error(R.drawable.nothing).into(album);
            songname.setText(meta.getTitle());
        }
        else {
            album.setImageResource(R.drawable.nothing);
            songname.setText("음악을 선택하면 재생합니다.");
        }
    }

    public void onClick(View v) {
        switch (v.getId()){
            //플레이어 Activity 보이기
            case R.id.smallplay :{
                break;
            }
            //재생, 일시정지
            case R.id.implay :{
                MusicApplication.getInstance().getManager().toggle();
                break;
            }
            //다음 곡
            case R.id.imnext :{
                MusicApplication.getInstance().getManager().next();
                break;
            }
        }
    }

    public void getMeta() { //로컬 미디어 데이터베이스에서 음악의 메타데이터를 가져옵니다. 어댑터로 전송.
        getSupportLoaderManager().initLoader(LOAD, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] proj = {
                        MediaStore.Audio.Media._ID,     MediaStore.Audio.Media.ALBUM_ID,
                        MediaStore.Audio.Media.TITLE,   MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.ARTIST,  MediaStore.Audio.Media.DURATION};
                String select = MediaStore.Audio.Media.IS_MUSIC + " = 1";
                String order  = MediaStore.Audio.Media.TITLE + " COLLATE LOCALIZED ASC";
                return new CursorLoader(getApplicationContext(),
                        uri, proj, select, null, order);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                adapter.swapCursor(data);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                adapter.swapCursor(null);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
