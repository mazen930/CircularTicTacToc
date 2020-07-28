package com.blogspot.coderzgeek.customviews;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class CustomDialogClass extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button yes, no;
    String whoWon;
    TextView dialogTitle;



    public CustomDialogClass(Activity a, String winnerName) {
        super(a);
        this.c = a;
        this.whoWon = winnerName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        yes = (Button) findViewById(R.id.btn_yes);
        no = (Button) findViewById(R.id.btn_no);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        dialogTitle=findViewById(R.id.txt_dia);
        // Display who Wins
        dialogTitle.setText(whoWon);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                c.finish();
                break;
            case R.id.btn_no:
                Intent i = new Intent(getContext(), StartGameActivity.class);
                c.startActivity(i);
                break;
            default:
                break;
        }
    }
}