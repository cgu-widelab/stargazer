package com.example.mucolores.stargazer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main9Activity extends AppCompatActivity {

    private Button goBtn;
    private Spinner citySpinner,sectionSpinner;
    private DatePicker datePicker;
    private Dialog dialog10,dialog;
    private ListView listView10;

    private String dataUrl = "http://34.80.175.22/stargazer/9_getLocation.php";
    private String zipKey = "zip_code",cityKey = "city/county",sectionKey = "section/town";
    private String dataString;
    private ArrayList<String> cityArrayList;
    private ArrayList<ArrayList<String>> locationArrayList;
    ArrayList<HashMap<String,String>> CityAdapterArrayList;

    private String searchPlaceUrl = "http://34.80.175.22/stargazer/9_getPlaceData.php?";
    private String cityString = "city",sectionString = "section",placeKey = "place_name";
    private String placeSearchString;
    private ArrayList<String> searchPlaceArrayList;
    private SimpleAdapter Dialog10ListViewAdapter;

    private Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main9);

        citySpinner = findViewById(R.id.citySpinner_ID);
        sectionSpinner = findViewById(R.id.sectionSpinner_ID);
        datePicker = findViewById(R.id.dataPicker_ID);
        datePicker.setMinDate(System.currentTimeMillis()-1000);
        goBtn = findViewById(R.id.goBtn_ID);
        setDialog10();
        listView10 = dialog10.findViewById(R.id.listView_ID);

        bundle = new Bundle();
        InitialLocation();
        setGoBtn();
        setDialog10ListViewListener();


    }

    private void setGoBtn()
    {
        goBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchPlaceArrayList = new ArrayList<>();
                setDialog10ListView();
            }
        });

    }

    private void setDialog10ListView()
    {
        showDownLoadingDialog(true);
        listView10.setAdapter(null);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                placeSearchString = DB_Connector.updatingData(searchPlaceUrl + cityString + "=" + cityArrayList.get(citySpinner.getSelectedItemPosition()) + "&&" +  sectionString + "=" + locationArrayList.get(citySpinner.getSelectedItemPosition()).get(sectionSpinner.getSelectedItemPosition()));
                if(placeSearchString.equals("NoResult"))
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Main9Activity.this,"抱歉，您所指定的區域目前沒有資料來源",Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else
                {
                    try{
                        JSONObject jsonObject = new JSONObject(placeSearchString);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for(int i=0;i<jsonArray.length();i++)
                        {
                            JSONObject tmpJSONObject = jsonArray.getJSONObject(i);
                            searchPlaceArrayList.add(tmpJSONObject.getString(placeKey));
                        }
                    }catch (JSONException e)
                    {
                        Log.e("JSON Error",e.toString());
                    }
                    ArrayList<Map<String,String>> placeAdapterArrayList = new ArrayList<>();
                    for(int i=0;i<searchPlaceArrayList.size();i++)
                    {
                        Map<String,String> tmp = new HashMap<>();
                        tmp.put(placeKey,searchPlaceArrayList.get(i));
                        placeAdapterArrayList.add(tmp);
                    }
                    Dialog10ListViewAdapter = new SimpleAdapter(
                            dialog10.getContext(),
                            placeAdapterArrayList,
                            R.layout.listview_layout1,
                            new String[]{placeKey},
                            new int []{R.id.listViewLayout1TextView1_ID}
                    );
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listView10.setAdapter(Dialog10ListViewAdapter);
                            dialog10.show();
                        }
                    });

                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showDownLoadingDialog(false);
                    }
                });
            }
        });
        thread.start();

    }

    private void setDialog10ListViewListener()
    {
        listView10.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent11 = new Intent();
                intent11.setClass(Main9Activity.this,Main11Activity.class);
                bundle.putString(getResources().getString(R.string.bundle_place_key),searchPlaceArrayList.get(position));

                String year,month,day;
                year = String.valueOf(datePicker.getYear());
                if(datePicker.getMonth()<10)
                {
                    month = '0'+String.valueOf(datePicker.getMonth()+1);
                }
                else
                {
                    month = String.valueOf(datePicker.getMonth()+1);
                }
                if(datePicker.getDayOfMonth()<10)
                {
                    day = '0'+String.valueOf(datePicker.getDayOfMonth());
                }
                else
                {
                    day = String.valueOf(datePicker.getDayOfMonth());
                }
                bundle.putString(getResources().getString(R.string.bundle_date_key), year + "-" + month + "-" + day);
                bundle.putString(getResources().getString(R.string.bundle_city_key),cityArrayList.get(citySpinner.getSelectedItemPosition()));
                intent11.putExtras(bundle);
                startActivity(intent11);
            }
        });
    }

    private void setDialog10()
    {
        dialog10 = new Dialog(Main9Activity.this);
        dialog10.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog10.setContentView(R.layout.activity_main10);
        dialog10.setCancelable(true);

        Window window10 = dialog10.getWindow();
        DisplayMetrics displayMetrics10 = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics10);
        WindowManager.LayoutParams layoutParams10 = window10.getAttributes();
        layoutParams10.width = (int)(displayMetrics10.widthPixels*0.9);
        layoutParams10.height = (int)(displayMetrics10.heightPixels*0.7);
        window10.setAttributes(layoutParams10);
    }

    private void InitialLocation()
    {
        showDownLoadingDialog(true);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                setLocationData();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setCitySpinner();
                        setSectionSpinner();
                        showDownLoadingDialog(false);
                    }
                });
            }
        });
        thread.start();
    }

    private void showDownLoadingDialog(boolean toShow)
    {
        if(toShow)
        {
            dialog = new Dialog(Main9Activity.this);
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
        }
        else
        {
            dialog.cancel();
        }
    }

    private void setLocationData()
    {
        dataString = DB_Connector.updatingData(dataUrl);
        cityArrayList = new ArrayList<>();
        locationArrayList = new ArrayList<>();
        try {
            JSONObject jsonObjectAll = new JSONObject(dataString);
            JSONArray cityJSONArray = jsonObjectAll.getJSONArray("city");
            for(int i=0;i<cityJSONArray.length();i++)
            {
                JSONObject tmpJSONObject = cityJSONArray.getJSONObject(i);
                cityArrayList.add(tmpJSONObject.getString(cityKey));
                locationArrayList.add(new ArrayList<String>());
            }
            JSONArray locationJSONArray = jsonObjectAll.getJSONArray("data");
            for(int i=0;i<locationJSONArray.length();i++)
            {
                JSONObject tmpJSONObject = locationJSONArray.getJSONObject(i);
                locationArrayList.get(cityArrayList.indexOf(tmpJSONObject.getString(cityKey))).add(tmpJSONObject.getString(sectionKey));
            }
        }
        catch (JSONException e)
        {
            Log.e("JSON Error",e.toString());
        }
    }

    private void setCitySpinner()
    {
        CityAdapterArrayList = new ArrayList<>();
        for(int i=0;i<cityArrayList.size();i++)
        {
            HashMap<String,String> tmpHashMap = new HashMap<>();
            tmpHashMap.put(cityKey,cityArrayList.get(i));
            CityAdapterArrayList.add(tmpHashMap);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter
                (
                        Main9Activity.this,
                        CityAdapterArrayList,
                        R.layout.spinner_layout1,
                        new String[]{cityKey},
                        new int []{R.id.spinnerLayout1TextView1_ID}

                );
        citySpinner.setAdapter(simpleAdapter);
    }

    private void setSectionSpinner()
    {
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<HashMap<String,String>> sectionAdapterArrayList = new ArrayList<>();
                for(int i=0;i<locationArrayList.get(position).size();i++)
                {
                    HashMap<String,String> tmp = new HashMap<>();
                    tmp.put(sectionKey,locationArrayList.get(position).get(i));
                    sectionAdapterArrayList.add(tmp);
                }
                SimpleAdapter sectionSimpleAdapter = new SimpleAdapter(
                        Main9Activity.this,
                        sectionAdapterArrayList,
                        R.layout.spinner_layout1,
                        new String[]{sectionKey},
                        new int []{R.id.spinnerLayout1TextView1_ID}
                );
                sectionSpinner.setAdapter(sectionSimpleAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
