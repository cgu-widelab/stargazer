package com.example.mucolores.stargazer;

import android.app.Activity;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class Main3Activity extends AppCompatActivity {

    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        countDownTimer = new CountDownTimer(1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Intent intent = new Intent();
                intent.setClass(Main3Activity.this,Main4Activity.class);
                startActivity(intent);
                finish();
            }
        };

        countDownTimer.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(countDownTimer!=null)
        {
            countDownTimer.cancel();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (countDownTimer != null)
        {
            countDownTimer.cancel();
        }
    }
}
