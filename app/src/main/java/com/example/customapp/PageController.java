package com.example.customapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.customapp.Fragments.Albums;
import com.example.customapp.Fragments.AllMusic;
import com.example.customapp.Fragments.PlayList;

public class PageController extends FragmentPagerAdapter {
    private int tabCount;

    PageController(FragmentManager supportFragmentManager, int tabs) {
        super(supportFragmentManager);
        tabCount = tabs;
    }

    @NonNull
    @Override
    public Fragment getItem (int i){
        Fragment fragment = null;
        switch(i) {
            case 0:
                fragment = new AllMusic();
                break;
            case 1:
                fragment = new Albums();
                break;
            case 2:
                fragment = new PlayList();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount(){
        return tabCount;
    }
}
