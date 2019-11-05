package com.example.customapp;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView songNamePlaying;
    private ImageButton playingBtn;

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getExtras();
        initUI();
    }

    //Creates the now playing bar if there is a song playing.
    private void getExtras() {
        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            if(extras.getString("songTitle") != null) {
                String songTitle = extras.getString("songTitle");
                nowPlaying(songTitle);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    protected void initUI(){
        initToolbar();
        initViewByIds();
        initPageController();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settingBtn) {
            startActivity(new Intent(MainActivity.this, Settings.class));
        }
        return super.onOptionsItemSelected(item);
    }

    protected void initToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    protected void initViewByIds(){
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        songNamePlaying = findViewById(R.id.songPlaying);
    }

    //Deals with creating the nowplaying bar in MainActivity
    //and the onclick listener.
    private void nowPlaying(String songPlaying) {
        LinearLayout nowPlayingBar = findViewById(R.id.linearlayout);
        nowPlayingBar.setVisibility(View.VISIBLE);
        songNamePlaying = findViewById(R.id.songPlaying);
        songNamePlaying.setText(songPlaying);
        playingBtn = findViewById(R.id.nowPlaying);
        playingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MediaPlayerSingleton.isPlayingSong()) {
                    playingBtn.setBackgroundResource(R.drawable.play_btn);
                    MediaPlayerSingleton.pause();
                } else {
                    playingBtn.setBackgroundResource(R.drawable.pause_btn);
                    MediaPlayerSingleton.play();
                }
            }
        });
        nowPlayingBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //Deals with swapping of tabs in MainActivity
    protected void initPageController(){
        PageController pageController = new PageController(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pageController);

        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                int tabIconColor = ContextCompat.getColor(getApplicationContext(), R.color.iconSelected);
                Objects.requireNonNull(tab.getIcon()).setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                Log.i("TAG", "onTabSelected: " + tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(getApplicationContext(), R.color.icondeSelected);
                Objects.requireNonNull(tab.getIcon()).setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                Log.i("TAG", "onTabUnselected: " + tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { Log.i("TAG", "onTabReselected: " + tab.getPosition()); }
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }
}
