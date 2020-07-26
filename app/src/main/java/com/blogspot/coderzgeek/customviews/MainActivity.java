package com.blogspot.coderzgeek.customviews;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.blogspot.coderzgeek.customviews.views.CustomView;

public class MainActivity extends AppCompatActivity {

    private CustomView mCustomView;
    public int gameMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCustomView = (CustomView) findViewById(R.id.customView);

        Intent i = getIntent();
        gameMode = i.getIntExtra("gameMode", 1);

    }
}
