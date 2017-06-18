package com.ivsa.normalplayer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;



/*
* Tab
* 기본 Activity입니다.
*
* */


public class Tab extends AppCompatActivity{
    public final int PERMISSION_WRITE = 100;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter adapter;
    public static Context contextOfApplication;
    public static Context getContextOfApplication()
    {
        return contextOfApplication;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        contextOfApplication = getApplicationContext();
        tabLayout = (TabLayout) findViewById(R.id.tab);
        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(getApplicationContext(),
                        "미디어를 불러오려면 SDCard 쓰기 권한이 필요합니다. \n",Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE);

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE);

            }
        }
        else {
            adapter.addFragment("음악", new FragmentExplorer());
            adapter.addFragment("재생목록", new FragmentPlaylist());
            viewPager.setAdapter(adapter);
            tabLayout.setupWithViewPager(viewPager);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_WRITE : {
                if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    adapter.addFragment("음악", new FragmentExplorer());
                    adapter.addFragment("재생목록", new FragmentPlaylist());
                    viewPager.setAdapter(adapter);
                    tabLayout.setupWithViewPager(viewPager);
                    break;
                }
            }
        }
    }

    public void onClick(View v) {
        switch (v.getId()){
            //플레이어 Activity 보이기
            case R.id.smallplay :{
                Intent intent = new Intent(Tab.this, Playback.class);
                startActivity(intent);
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
}
