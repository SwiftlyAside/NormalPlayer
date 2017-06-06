package com.example.iveci.pmultip;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
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
public class Playback_old extends AppCompatActivity {
    MediaPlayer playback;
    ImageView album;
    ImageButton iplay;
    SeekBar timeseek;
    TextView sinfo, ainfo, nowpos, endpos;
    private String MP = getExternalMediaPath();
    private ArrayList<String> musics = new ArrayList<>();
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
                    sleep(110); //탐색바 갱신주기(ms단위). (50~1000) 짧으면 리소스사용량 상승, 길면 반응지연시간 상승.
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

    class MFilter implements FilenameFilter { //음악파일만 반환하는 기능이 있는 클래스를 생성합니다.

        @Override
        public boolean accept(File dir, String name) {
            return (name.endsWith(".mp3") || name.endsWith(".m4a")|| name.endsWith(".wav")
                ||  name.endsWith(".flac")|| name.endsWith(".ogg"));
        }
    }

    public String getExternalMediaPath(){ //SD카드 미디어폴더의 위치를 가져옵니다.
        String sdPath;
        String ext = Environment.getExternalStorageState();
        if(ext.equals(Environment.MEDIA_MOUNTED)){
            sdPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
                    .getAbsolutePath() + "/";
        }
        else sdPath = getFilesDir() + "";
        return sdPath;
    }

    public void refresh(){ //재생목록을 갱신합니다.
        File exdir = new File(MP);
        if (exdir.listFiles(new MFilter()).length > 0) {
            for (File file:exdir.listFiles(new MFilter())){
                musics.add(file.getName());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) { // 초기화, SD카드 미디어폴더접근권한을 확인합니다.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //권한체크
        int permissioninfo = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissioninfo == PackageManager.PERMISSION_GRANTED)
            refresh();
        else {
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

        album    = (ImageView)findViewById(R.id.ialbumart);
        iplay    = (ImageButton)findViewById(R.id.bstst);
        timeseek = (SeekBar) findViewById(R.id.timeseek);
        sinfo    = (TextView) findViewById(R.id.songinfo);
        ainfo    = (TextView) findViewById(R.id.ars_albinfo);
        nowpos   = (TextView) findViewById(R.id.snowpos);
        endpos   = (TextView) findViewById(R.id.sendpos);
        Intent intent = getIntent();
        pos = intent.getIntExtra("pos", 0);
        m_musics = (ArrayList<Meta>) intent.getSerializableExtra("playlist");
        resolver = getContentResolver();

        if (musics.size() > 0){
            try {
                playback = new MediaPlayer();
                playback.setDataSource(MP+musics.get(pos));
                playback.prepare();
                playback.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        setPlayNext(mp);
                    }
                });
                timeseek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (seekBar.getMax()==progress) {
                            play = false;
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        play = false;
                        playback.pause();
                        playback.seekTo(seekBar.getProgress());
                        play = true;
                        playback.start();
                        new mps().start();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    //플레이어 제어

    public void setPlayNext(MediaPlayer mp){ //다음 곡을 있으면 재생합니다.
        if (musics.size()-1 > pos) { //다음 곡이 있으면
            try {
                mp.reset();
                mp.setDataSource(MP+musics.get(++pos));
                mp.prepare();
                int epos = mp.getDuration();
                timeseek.setProgress(0);
                timeseek.setMax(epos);
                endpos.setText(epos/60000+":"+(epos%60000)/10000+""+((epos%60000)%10000)/1000);
                play = true;
                mp.start();
                new mps().start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else { //다음 곡이 없으면
            if (!mp.isLooping()) mp.stop();
            else {
                timeseek.setVisibility(View.INVISIBLE);
                nowpos.setVisibility(View.INVISIBLE);
                endpos.setVisibility(View.INVISIBLE);
            }
            pos = 0;
            mp.reset();
        }
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
                    Log.d("its under","15!");
                }
                else{ //처음 위치로 돌아가기 전에
                    if (playback.isPlaying()) { //재생 중이었다면 처음위치에서 재생을 재개한다.
                        play = false;
                        playback.pause();
                        playback.seekTo(0);
                        play = true;
                        playback.start();
                        new mps().start();
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

                    playback.setLooping(false);
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
                *
                * 다음곡이 없는 경우 재생을 종료한다
                * 그렇지 않은 경우
                *   재생중이었던 경우
                *       다음 곡을 바로 재생한다
                *   그렇지 않은 경우 다음 곡으로만 간다 (이부분만 이 문단에서 설계할것)
                * */
                if (!playback.isPlaying()) {

                }
                else setPlayNext(playback);
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
