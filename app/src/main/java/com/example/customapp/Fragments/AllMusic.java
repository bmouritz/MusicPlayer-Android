package com.example.customapp.Fragments;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.customapp.MainActivity;
import com.example.customapp.Player;
import com.example.customapp.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllMusic extends Fragment {
    private ListView listView;
    private Intent play, intent;
    public Button btn;

    private ArrayList<File> playlistSongs = new ArrayList<>();

    public AllMusic() { /*Required empty public constructor */ }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_all_music, container, false);
        listView = view.findViewById(R.id.music);
        play = new Intent(getActivity(), Player.class);
        btn = view.findViewById(R.id.add);
        updateBtn();
        askStoragePermissions();

        return view;
    }

    private ArrayList<File> getMusic(File file){
        ArrayList<File> musicLists = new ArrayList<>();
        File[] files = file.listFiles();

        for(File singleFile: files ){
            if(singleFile.isDirectory() && !singleFile.isHidden()){
                musicLists.addAll(getMusic(singleFile));
            }else{
                if(singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".m4a") || singleFile.getName().endsWith(".wav") || singleFile.getName().endsWith(".m4b")){
                    musicLists.add(singleFile);
                }
            }
        }

        return musicLists;
    }

    private void display(){
        final ArrayList<File> allSongs = getMusic(Environment.getExternalStorageDirectory());
        final String[] songs = new String[allSongs.size()];

        for(int i = 0; i < allSongs.size(); i++){
            songs[i] = allSongs.get(i).getName().replaceAll(".+/", "").replace(".mp3","").replace(".m4a","").replace(".wav","").replace(".m4b","");
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, songs);

        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // start music player when song name is clicked
                String songName = listView.getItemAtPosition(position).toString();
                play.putExtra("songs",allSongs).putExtra("songName",songName).putExtra("position",position);
                if(Player.player != null) { Player.player.finish(); }
                startActivity(play);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < allSongs.size() - 1; i++) {
                    if (allSongs.get(i).toString().contains(listView.getItemAtPosition(position).toString())) {
                        playlistSongs.add(allSongs.get(i));
                    }
                }
                return true;
            }
        });
    }

    private void updateBtn() {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getActivity().getBaseContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("playlist", playlistSongs);
                getActivity().startActivity(intent);
            }
        });
    }

    private void askStoragePermissions(){
        Dexter.withActivity(getActivity()).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                display();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {}

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) { token.continuePermissionRequest(); }
        }).check();
    }
}