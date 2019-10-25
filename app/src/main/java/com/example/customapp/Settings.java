package com.example.customapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class Settings extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Toolbar toolbar;
    Switch language, theme;
    Button save;
    DisplayMetrics dm;
    boolean darkEnabled;
    Spinner langOption;
    private static String langSelected = "none";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        initUI();
    }

    private void initWindow() {
        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width * .8), (int)(height * .32));
    }

    private void initUI(){
        initWindow();

        toolbar = findViewById(R.id.toolbar);
        language = findViewById(R.id.language);
        theme = findViewById(R.id.theme);
        save = findViewById(R.id.save);
        langOption = findViewById(R.id.languageOptions);
        langOption.setVisibility(View.GONE);

        initToolbar();
        initSwitches();
    }

    public void initSpinner(Spinner spinner) {
        ArrayList<String> languagesArray;
        //Sets ArrayList based on what language is already selected
        if(langSelected.equals("vi")){
            languagesArray = new ArrayList<>();
            languagesArray.add("Vietnamese");
            languagesArray.add("English");
        } else {
            languagesArray = new ArrayList<>();
            languagesArray.add("English");
            languagesArray.add("Vietnamese");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_items, languagesArray);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    private void initButton(final String lang) {
        save.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                //Set language of app
                if(lang.equals("en")){
                    setAppLocale("en");
                } else{
                    setAppLocale("vi");
                }
                //Sets theme of app
                if(theme.isChecked()) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    darkEnabled = true;
                } else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    darkEnabled = false;
                }

                //Saves the data into the phone, so when restarted it remembers
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(Settings.this);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("lang", lang);
                editor.putBoolean("bool", darkEnabled);
                editor.apply();
                finish();
                startActivity(getIntent());
            }
        });
    }

    private void initSwitches() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(Settings.this);
        String lang = settings.getString("lang", "0");
        boolean darkEnabled = settings.getBoolean("bool", false);
        theme.setChecked(darkEnabled);
        langSelected = lang;

        language.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                langOption.setVisibility(View.VISIBLE);
                initSpinner(langOption);
            }
        });
    }

    private void setAppLocale(String localeCode){
        Resources rs = getResources();
        DisplayMetrics dm = rs.getDisplayMetrics();
        Configuration conf = rs.getConfiguration();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            conf.setLocale(new Locale(localeCode.toLowerCase()));
        } else {
            conf.locale = new Locale(localeCode.toLowerCase());
        }
        rs.updateConfiguration(conf, dm);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        String text = parent.getItemAtPosition(position).toString();
        switch (text) {
            //English
            case "English":
                langSelected = "en";
                break;
            //Vietnamese
            case "Vietnamese":
                langSelected = "vi";
                break;
            default:
                break;
        }
        initButton(langSelected);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
