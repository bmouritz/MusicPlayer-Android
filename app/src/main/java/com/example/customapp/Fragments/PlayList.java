package com.example.customapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.customapp.Player;
import com.example.customapp.R;

import java.io.File;
import java.util.ArrayList;

public class PlayList extends Fragment {
    private ListView listView;
    private ArrayList<File> songFileList = new ArrayList<>();
    private Intent play;
    private Button btn;

    public PlayList() { /* Required empty public constructor */ }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play_list, container, false);
        listView = view.findViewById(R.id.playlist);
        play = new Intent(getActivity(), Player.class);
        btn = view.findViewById(R.id.clearPlaylist);
        initButton();
        initPlaylist();
        return view;
    }

    private void initButton() {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songFileList = new ArrayList<>();
                Toast toast = Toast.makeText(getContext(), "Playlist cleared", Toast.LENGTH_SHORT);
                toast.show();
                display();
            }
        });
    }

    private void initPlaylist() {
        Intent intent = getActivity().getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null) {
            if (bundle.getParcelableArrayList("playlist") != null) {
                songFileList = (ArrayList) bundle.getParcelableArrayList("playlist");
                display();
            }
        }
    }

    private void display() {
        final String[] songs = new String[songFileList.size()];
        for(int i = 0; i < songFileList.size(); i++){
            songs[i] = songFileList.get(i).toString().replaceAll(".+/", "").replace(".mp3","").replace(".m4a","").replace(".wav","").replace(".m4b","");
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, songs);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String songName = listView.getItemAtPosition(position).toString();
                    play.putExtra("songs",songFileList).putExtra("songName",songName).putExtra("position",position);
                    startActivity(play);
                }
        });
    }

}
