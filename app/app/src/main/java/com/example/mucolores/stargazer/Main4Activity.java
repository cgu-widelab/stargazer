package com.example.mucolores.stargazer;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Main4Activity extends AppCompatActivity {

    private Dialog dialog5,dialog6,dialog20;
    private Button switchUserBtn;
    private Button searchStarDataBtn,searchStarPlaceBtn,favoriteButton,favoriteStarButton,favoritePlaceButton;
    private Button starDataBtn,SkyPictureBtn;
    private TextView userNameTextView;
    private LinearLayout user_LinearLayout;
    private TextView dialogUserNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        searchStarDataBtn = findViewById(R.id.searchStarDataBtn_ID);
        searchStarPlaceBtn = findViewById(R.id.searchStarPlaceBtn_ID);
        userNameTextView = findViewById(R.id.userNameTextView_ID);
        user_LinearLayout = findViewById(R.id.user_LinearLayout_ID);
        favoriteButton = findViewById(R.id.myFavoriteBtn_ID);

        setUserName();
        setSearchDataBtn();
        setSearchStarPlaceBtn();
        setDialog20();
        setMyFavoriteBtn();
        setFavoriteStarButton();
        setFavoritePlaceButton();
        setUser_LinearLayout();
        setDialog5();
        setDialog6();

        starDataBtn = dialog6.findViewById(R.id.starDataBtn_ID);
        SkyPictureBtn = dialog6.findViewById(R.id.SkyPictureBtn_ID);
        setStarDataBtn();
        setSkyPictureBtn();

        switchUserBtn = dialog5.findViewById(R.id.switchUserBtn_ID);
        setSwitchUserBtn();
    }


    private void setUserName()
    {
        SharedPreferences sharedPreferences  = getSharedPreferences(getResources().getString(R.string.sharedPreference_Key),MODE_PRIVATE);
        userNameTextView.setText(sharedPreferences.getString(getResources().getString(R.string.user),"Non existing user"));
    }

    private void setSearchDataBtn()
    {
        searchStarDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog6.show();
            }
        });
    }

    private void setSearchStarPlaceBtn()
    {
        searchStarPlaceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent9 = new Intent();
                intent9.setClass(Main4Activity.this,Main9Activity.class);
                startActivity(intent9);
            }
        });
    }

    private void setMyFavoriteBtn()
    {
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog20.show();
            }
        });
    }

    private void setFavoriteStarButton()
    {
        favoriteStarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Main4Activity.this,Main21Activity.class);
                startActivity(intent);
            }
        });
    }

    private void setFavoritePlaceButton()
    {
        favoritePlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Main4Activity.this,Main16Activity.class);
                startActivity(intent);
            }
        });
    }

    private void setUser_LinearLayout()
    {
        user_LinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog5.show();
            }
        });
    }

    private void setDialog5()
    {
        dialog5 = new Dialog(Main4Activity.this);
        dialog5.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog5.setContentView(R.layout.activity_main5);
        dialog5.setCancelable(true);

        Window window5 = dialog5.getWindow();
        DisplayMetrics displayMetrics5 = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics5);
        WindowManager.LayoutParams layoutParams5 = window5.getAttributes();

        layoutParams5.width = (int)(displayMetrics5.widthPixels*0.9);
        layoutParams5.height = (int)(displayMetrics5.heightPixels*0.7);

        window5.setAttributes(layoutParams5);

        dialogUserNameTextView = dialog5.findViewById(R.id.userNameTextView_ID);
        dialogUserNameTextView.setText(userNameTextView.getText().toString());
    }

    private void setDialog6()
    {
        dialog6 = new Dialog(Main4Activity.this);
        dialog6.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog6.setContentView(R.layout.activity_main6);
        dialog6.setCancelable(true);

        Window window6 = dialog6.getWindow();
        DisplayMetrics displayMetrics6 = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics6);
        WindowManager.LayoutParams layoutParams6 = window6.getAttributes();

        layoutParams6.width = (int)(displayMetrics6.widthPixels*0.9);
        layoutParams6.height = (int)(displayMetrics6.heightPixels*0.5);

        window6.setAttributes(layoutParams6);
    }

    private void setDialog20()
    {
        dialog20 = new Dialog(Main4Activity.this);
        dialog20.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog20.setContentView(R.layout.activity_main20);
        dialog20.setCancelable(true);

        Window window = dialog20.getWindow();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        WindowManager.LayoutParams layoutParams = window.getAttributes();

        layoutParams.width = (int)(displayMetrics.widthPixels*0.9);
        layoutParams.height = (int)(displayMetrics.heightPixels*0.5);
        window.setAttributes(layoutParams);

        favoriteStarButton = dialog20.findViewById(R.id.favoriteStarButton_ID);
        favoritePlaceButton = dialog20.findViewById(R.id.favoritePlaceButton_ID);
    }

    private void setStarDataBtn()
    {
        starDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent();
                intent3.setClass(Main4Activity.this,Main13Activity.class);
                startActivity(intent3);
            }
        });
    }

    private void setSkyPictureBtn()
    {
        SkyPictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog6.cancel();
                Intent intent = new Intent();
                intent.setClass(Main4Activity.this, UnityPlayerActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setSwitchUserBtn()
    {
        switchUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent5 = new Intent();
                intent5.setClass(Main4Activity.this,Main2Activity.class);
                startActivity(intent5);
                finish();
            }
        });
    }
}
