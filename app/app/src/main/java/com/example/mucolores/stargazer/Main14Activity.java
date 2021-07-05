package com.example.mucolores.stargazer;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Main14Activity extends AppCompatActivity {

    private Button goBtn14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main14);

        goBtn14 = findViewById(R.id.goBtn14_ID);
        goBtn14.setOnClickListener(goBtn14OCL);
    }

    View.OnClickListener goBtn14OCL = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent15 = new Intent();
            intent15.setClass(Main14Activity.this,UnityPlayerActivity.class);
            startActivity(intent15);

        }
    };
}
