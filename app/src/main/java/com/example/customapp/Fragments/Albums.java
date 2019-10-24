package com.example.customapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.customapp.Player;
import com.example.customapp.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Albums extends ListFragment {
    private List<String> fileList = new ArrayList<>();
    private Intent play;
    private String path = "/sdcard/Music/";
    Button btn;

    public Albums() { /* Required empty public constructor */ }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_albums, container, false);
        play = new Intent(getActivity(), Player.class);
        btn = view.findViewById(R.id.root);
        initBtn();
        initFolders(path);
        return view;
    }

    private void initBtn() {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                path = "/sdcard/Music/";
                initFolders(path);
            }
        });
    }

    //Gets the initial path of the folder, and displays all items in the List.
    private void initFolders(String path) {
        File root = new File(path);
        final File[] files = root.listFiles();
        fileList.clear();
        for (File file : files){
            fileList.add(file.getPath());
        }

        //gets path of folder, removes unwanted path details and displays to list.
        String[] folders = new String[fileList.size()];
        for(int i = 0; fileList.size() > i; i++){
            folders[i] = fileList.get(i).replace(path, "").replaceAll(".+/", "").replace(".mp3","").replace(".m4a","").replace(".wav","").replace(".m4b","");;
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, folders);
        setListAdapter(arrayAdapter);
    }

    //Gets music from the directory path and returns as a ArrayList of Files
    private ArrayList<File> getMusic(File path){
        ArrayList<File> musicLists = new ArrayList<>();
        File[] files = path.listFiles();

        for(File singleFile: files ){
            if(singleFile.isDirectory() && !singleFile.isHidden()){ musicLists.addAll(getMusic(singleFile)); }
            else {
                if(singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".m4a") || singleFile.getName().endsWith(".wav") || singleFile.getName().endsWith(".m4b")){
                    musicLists.add(singleFile);
                }
            }
        }

        return musicLists;
    }

    //Checks which folder you've selected
    //If the folder has a folder inside it, it will display that folder and it's contents, with the ability to go into the next folder.
    //Once folder has no more sub folders, the song selected will play.
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String songName = l.getItemAtPosition(position).toString();
        File song = new File(path += songName);

        if(song.isDirectory()) { initFolders(song.toString()); }
        else {
            final ArrayList<File> allSongs = getMusic(new File(path.replace(songName, "")));
            startActivity(play.putExtra("songs",allSongs).putExtra("songName",songName).putExtra("position",position));
        }
    }
}
