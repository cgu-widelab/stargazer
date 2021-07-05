package com.example.mucolores.stargazer;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Main8Activity extends AppCompatActivity {

    private ImageView starImageView,dialogImageView;
    private ImageButton favoriteImageButton;
    private Bitmap Image;
    private TextView textView8;
    private TextView starEnNameTextView,StarAscensionTextView,starAngleTextView,starFamilyTextView,starIntroduceTextView;
    private Dialog downloadDialog,dialog,graphDialog;

    private String star_name;
    private String checkFavoriteUrl = "http://34.80.175.22/stargazer/8_checkFavorite.php?";
    private String saveFavoriteStarUrl = "http://34.80.175.22/stargazer/8_saveStar.php?";
    private String deleteFavoriteStarUrl = "http://34.80.175.22/stargazer/8_deleteStar.php?";
    private String userString = "user",starString = "star";
    private String userName;
    private boolean like;
    private String existing;

    private String PickedStar;
    private String starDataUrl = "http://34.80.175.22/stargazer/8_starData.php?";
    private String StarName = "StarName";
    private String StarData;
    private String pic_link = "pic_link";
    private String pic_url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main8);
        starImageView = findViewById(R.id.starImageView_ID);
        textView8 = findViewById(R.id.activity_TextView_ID);
        starEnNameTextView = findViewById(R.id.starEnNameTextView_ID);
        StarAscensionTextView = findViewById(R.id.StarAscensionTextView_ID);
        starAngleTextView = findViewById(R.id.starAngleTextView_ID);
        starFamilyTextView = findViewById(R.id.starFamily_ID);
        starIntroduceTextView = findViewById(R.id.starIntroduceTextView_ID);
        favoriteImageButton = findViewById(R.id.favoriteImageButton_ID);
        starIntroduceTextView.setMovementMethod(new ScrollingMovementMethod());

        GetBundleData();
        setAllStarData();
        setFavoriteImageButton();

    }

    private void GetBundleData()
    {
        Intent intent8 = getIntent();
        Bundle receiveBundle8;
        receiveBundle8 = intent8.getExtras();
        if (receiveBundle8 != null) {
            PickedStar = receiveBundle8.getString(getResources().getString(R.string.bundle_selected_star_key));
        }
        textView8.setText(PickedStar);
    }

    private void setAllStarData() {
        SharedPreferences sharedPreferences  = getSharedPreferences(getResources().getString(R.string.sharedPreference_Key),MODE_PRIVATE);
        userName = sharedPreferences.getString(getResources().getString(R.string.user),"Non existing user");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                StarData = DB_Connector.updatingData(starDataUrl + StarName + "=" + PickedStar);
            }
        });
        thread.start();
        try
        {
            thread.join();
        }catch (InterruptedException e)
        {
            Log.e("Interrupt Error",e.toString());
        }

        dialog = new Dialog(Main8Activity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout1);
        dialog.setCanceledOnTouchOutside(true);

        Window window = dialog.getWindow();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = (int)(displayMetrics.widthPixels*0.7);
        layoutParams.height = (int)(displayMetrics.heightPixels*0.4);
        window.setAttributes(layoutParams);
        dialog.show();

        setData();
        dialog.cancel();
        setStarDialog();
        setStarImage();
        setStarImageOCL();
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                if(DB_Connector.updatingData(checkFavoriteUrl + userString + "=" + userName + "&&" + starString + "=" + star_name).equals("exist"))
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            favoriteImageButton.setImageResource(android.R.drawable.btn_star_big_on);
                            like = true;
                        }
                    });
                }
                else
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            favoriteImageButton.setImageResource(android.R.drawable.btn_star_big_off);
                            like = false;
                        }
                    });
                }
            }
        });
        thread1.start();

    }

    private void setData()
    {
        try
        {
            JSONObject jsonObjectAll = new JSONObject(StarData);
            JSONArray jsonArray = jsonObjectAll.getJSONArray("data");
            JSONObject dataJSONObject = jsonArray.getJSONObject(0);
            star_name = dataJSONObject.getString("star_name");
            starEnNameTextView.setText(star_name);
            StarAscensionTextView.setText(dataJSONObject.getString("Right_Ascension"));
            starAngleTextView.setText(dataJSONObject.getString("Declination"));
            starFamilyTextView.setText(dataJSONObject.getString("family"));
            starIntroduceTextView.setText(dataJSONObject.getString("star_description"));
            pic_url = dataJSONObject.getString(pic_link);
        }catch (JSONException e)
        {
            Log.e("JSON Error",e.toString());
        }

    }

    private void setStarDialog()
    {
        graphDialog  = new Dialog(Main8Activity.this);
        graphDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        graphDialog.setContentView(R.layout.dialog_layout2);
        graphDialog.setCanceledOnTouchOutside(true);
        Window window = graphDialog.getWindow();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = (int)(displayMetrics.widthPixels*0.9);
        layoutParams.height = (int)(displayMetrics.heightPixels*0.7);
        window.setAttributes(layoutParams);
    }

    private void setStarImage()
    {
        downloadDialog = new Dialog(Main8Activity.this);
        downloadDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        downloadDialog.setContentView(R.layout.dialog_layout1);
        downloadDialog.setCanceledOnTouchOutside(false);
        downloadDialog.setCancelable(false);

        Window window = downloadDialog.getWindow();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = (int)(displayMetrics.widthPixels*0.7);
        layoutParams.height = (int)(displayMetrics.heightPixels*0.4);
        window.setAttributes(layoutParams);

        downloadDialog.show();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Image = BitmapFactory.decodeStream((InputStream)new URL(pic_url).getContent());
                } catch (IOException e) {
                    Log.e("URL Error",e.toString());
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        downloadDialog.cancel();
                        starImageView.setImageBitmap(Image);
                        dialogImageView = graphDialog.findViewById(R.id.starImageView_ID);
                        dialogImageView.setImageBitmap(Image);
                    }
                });
            }
        });
        thread.start();
    }

    private void setStarImageOCL()
    {
        starImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                graphDialog.show();
            }
        });
    }

    private void setFavoriteImageButton()
    {
        favoriteImageButton.setOnClickListener(new View.OnClickListener() {
            Dialog downloadDialog;
            @Override
            public void onClick(View v) {
                if(like)
                {
                    downloadDialog = new Dialog(Main8Activity.this);
                    downloadDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    downloadDialog.setContentView(R.layout.dialog_layout1);
                    downloadDialog.setCanceledOnTouchOutside(true);

                    Window window = downloadDialog.getWindow();
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    WindowManager.LayoutParams layoutParams = window.getAttributes();
                    layoutParams.width = (int) (displayMetrics.widthPixels * 0.7);
                    layoutParams.height = (int) (displayMetrics.heightPixels * 0.4);
                    window.setAttributes(layoutParams);

                    downloadDialog.show();
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DB_Connector.updatingData(deleteFavoriteStarUrl + userString + "=" + userName + "&&" + starString + "=" + star_name);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    downloadDialog.cancel();
                                }
                            });
                        }
                    });
                    thread.start();
                    favoriteImageButton.setImageResource(android.R.drawable.btn_star_big_off);
                    like = false;
                }
                else
                {
                    downloadDialog = new Dialog(Main8Activity.this);
                    downloadDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    downloadDialog.setContentView(R.layout.dialog_layout1);
                    downloadDialog.setCanceledOnTouchOutside(true);

                    Window window = downloadDialog.getWindow();
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    WindowManager.LayoutParams layoutParams = window.getAttributes();
                    layoutParams.width = (int) (displayMetrics.widthPixels * 0.7);
                    layoutParams.height = (int) (displayMetrics.heightPixels * 0.4);
                    window.setAttributes(layoutParams);

                    downloadDialog.show();

                    Thread thread1 = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DB_Connector.updatingData(saveFavoriteStarUrl + userString + "=" + userName + "&&" + starString + "=" + star_name);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    downloadDialog.cancel();
                                }
                            });
                        }
                    });
                    thread1.start();
                    favoriteImageButton.setImageResource(android.R.drawable.btn_star_big_on);
                    like = true;
                }



            }

        });
    }
}
