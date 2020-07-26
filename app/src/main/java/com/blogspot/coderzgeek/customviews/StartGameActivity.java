package com.blogspot.coderzgeek.customviews;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartGameActivity extends AppCompatActivity {
    Button onePlayerButton, twoPlayerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);
        onePlayerButton = (Button) findViewById(R.id.OnePlayerButton);
        twoPlayerButton = (Button) findViewById(R.id.TwoPlayerButton);
        onePlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("gameMode", 1);
                startActivity(i);
            }
        });
        twoPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("gameMode", 0);
                startActivity(i);
            }
        });
    }

}