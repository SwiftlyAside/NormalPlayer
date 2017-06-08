package com.example.iveci.pmultip;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/*
* Explorer
* Description:
* 이 Activity는 재생목록 탐색과 생성을 담당합니다.
*
* Functions:
* 재생목록 표시
* 재생목록 생성(미구현)
* 재생목록 검색(미구현)
* 분류화(노래/앨범/아티스트) (미구현)
*
* */

public class Explorer extends AppCompatActivity {
    private ServiceConnection serviceConnection;
    private PlaybackService pService;
    ListView listView;
    ArrayList<Meta> musics;
    MusicAdapter adapter;
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
            serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    pService = ((PlaybackService.playbackServicebinder) service).getService();
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    serviceConnection = null;
                    pService = null;
                }
            };
            listView = (ListView) findViewById(R.id.mlist);
            getMeta();
            adapter = new MusicAdapter(this, musics);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(Explorer.this, Playback.class);
                    intent.putExtra("pos", position);
                    intent.putExtra("playlist", musics);
                    startActivity(intent);
                }
            });
        }

    }

    public void getMeta() { //음악의 메타데이터를 가져옵니다.
        musics = new ArrayList<>();
        String[] column = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.ALBUM_ID,
                           MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ALBUM,
                           MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DURATION};

        Cursor cursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, column, null, null, null);

        while (cursor.moveToNext()) {
            Meta meta = new Meta();
            meta.setId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
            meta.setAlbumId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
            meta.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            meta.setAlbum(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
            meta.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
            meta.setDuration(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))); //컬럼인식불가능.
            musics.add(meta);
        }
        cursor.close();
    }
}
