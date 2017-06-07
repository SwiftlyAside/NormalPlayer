package com.example.iveci.pmultip;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

/*
* Playback
* Description:
* 이 Activity는 음악을 재생합니다.
*
* Functions:
* 음악 재생/일시정지
* 트래킹바로 위치변경
* 이전/다음 곡 전환
*
* Used other libraries:
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
    private ArrayList<Meta> m_musics;
    private ContentResolver resolver;
    private int pos = 0;
    boolean play = false;

    //UI처리 Thread

    class mps extends Thread { //재생중일 때, 탐색바를 움직이는 thread를 생성합니다.
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
                            int spsc = spos%60000;
                            nowpos.setText(spos/60000+":"+spsc/10000+""+(spsc%10000)/1000);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }
    }

    //미디어처리

    @Override
    protected void onDestroy() {
        super.onDestroy();
        play = false;
        if(playback != null) {
            playback.stop();
            playback.release();
            playback = null;
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
        sinfo.setSelected(true);
        ainfo.setSelected(true);
        Intent intent = getIntent();
        pos = intent.getIntExtra("pos", 0);
        m_musics = (ArrayList<Meta>) intent.getSerializableExtra("playlist");
        resolver = getContentResolver();

        setPlay(m_musics.get(pos));
        new mps().start();

        playback.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (pos < m_musics.size() - 1)
                    setPlay(m_musics.get(++pos));
                else finish();
            }
        });

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

    //플레이어 제어

    public void setPlay(Meta meta) { //메타데이터로 재생합니다.
        try {
            sinfo.setText(meta.getTitle());
            ainfo.setText(meta.getArtist() + " - " + meta.getAlbum());
            Uri musicuri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, meta.getId());
            playback.reset();
            playback.setDataSource(this, musicuri);
            playback.prepare();
            int epos = playback.getDuration();
            timeseek.setProgress(0);
            timeseek.setMax(epos);
            endpos.setText(epos/60000+":"+(epos%60000)/10000+""+((epos%60000)%10000)/1000);
            play = true;
            playback.start();
            timeseek.setVisibility(View.VISIBLE);
            nowpos.setVisibility(View.VISIBLE);
            endpos.setVisibility(View.VISIBLE);
            Bitmap bitmap = BitmapFactory.decodeFile(getAlbumart(Long.parseLong(meta.getAlbumId()),getApplicationContext()));
            album.setImageBitmap(bitmap);
            new mps().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getAlbumart(long albumid, Context context) {
        Cursor album = context.getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums._ID + " = ?",
                new String[]{Long.toString(albumid)},
                null);
        String result = null;
        if (album.moveToFirst())
            result = album.getString(0);
        album.close();
        return result;
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.bprev :{
                /*
                * 이전 버튼입니다.
                *
                *
                * 고려해야 할 사항.
                *
                * 현재 재생 위치가 임계값 미만이면서 이전곡이 존재하는 경우
                *   이전 곡으로.
                * 그렇지 않은 경우
                *   재생중이었던 경우
                *       처음 위치에서 재생을 재개한다
                *   그렇지 않은 경우 처음위치로만 간다
                * */
                if ((float) (timeseek.getProgress())/(float)(timeseek.getMax()) < 0.15 && pos > 0) { //이전 곡으로 가야한다.
                    setPlay(m_musics.get(--pos));
                }
                else{ //처음 위치로 돌아가기 전에
                    if (playback.isPlaying()) { //재생 중이었다면 처음위치에서 재생을 재개한다.
/*                        play = false;
                        playback.pause();*/
                        playback.seekTo(0);
/*                        play = true;
                        playback.start();
                        new mps().start();*/
                    }
                    else { //그렇지 않은 경우 처음 위치로만 간다.
                        playback.seekTo(0);
                    }

                }
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
                if(!playback.isPlaying() && playback.getCurrentPosition() != 0){ //일시정지일때
                    play = true;
                    playback.start();
                    new mps().start();
                    iplay.setImageResource(R.drawable.pause);
                }
                else if(!playback.isPlaying()) { //플레이 안하고 있을때
                    play = true;
                    playback.start();
                    int epos = playback.getDuration();
                    timeseek.setMax(epos);
                    endpos.setText(epos/60000+":"+(epos%60000)/10000+""+((epos%60000)%10000)/1000);
                    new mps().start();
                    timeseek.setVisibility(View.VISIBLE);
                    nowpos.setVisibility(View.VISIBLE);
                    endpos.setVisibility(View.VISIBLE);
                    iplay.setImageResource(R.drawable.pause);
                }
                else { //플레이중일때
                    play = false;
                    playback.pause();
                    iplay.setImageResource(R.drawable.play);
                }
                break;
            }
            case R.id.bnext :{
                /*
                * 다음 버튼입니다.
                *
                * 고려해야 할 사항.
                * 다음 곡이 남은 경우
                *   다음 곡으로만 간다
                * 없으면 끝낸다
                * */
                if (pos < m_musics.size() - 1)
                    setPlay(m_musics.get(++pos));
                else finish();
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
}
