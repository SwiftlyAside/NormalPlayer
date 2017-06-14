package com.example.iveci.pmultip;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
        contextOfApplication = getApplicationContext();
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
        setContentView(R.layout.activity_tab);
        tabLayout = (TabLayout) findViewById(R.id.tab);
        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment("음악", new FragmentExplorer());
        adapter.addFragment("재생목록", new FragmentPlaylist());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
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
