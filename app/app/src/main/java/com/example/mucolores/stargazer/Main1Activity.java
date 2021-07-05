package com.example.mucolores.stargazer;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

public class Main1Activity extends AppCompatActivity {

    private ImageView TouchImageView;

    private Animation animation1,animation2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

        TouchImageView = findViewById(R.id.touchImageView_ID);
        TouchImageView.setImageResource(R.mipmap.touch2);
        animation1 = new AlphaAnimation(1,0);
        animation2 = new AlphaAnimation(0,1);
        animation1.setDuration(1500);
        animation2.setDuration(1500);

        TouchImageView.setAnimation(animation1);
        animation1.startNow();
        animation1.setAnimationListener(animation1L);
        animation2.setAnimationListener(animation2L);
    }

    public void Activity(View view)
    {
        Intent intent = new Intent();
        intent.setClass(Main1Activity.this,Main2Activity.class);
        startActivity(intent);
    }

    Animation.AnimationListener animation1L = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            TouchImageView.setAnimation(animation2);
            animation2.startNow();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    Animation.AnimationListener animation2L = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            TouchImageView.setAnimation(animation1);
            animation1.startNow();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };
}
