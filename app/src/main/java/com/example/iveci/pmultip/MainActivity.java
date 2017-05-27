package com.example.iveci.pmultip;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

/*
* MultiP
* Description:
* 뭔가를 재생하는 프로그램.
* Functions:
* 미확정
*
* Used other libraries:
*
* ETC:
* 이 프로그램에서 상단바는 쓰지 않음.
* 
* */
public class MainActivity extends AppCompatActivity {
    MediaPlayer player;
    ImageView albart;
    SeekBar timeseek;
    TextView sinfo, ainfo;
    boolean play = false;

    class mps extends Thread {
        @Override
        public void run() {
            while(play){ //재생중인 경우, 탐색바를 움직입니다.
                timeseek.setProgress(player.getCurrentPosition());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timeseek = (SeekBar) findViewById(R.id.timeseek);
        player = MediaPlayer.create(getApplicationContext(),
                R.raw.diamond);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.bprev :{
                break;
            }
            case R.id.bstst :{
                /*
                재생/일시정지.

                고려해야 할 사항.

                플레이 안하고 있을때
                일시정지일때
                플레이중일때
                 */
                if(!player.isPlaying() && player.getCurrentPosition() != 0){ //일시정지일때
                    play = true;
                    player.start();
                }
                else if(!player.isPlaying()) { //플레이 안하고 있을때
                    player.setLooping(false);
                    play = true;
                    player.start();
                    timeseek.setMax(player.getDuration());
                    new mps().start();
                }
                else { //플레이중일때
                    play = false;
                    player.pause();
                }
                break;
            }
            case R.id.bnext :{
                break;
            }
        }
    }
}
