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
    MediaPlayer mp;
    ImageView albart;
    SeekBar timeseek;
    TextView sinfo, ainfo;
    boolean play = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.bprev :{
                break;
            }
            case R.id.bstst :{
                break;
            }
            case R.id.bnext :{
                break;
            }
        }
    }
}
