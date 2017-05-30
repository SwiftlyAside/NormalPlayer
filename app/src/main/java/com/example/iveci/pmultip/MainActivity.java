package com.example.iveci.pmultip;

import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

/*
* MultiP
* Description:
* 이 어플리케이션은 음악을 재생합니다.
*
* Functions:
* 음악 재생/일시정지 (중지 없음?)
*
* Used other libraries:
*
* ETC:
* 이 프로그램에서 상단바는 쓰지 않음.
* 
* */
public class MainActivity extends AppCompatActivity {
    MediaPlayer playback = new MediaPlayer();
    ImageView albart;
    SeekBar timeseek;
    TextView sinfo, ainfo, startpos, endpos;
    private String MP = getExternalPath();
    private ArrayList<String> musics = new ArrayList<>();
    private ArrayAdapter<String> mlist;
    boolean play = false;

    class mps extends Thread { //재생중일 때, 탐색바를 움직이는 thread를 생성합니다.
        @Override
        public void run() {
            while(play){
                final int spos = playback.getCurrentPosition();
                timeseek.setProgress(spos);
                try {
                    sleep(1000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int spsc = spos%60000;
                            startpos.setText(spos/60000+":"+spsc/10000+""+(spsc%10000)/1000);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }
    }

    class MFilter implements FilenameFilter { //음악파일만 반환하는 기능이 있는 클래스를 생성합니다.

        @Override
        public boolean accept(File dir, String name) {
            return (name.endsWith(".mp3") || name.endsWith(".m4a")|| name.endsWith(".wav")
                ||  name.endsWith(".flac")|| name.endsWith(".ogg"));
        }
    }

    public String getExternalPath(){
        String sdPath ="";
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
        timeseek = (SeekBar) findViewById(R.id.timeseek);
        sinfo    = (TextView) findViewById(R.id.songinfo);
        ainfo    = (TextView) findViewById(R.id.ars_albinfo);
        startpos = (TextView) findViewById(R.id.sstartpos);
        endpos   = (TextView) findViewById(R.id.sendpos);
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
        if (musics.size() > 0){
            try {
                playback.setDataSource(MP+musics.get(0));
                playback.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
                * 현재 재생 위치가 임계값 미만인 경우
                * 그렇지 않은 경우
                * */
                break;
            }
            case R.id.bstst :{
                /*
                재생/일시정지 버튼입니다.

                고려해야 할 사항.

                플레이 안하고 있을때
                일시정지일때
                플레이중일때
                 */
                if(!playback.isPlaying() && playback.getCurrentPosition() != 0){ //일시정지일때
                    play = true;
                    playback.start();
                    new mps().start();
                }
                else if(!playback.isPlaying()) { //플레이 안하고 있을때
                    playback.setLooping(false);
                    play = true;
                    playback.start();
                    int epos = playback.getDuration();
                    timeseek.setMax(epos);
                    endpos.setText(epos/60000+":"+(epos%60000)/10000+""+((epos%60000)%10000)/1000);
                    new mps().start();
                    startpos.setVisibility(View.VISIBLE);
                    endpos.setVisibility(View.VISIBLE);
                }
                else { //플레이중일때
                    play = false;
                    playback.pause();
                }
                break;
            }
            case R.id.bnext :{
                /*
                * 다음 버튼입니다.
                *
                * 고려해야 할 사항.
                *
                * 현재 재생 위치가 임계값 이상인 경우
                * 그렇지 않은 경우
                * */
                break;
            }
        }
    }
}
