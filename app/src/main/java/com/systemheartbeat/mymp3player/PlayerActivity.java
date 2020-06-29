package com.systemheartbeat.mymp3player;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    TextView txt_song_name;
    SeekBar songSeekBar;
    Button btn_play_pause, btn_next, btn_prev;

    static MediaPlayer mediaPlayer;
    int position;
    String song;
    ArrayList<File> songs;
    Thread updateSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        txt_song_name = findViewById(R.id.txt_song_name);
        songSeekBar = findViewById(R.id.songSeekBar);
        btn_play_pause = findViewById(R.id.btn_play_pause);
        btn_next = findViewById(R.id.btn_next);
        btn_prev = findViewById(R.id.btn_prev);

        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        song = intent.getStringExtra("song");
        position = bundle.getInt("position", 0);
        songs = (ArrayList) bundle.getParcelableArrayList("songs");

        txt_song_name.setText(song);
        txt_song_name.setSelected(true);

        Uri uri = Uri.parse(songs.get(position).toString());

        updateSeekBar =  new Thread() {
            @Override
            public void run() {
                super.run();
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;
                while (totalDuration > currentPosition) {
                    try {
                        sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        songSeekBar.setProgress(currentPosition);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();

        songSeekBar.setMax(mediaPlayer.getDuration());

        updateSeekBar.start();

        songSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        btn_play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songSeekBar.setMax(mediaPlayer.getDuration());

                if(mediaPlayer.isPlaying()) {
                    btn_play_pause.setBackgroundResource(R.drawable.ic_play);
                    mediaPlayer.pause();
                } else {
                    btn_play_pause.setBackgroundResource(R.drawable.ic_pause);
                    mediaPlayer.start();
                }
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position + 1) % songs.size());
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                song = songs.get(position).getPath();
                txt_song_name.setText(songs.get(position).getName());
                mediaPlayer.start();
            }
        });

        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position - 1) < 0) ? (songs.size() - 1) : (position - 1);
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                song = songs.get(position).getPath();
                txt_song_name.setText(songs.get(position).getName());
                mediaPlayer.start();
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
