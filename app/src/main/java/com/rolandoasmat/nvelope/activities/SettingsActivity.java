package com.rolandoasmat.nvelope.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.rolandoasmat.nvelope.R;

import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
    }
}
