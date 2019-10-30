package com.example.customapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class Player extends AppCompatActivity {
    private ArrayList<File> songFileList;
    private SeekBar seekBar;
    private TextView songTitle, curTime, totTime;
    private ImageView playBtn, nextBtn, prevBtn, repeat, shuffle;
    private int position;
    private Toolbar toolbar;
    private Boolean shuffled, repeated;

    private MediaPlayer mediaPlayer;
    private String sname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playing);
        mediaPlayer = MediaPlayerSingleton.getInstance();
        initFindIds();
        initMediaPlayer();
    }

    //Inflates the toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.player_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.Eq) {
            initEQBtn();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initFindIds() {
        seekBar = findViewById(R.id.musicSeekBar);
        songTitle = findViewById(R.id.songTitle);
        curTime = findViewById(R.id.currTime);
        totTime = findViewById(R.id.totalTime);
        playBtn = findViewById(R.id.playbtn);
        prevBtn = findViewById(R.id.imageView5);
        nextBtn = findViewById(R.id.imageView4);
        toolbar = findViewById(R.id.toolbarPlaying);
        shuffle = findViewById(R.id.shuffle);
        repeat = findViewById(R.id.repeat);

        //Set default boolean to false
        shuffled = false;
        repeated = false;
        initToolbar();
    }

    //if Eq button is selected, startActivity
    private void initEQBtn() {
        Intent intent = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
        intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getPackageName());
        intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, mediaPlayer.getAudioSessionId());

        if ((intent.resolveActivity(getPackageManager()) != null)) {
            startActivityForResult(intent, 100);
        }
    }

    private void initToolbar() {
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Player.this, MainActivity.class);
                intent.putExtra("songTitle", sname);
                startActivity(intent);
            }
        });
    }

    private void initMediaPlayer() {
        Intent playerData = getIntent();
        Bundle bundle = playerData.getExtras();
        songFileList = (ArrayList) bundle.getParcelableArrayList("songs");
        position = bundle.getInt("position", 0);
        initPlayer(position);

        initButtons();
    }

    private void initButtons() {
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position <= 0) {
                    position = songFileList.size() - 1;
                } else { position--; }
                initPlayer(position);
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position < songFileList.size() - 1) {
                    position++;
                } else {
                    position = 0;
                }
                initPlayer(position);
            }
        });

        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!shuffled) {
                    Collections.shuffle(songFileList);
                    shuffle.setImageResource(R.drawable.shuffled);
                    shuffled = true;
                } else {
                    shuffle.setImageResource(R.drawable.shuffle);
                    shuffled = false;
                }
            }
        });

        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!repeated) {
                    mediaPlayer.setLooping(true);
                    repeat.setImageResource(R.drawable.repeated);
                    repeated = true;
                } else {
                    repeat.setImageResource(R.drawable.repeat);
                    repeated = false;
                }
            }
        });
    }

    private void initPlayer(final int position) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.reset();
        }

        sname = songFileList.get(position).getName().replace(".mp3", "").replace(".m4a", "").replace(".wav", "").replace(".m4b", "");
        songTitle.setText(sname);
        Uri songResourceUri = Uri.parse(songFileList.get(position).toString());

        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(getApplicationContext(), songResourceUri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepareAsync(); // prepare async as to not block main thread
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                String totalTime = createTimeLabel(mediaPlayer.getDuration());
                totTime.setText(totalTime);
                seekBar.setMax(mediaPlayer.getDuration());
                mediaPlayer.start();
                playBtn.setImageResource(R.drawable.pause_btn);
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                int currSongPosition = position;
                // code to repeat songs until the
                if (currSongPosition < songFileList.size() - 1) {
                    currSongPosition++;
                    initPlayer(currSongPosition);
                } else {
                    currSongPosition = 0;
                    initPlayer(currSongPosition);
                }
            }
        });

        initSeekBar();
    }

    private void initSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    seekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mediaPlayer != null) {
                    try {
                        // create new message to send to handler
                        if (mediaPlayer.isPlaying()) {
                            Message message = new Message();
                            message.what = mediaPlayer.getCurrentPosition();
                            handler.sendMessage(message);
                            Thread.sleep(1000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    //The handler deals with setting the progress of the progress bar
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int current_position = msg.what;
            seekBar.setProgress(current_position);
            String currentTime = createTimeLabel(current_position);
            curTime.setText(currentTime);
        }
    };

    private void play() {
        if (!mediaPlayer.isPlaying()) {
            MediaPlayerSingleton.play();
            playBtn.setImageResource(R.drawable.pause_btn);
        } else {
            pause();
        }
    }

    private void pause() {
        if (mediaPlayer.isPlaying()) {
            MediaPlayerSingleton.pause();
            playBtn.setImageResource(R.drawable.play_btn);
        }
    }

    private String createTimeLabel(int duration) {
        String timeLabel = "";
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;

        timeLabel += min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;
    }

    @SuppressLint("StaticFieldLeak")
    public static Activity player; {
        player = this;
    }
}