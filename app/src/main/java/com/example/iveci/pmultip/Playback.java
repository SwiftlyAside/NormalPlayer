package com.example.iveci.pmultip;

import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

/*
* Playback
* Description:
* 음악재생 UI입니다.
*
* Functions:
* 음악 재생/일시정지
* 트래킹바로 위치변경
* 이전/다음 곡 전환
*
*
* ETC:
* 이 프로그램에서 상단바는 쓰지 않음.
* 
* */
public class Playback extends AppCompatActivity {
    MediaPlayer playback = new MediaPlayer();
    ImageView album;
    ImageButton iplay;
    SeekBar timeseek;
    TextView sinfo, ainfo, nowpos, endpos;
    boolean play = false;

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

    //재생중일 때, 탐색바를 움직이는 thread를 생성합니다.
    class mps extends Thread {
        @Override
        public void run() {
            while(play){
                final int spos = playback.getCurrentPosition();
                timeseek.setProgress(spos);
                try {
                    sleep(300); //탐색바 갱신주기(ms단위). (100~1000) 짧으면 리소스사용량 상승, 길면 반응지연시간 상승.
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            nowpos.setText(DateFormat.format("mm:ss", spos));
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) { //초기화
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        album    = (ImageView)findViewById(R.id.ialbumart);
        iplay    = (ImageButton)findViewById(R.id.bstst);
        timeseek = (SeekBar) findViewById(R.id.timeseek);
        sinfo    = (TextView) findViewById(R.id.songinfo);
        ainfo    = (TextView) findViewById(R.id.ars_albinfo);
        nowpos   = (TextView) findViewById(R.id.snowpos);
        endpos   = (TextView) findViewById(R.id.sendpos);
        registerBroadCast();
        sinfo.setSelected(true);
        ainfo.setSelected(true);

        timeseek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seekBar.getMax() == progress)
                    play = false;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                playback.seekTo(seekBar.getProgress());
            }
        });
    }

    //UI를 새로고칩니다.
    public void refresh() {
        if (MusicApplication.getInstance().getManager().isReady()) {
            iplay.setImageResource(R.drawable.pause);
            new mps().start();
        }
        else
            iplay.setImageResource(R.drawable.play);
        Meta meta = MusicApplication.getInstance().getManager().getMeta();
        if (meta != null) {
            Uri albumart = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), Long.parseLong(meta.getAlbumId()));
            Picasso.with(getApplicationContext()).load(albumart).error(R.drawable.nothing).into(album);
            sinfo.setText(meta.getTitle());
            ainfo.setText(meta.getArtist() + " - " + meta.getAlbum());
            int epos = meta.getDuration();
            timeseek.setVisibility(View.VISIBLE);
            timeseek.setProgress(0);
            timeseek.setMax(epos);
            endpos.setText(DateFormat.format("mm:ss", epos));
        }
        else {
            album.setImageResource(R.drawable.nothing);
            sinfo.setText("");
            ainfo.setText("");
            timeseek.setVisibility(View.INVISIBLE);
        }
    }

    //플레이어 제어

    public void onClick(View v){
        switch (v.getId()){
            case R.id.bprev :{
                /*
                * 이전 버튼입니다.
                * */
                MusicApplication.getInstance().getManager().prev();
                break;
            }
            case R.id.bstst :{
                /*
                재생/일시정지 버튼입니다.

                고려해야 할 사항.

                플레이어 준비가 안된 상태일때 (추가필요)
                준비는 되어있으나 플레이 안하고 있을때
                일시정지일때
                플레이중일때
                 */
                MusicApplication.getInstance().getManager().toggle();
                break;
            }
            case R.id.bnext :{
                /*
                * 다음 버튼입니다.
                * */
                MusicApplication.getInstance().getManager().next();
                break;
            }
            case R.id.repeat :{
                /*
                * 반복 버튼입니다.
                *
                * 고려해야 할 사항.
                *
                * 반복이 아닌 상태인 경우
                *   반복으로 하고 활성 상태로 표시
                * 그렇지 않은 경우
                *   반복을 해제하고 비활성 상태로 표시
                * */
                break;
            }
            case R.id.shuffle :{
                /*
                * 섞기 버튼입니다.
                *
                * 고려해야 할 사항.
                *
                * 상동.
                * 섞는 방법만 고민할것.
                * */
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
