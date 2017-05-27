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
    MediaPlayer player;
    ImageView albart;
    SeekBar timeseek;
    TextView sinfo, ainfo, startpos, endpos;
    boolean play = false;

    class mps extends Thread { //재생중일 때, 탐색바를 움직이는 thread를 생성합니다.
        @Override
        public void run() {
            while(play){
                timeseek.setProgress(player.getCurrentPosition());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timeseek = (SeekBar) findViewById(R.id.timeseek);
        player   = MediaPlayer.create(getApplicationContext(), R.raw.diamond);
        sinfo    = (TextView) findViewById(R.id.songinfo);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.bprev :{
                /*
                * 이전 버튼입니다.
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
                if(!player.isPlaying() && player.getCurrentPosition() != 0){ //일시정지일때
                    play = true;
                    player.start();
                    new mps().start();
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
