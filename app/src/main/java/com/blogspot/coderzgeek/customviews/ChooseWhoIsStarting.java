package com.blogspot.coderzgeek.customviews;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChooseWhoIsStarting extends AppCompatActivity {
    Button x;
    Button o;
    public static int firstToPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_who_is_starting);

        x = (Button) findViewById(R.id.PLayAsX);
        o = (Button) findViewById(R.id.PlayAsO);
        x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                firstToPlay = 0;
                startActivity(i);
            }
        });
        o.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                firstToPlay = 1;
                startActivity(i);
            }
        });
    }
}