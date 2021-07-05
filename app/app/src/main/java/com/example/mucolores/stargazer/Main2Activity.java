package com.example.mucolores.stargazer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {

    private EditText accountEditText,passwordEditText;
    private String resultString;
    private Thread thread;
    private String signInUrl = "http://34.80.175.22/stargazer/2_signIn.php?";
    private String account = "account=";
    private String password = "password=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        accountEditText = findViewById(R.id.accountEditText_ID);
        passwordEditText = findViewById(R.id.passwordEditText_ID);
        passwordEditText.setTransformationMethod(new PasswordTransformationMethod());
    }

    public void Sign (View view){
        if(accountEditText.getText().length()==0 || passwordEditText.getText().length()==0)
        {
            Toast.makeText(this,"登入資訊不可為空，請重新輸入",Toast.LENGTH_LONG).show();
        }
        else
        {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    resultString = DB_Connector.updatingData(signInUrl + account + accountEditText.getText().toString() + "&&" + password + passwordEditText.getText().toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (resultString.equals("signIn"))
                            {
                                Toast.makeText(Main2Activity.this, "登入成功", Toast.LENGTH_SHORT).show();
                                SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.sharedPreference_Key),MODE_PRIVATE);
                                sharedPreferences.edit()
                                        .putString(getResources().getString(R.string.user),accountEditText.getText().toString())
                                        .apply();
                                Intent intent = new Intent();
                                intent.setClass(Main2Activity.this, Main3Activity.class);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                Toast.makeText(Main2Activity.this, "登入資訊有誤", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
            thread.start();
        }
    }

    public void Register (View view)
    {
        Intent intent18 = new Intent();
        intent18.setClass(Main2Activity.this,Main18Activity.class);
        startActivity(intent18);
    }

}
