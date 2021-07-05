package com.example.mucolores.stargazer;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

public class Main18Activity extends AppCompatActivity {

    private EditText accountEditText,passwordEditText,otherAddressEditText;
    private TextView accountTextView,passwordTextView,helpTextView;
    private Spinner citySpinner,sectionSpinner;
    private LinearLayout helpLinearLayout;
    private Button registerButton;

    private Dialog dialog;
    private Thread getDataThread,registerThread,waitThread;
    private String registerUrl = "http://34.80.175.22/stargazer/18_register.php?";
    private String dataUrl = "http://34.80.175.22/stargazer/18_getLocation.php";
    private String dataString;
    private String RegisterResult;
    private ArrayList<String> cityArrayList;
    private ArrayList<ArrayList<String>> locationArrayList;
    private String zipKey = "zip_code",cityKey = "city/county",sectionKey = "section/town";
    private String account = "account",password = "password",CS = "CS",otherAddress = "otherAddress";
    private String helpText = "因為本程式有提供導航功能，為了更客製化您的規劃路程，可以大致提供你目前所居住的地址，以便往後計畫觀星路線";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main18);

        accountEditText = findViewById(R.id.accountEditText_ID);
        passwordEditText = findViewById(R.id.passwordEditText_ID);
        otherAddressEditText = findViewById(R.id.otherAddressEditText_ID);
        accountTextView = findViewById(R.id.accountTextView_ID);
        passwordTextView = findViewById(R.id.passwordTextView_ID);
        helpTextView = findViewById(R.id.helpTextView_ID);
        citySpinner = findViewById(R.id.citySpinner_ID);
        sectionSpinner = findViewById(R.id.sectionSpinner_ID);
        helpLinearLayout = findViewById(R.id.helpLinearLayout_ID);
        registerButton = findViewById(R.id.registerButton_ID);
        cityArrayList = new ArrayList<>();
        locationArrayList = new ArrayList<>();

        downloadData();
        setData();
        setSpinner();
        setHelpLinearLayout();
        Register();


    }

    private void downloadData()
    {
        dialog = new Dialog(Main18Activity.this);
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
        waitThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    dialog.cancel();
                } catch (InterruptedException e) {
                    Log.e("Thread Error",e.toString());
                }
            }
        });
        waitThread.start();
        getDataThread = new Thread(new Runnable() {
            @Override
            public void run() {
                dataString = DB_Connector.updatingData(dataUrl);
            }
        });
        getDataThread.start();
        try {
            getDataThread.join();
            //Toast.makeText(this,dataString, Toast.LENGTH_SHORT).show();

        } catch (InterruptedException e) {
            Log.e("SQL ERROR",e.toString());
        }
    }

    private void setData()
    {
        try{
            JSONObject jsonObjectAll = new JSONObject(dataString);

            JSONArray cityJSONArray = jsonObjectAll.getJSONArray("city");
            for(int i=0;i<cityJSONArray.length();i++)
            {
                JSONObject tmp = cityJSONArray.getJSONObject(i);
                cityArrayList.add(tmp.getString("city/county"));
                locationArrayList.add(new ArrayList<String>());
            }

            JSONArray jsonArray = jsonObjectAll.getJSONArray("data");
            for(int i=0;i<jsonArray.length();i++)
            {
                JSONObject tmpJSONObject = jsonArray.getJSONObject(i);
                locationArrayList.get(cityArrayList.indexOf(tmpJSONObject.getString(cityKey))).add(tmpJSONObject.getString(sectionKey));
            }
        }
        catch (JSONException e)
        {
            Log.e("JSON Error",e.toString());
        }
    }

    private void showData()
    {
        StringBuilder sb = new StringBuilder();

        for(int i=0;i<locationArrayList.size();i++)
        {
            for(int j=0;j<locationArrayList.get(i).size();j++)
            {
                sb.append(locationArrayList.get(i).get(j)).append(",");
            }
            sb.append("\n");
        }
        Toast.makeText(Main18Activity.this,sb.toString(),Toast.LENGTH_LONG).show();
    }

    private void setSpinner()
    {
        ArrayList<HashMap<String,String>> adapterArrayList = new ArrayList<>();
        for(int i=0;i<cityArrayList.size();i++)
        {
            HashMap<String,String> tmp = new HashMap<>();
            tmp.put(cityKey,cityArrayList.get(i));
            adapterArrayList.add(tmp);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter
                (
                        Main18Activity.this,
                        adapterArrayList,
                        R.layout.spinner_layout1,
                        new String[]{cityKey},
                        new int []{R.id.spinnerLayout1TextView1_ID}
                );
        citySpinner.setAdapter(simpleAdapter);
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<HashMap<String,String>> adapterArrayList = new ArrayList<>();
                for(int i=0;i<locationArrayList.get(position).size();i++)
                {
                    HashMap<String,String> tmp = new HashMap<>();
                    tmp.put(sectionKey,locationArrayList.get(position).get(i));
                    adapterArrayList.add(tmp);
                }
                SimpleAdapter simpleAdapter = new SimpleAdapter
                        (
                                Main18Activity.this,
                                adapterArrayList,
                                R.layout.spinner_layout1,
                                new String[]{sectionKey},
                                new int []{R.id.spinnerLayout1TextView1_ID}
                        );
                sectionSpinner.setAdapter(simpleAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setHelpLinearLayout()
    {
        helpLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Main18Activity.this,helpText,Toast.LENGTH_LONG).show();
            }
        });
    }

    private void Register()
    {
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder SB = new StringBuilder();
                String accountEmpty = "帳號不可為空",
                        passwordEmpty = "密碼不可為空",
                        otherAddressEmpty = "其他地址不可為空",
                        accountTooLong ="帳號不可超過18字",
                        passwordTooLong = "密碼不可超過18字",
                        accountUsed = "帳號已被使用過",
                        registerSuccess = "申請成功！！";
                boolean basicOk = true;
                accountTextView.setText(null);
                passwordTextView.setText(null);
                helpTextView.setText(null);
                if(accountEditText.getText().length()==0)
                {
                    accountTextView.setText(accountEmpty);
                    accountTextView.setTextColor(getResources().getColor(R.color.red));
                    basicOk = false;
                }
                if(passwordEditText.getText().length()==0)
                {
                    passwordTextView.setText(passwordEmpty);
                    passwordTextView.setTextColor(getResources().getColor(R.color.red));
                    basicOk = false;
                }
                if(otherAddressEditText.getText().length()==0)
                {
                    helpTextView.setText(otherAddressEmpty);
                    helpTextView.setTextColor(getResources().getColor(R.color.red));
                    basicOk = false;
                }
                if(accountEditText.getText().length()>18)
                {
                    accountTextView.setText(accountTooLong);
                    accountTextView.setTextColor(getResources().getColor(R.color.red));
                    basicOk = false;
                }
                if(passwordEditText.getText().length()>18)
                {
                    passwordTextView.setText(passwordTooLong);
                    passwordTextView.setTextColor(getResources().getColor(R.color.red));
                    basicOk = false;
                }
                if(basicOk)
                {
                    dialog = new Dialog(Main18Activity.this);
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
                    registerThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            RegisterResult = DB_Connector.updatingData(registerUrl +
                                    account + "=" +accountEditText.getText() + "&&" +
                                    password + "=" +passwordEditText.getText()+ "&&" +
                                    CS + "=" +cityArrayList.get(citySpinner.getSelectedItemPosition())+locationArrayList.get(citySpinner.getSelectedItemPosition()).get(sectionSpinner.getSelectedItemPosition())+ "&&" +
                                    otherAddress + "=" +otherAddressEditText.getText()
                            );
                        }
                    });
                    registerThread.start();
                    try {
                        registerThread.join();
                        dialog.cancel();
                    } catch (InterruptedException e) {
                        Log.e("RegisterThread Error",e.toString());
                    }
                    if(RegisterResult.equals("failed"))
                    {
                        Toast.makeText(Main18Activity.this,accountUsed,Toast.LENGTH_LONG).show();
                    }
                    else if(RegisterResult.equals("success"))
                    {
                        Toast.makeText(Main18Activity.this,registerSuccess,Toast.LENGTH_LONG).show();
                        finish();
                    }
                }

                if(SB.toString().length()>0)
                {
                    Toast.makeText(Main18Activity.this,SB.toString(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
