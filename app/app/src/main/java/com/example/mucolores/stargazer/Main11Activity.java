package com.example.mucolores.stargazer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Main11Activity extends AppCompatActivity {

    private TextView placeNameTextView,sunsetTextView;
    private ImageButton favoriteImageButton;
    private Button extendWeatherButton,routeButton;
    private Dialog dialog12;
    private Button button12;
    private EditText startEditText;
    private TextView destinationTextView;
    private LineChart tempChart;
    private LineChart rainChart;

    private Bundle receiveBundle;

    private String placeName,city,date;
    private String weather3SunsetURL = "http://34.80.175.22/stargazer/11_sectionWeather3.php?";
    private String weather7SunsetURL = "http://34.80.175.22/stargazer/11_sectionWeather7.php?";
    private String placeNameString = "placeName",cityString = "city",dateString = "date";
    private String zip_codeString = "zip_code",
            dd_DateTimeString = "dd_DateTime",
            TString = "T",
            RHString = "RH",
            PoP6hString="PoP6h",
            PoP24hString = "PoP24h",
            CIString = "CI",
            WxString = "Wx",
            WDString = "WD";

    private String areaString = "area",
            ss_DateString = "ss_Date",
            sr_TimeString = "sr_Time",
            ss_TimeString = "ss_Time";

    private String data;
    private ArrayList<HashMap<String,String>> weatherAllData;
    private HashMap<String,String> sunset;

    private String checkFavoriteUrl = "http://34.80.175.22/stargazer/11_checkFavorite.php?";
    private String savePlaceUrl = "http://34.80.175.22/stargazer/11_savePlace.php?";
    private String deletePlaceUrl = "http://34.80.175.22/stargazer/11_deletePlae.php?";
    private String userName;
    private String userString = "user",placeString = "place";
    private boolean like;

    private String getPlaceUrl = "http://34.80.175.22/stargazer/11_getPlaceAddress.php?";
    private String placeData;
    private String placeAddress;

    private ListView weatherListView;

    private Dialog dialog;

    private DateUtil dateUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main11);

        Intent intent = Main11Activity.this.getIntent();
        receiveBundle = intent.getExtras();
        favoriteImageButton = findViewById(R.id.favoriteImageButton_ID);
        extendWeatherButton = findViewById(R.id.extendWeatherButton_ID);
        placeNameTextView = findViewById(R.id.placeNameTextView_ID);
        sunsetTextView = findViewById(R.id.sunsetTextView_ID);
        routeButton = findViewById(R.id.routeButton_ID);
        tempChart = findViewById(R.id.tempChart_ID);
        rainChart = findViewById(R.id.rainChart_ID);
        weatherAllData = new ArrayList<>();
        sunset = new HashMap<>();

        initial();
        setDialog12();
        setRouteButton();
        getData();
        setButton12();

    }

    private void initial()
    {
        placeName = receiveBundle.getString(getResources().getString(R.string.bundle_place_key));
        city = receiveBundle.getString(getResources().getString(R.string.bundle_city_key));
        date = receiveBundle.getString(getResources().getString(R.string.bundle_date_key),null);
        placeNameTextView.setText(receiveBundle.getString(getResources().getString(R.string.bundle_place_key)));
        SharedPreferences sharedPreferences  = getSharedPreferences(getResources().getString(R.string.sharedPreference_Key),MODE_PRIVATE);
        userName = sharedPreferences.getString(getResources().getString(R.string.user),"Non existing user");
        dateUtil = new DateUtil();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                placeData = DB_Connector.updatingData(getPlaceUrl + placeString + "=" + placeName);
                try{
                    JSONObject jsonObject = new JSONObject(placeData);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                    placeAddress = jsonObject1.getString("city/county") + jsonObject1.getString("section/town") + jsonObject1.getString("other_Address");
                }catch (JSONException e)
                {
                    Log.e("JSON Error",e.toString());
                }

                if(DB_Connector.updatingData(checkFavoriteUrl + userString + "=" + userName + "&&" + placeString + "=" + placeName).equals("exist"))
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
        thread.start();
        setFavoriteImageButton();
    }

    private void setFavoriteImageButton()
    {
        favoriteImageButton.setOnClickListener(new View.OnClickListener() {
            Dialog downloadDialog;
            @Override
            public void onClick(View v) {
                if(like)
                {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    downloadDialog = new Dialog(Main11Activity.this);
                                    downloadDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    downloadDialog.setContentView(R.layout.dialog_layout1);
                                    downloadDialog.setCanceledOnTouchOutside(true);

                                    Window window = downloadDialog.getWindow();
                                    DisplayMetrics displayMetrics = new DisplayMetrics();
                                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                                    WindowManager.LayoutParams layoutParams = window.getAttributes();
                                    layoutParams.width = (int)(displayMetrics.widthPixels*0.7);
                                    layoutParams.height = (int)(displayMetrics.heightPixels*0.4);
                                    window.setAttributes(layoutParams);

                                    downloadDialog.show();
                                }
                            });
                            DB_Connector.updatingData(deletePlaceUrl + userString + "=" + userName + "&&" + placeString + "=" + placeName);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    favoriteImageButton.setImageResource(android.R.drawable.btn_star_big_off);
                                    like = false;
                                    downloadDialog.cancel();
                                }
                            });
                        }
                    });
                    thread.start();
                }
                else
                {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    downloadDialog = new Dialog(Main11Activity.this);
                                    downloadDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    downloadDialog.setContentView(R.layout.dialog_layout1);
                                    downloadDialog.setCanceledOnTouchOutside(true);

                                    Window window = downloadDialog.getWindow();
                                    DisplayMetrics displayMetrics = new DisplayMetrics();
                                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                                    WindowManager.LayoutParams layoutParams = window.getAttributes();
                                    layoutParams.width = (int)(displayMetrics.widthPixels*0.7);
                                    layoutParams.height = (int)(displayMetrics.heightPixels*0.4);
                                    window.setAttributes(layoutParams);

                                    downloadDialog.show();
                                }
                            });
                            DB_Connector.updatingData(savePlaceUrl + userString + "=" + userName + "&&" + placeString + "=" + placeName);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    favoriteImageButton.setImageResource(android.R.drawable.btn_star_big_on);
                                    like = true;
                                    downloadDialog.cancel();
                                }
                            });
                        }
                    });
                    thread.start();
                }
            }
        });
    }

    private void setExtendWeatherButton()
    {
        extendWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog19 = new Dialog(Main11Activity.this);
                dialog19.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog19.setContentView(R.layout.activity_main19);
                dialog19.setCancelable(true);

                Window window19 = dialog19.getWindow();
                DisplayMetrics displayMetrics11 = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics11);
                WindowManager.LayoutParams layoutParams12 = window19.getAttributes();

                layoutParams12.width = (int)(displayMetrics11.widthPixels*0.9);
                layoutParams12.height = (int)(displayMetrics11.heightPixels*0.7);
                window19.setAttributes(layoutParams12);

                weatherListView = dialog19.findViewById(R.id.weatherListView_ID);
                SimpleAdapter simpleAdapter;
                if(InThreeDay(date))
                {

                    simpleAdapter = new SimpleAdapter
                    (
                        dialog19.getContext(),
                            weatherAllData,
                        R.layout.listview_layout4,
                        new String[]{dd_DateTimeString,TString,RHString,PoP6hString,CIString,WxString,WDString},
                        new int[]{R.id.dd_DateTimeTextView_ID,R.id.TTextView_ID,R.id.RHTextView_ID,R.id.PoPTextView_ID,R.id.CITextView_ID,R.id.WxTextView_ID,R.id.WDTextView_ID}
                    );
                }
                else
                {
                     simpleAdapter = new SimpleAdapter(
                            dialog19.getContext(),
                             weatherAllData,
                            R.layout.listview_layout4,
                            new String[]{dd_DateTimeString,TString,RHString,PoP24hString,CIString,WxString,WDString},
                            new int[]{R.id.dd_DateTimeTextView_ID,R.id.TTextView_ID,R.id.RHTextView_ID,R.id.PoPTextView_ID,R.id.CITextView_ID,R.id.WxTextView_ID,R.id.WDTextView_ID}
                    );
                }
                weatherListView.setAdapter(simpleAdapter);

                dialog19.show();
            }
        });
    }

    private void setDialog12()
    {
        dialog12 = new Dialog(Main11Activity.this);
        dialog12.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog12.setContentView(R.layout.activity_main12);
        dialog12.setCancelable(true);

        Window window12 = dialog12.getWindow();
        DisplayMetrics displayMetrics12 = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics12);
        WindowManager.LayoutParams layoutParams12 = window12.getAttributes();
        layoutParams12.width = (int)(displayMetrics12.widthPixels*0.9);
        layoutParams12.height = (int)(displayMetrics12.heightPixels*0.7);

        window12.setAttributes(layoutParams12);

        button12 = dialog12.findViewById(R.id.startButton_ID);
        startEditText = dialog12.findViewById(R.id.startExitText_ID);
        destinationTextView = dialog12.findViewById(R.id.destinationEditText_ID);
    }

    private void setRouteButton()
    {
        routeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destinationTextView.setText(placeAddress);
                dialog12.show();
            }
        });
    }

    private void setButton12()
    {
        button12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startAddress = "saddr=" + startEditText.getText().toString();
                String destinationAddress = "daddr=" + destinationTextView.getText().toString();

                if(startAddress.length()==0 || destinationAddress.length()==0)
                {
                    Toast.makeText(Main11Activity.this,"輸入的地址不可為空",Toast.LENGTH_LONG).show();
                }
                else
                {
                    String uriString = "http://maps.google.com/maps?" + startAddress + "&" + destinationAddress;

                    Uri uri = Uri.parse(uriString);

                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);

                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

                    startActivity(intent);
                }

            }
        });
    }

    private void getData()
    {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog = new Dialog(Main11Activity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog_layout1);
                        dialog.setCancelable(false);

                        Window window = dialog.getWindow();
                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                        WindowManager.LayoutParams layoutParams = window.getAttributes();
                        layoutParams.width = (int)(displayMetrics.widthPixels*0.7);
                        layoutParams.height = (int)(displayMetrics.heightPixels*0.4);
                        window.setAttributes(layoutParams);
                        dialog.show();

                        tempChart.setPadding(50,0,50,0);
                        XAxis tempXAxis = tempChart.getXAxis();
                        tempXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

                        YAxis tempYLeftAxis,tempYRightAxis;
                        tempYLeftAxis = tempChart.getAxisLeft();
                        tempYLeftAxis.setAxisMaxValue(40);
                        tempYLeftAxis.setAxisMinValue(10);

                        tempYLeftAxis.setLabelCount(4);
                        tempYLeftAxis.setStartAtZero(false);

                        tempYRightAxis = tempChart.getAxisRight();
                        tempYRightAxis.setAxisMaxValue(40);
                        tempYRightAxis.setAxisMinValue(10);

                        tempYRightAxis.setLabelCount(4);
                        tempYRightAxis.setStartAtZero(false);

                        tempChart.setScaleEnabled(false);
                        tempChart.setDoubleTapToZoomEnabled(false);
                        tempChart.offsetLeftAndRight(50);

                        XAxis rainXAxis = rainChart.getXAxis();
                        rainXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        rainChart.setPadding(30,0,30,0);
                        YAxis rainYLeftAxis,rainYRightAxis;

                        rainYLeftAxis = rainChart.getAxisLeft();
                        rainYLeftAxis.setAxisMaxValue(100);
                        rainYLeftAxis.setAxisMinValue(0);

                        rainYLeftAxis.setLabelCount(6);
                        rainYLeftAxis.setStartAtZero(false);

                        rainYRightAxis = rainChart.getAxisRight();
                        rainYRightAxis.setAxisMaxValue(100);
                        rainYRightAxis.setAxisMinValue(0);

                        rainYRightAxis.setLabelCount(6);
                        rainYRightAxis.setStartAtZero(false);

                        rainChart.setScaleEnabled(false);
                        rainChart.setDoubleTapToZoomEnabled(false);
                    }
                });

                if(InThreeDay(date))
                {
                    data = DB_Connector.updatingData(weather3SunsetURL +
                            placeNameString + "=" + placeName + "&&" +
                            cityString + "=" + city + "&&" +
                            dateString + "=" + date);
                    try
                    {
                        JSONObject jsonObjectAll = new JSONObject(data);
                        JSONArray weatherJSONArray = jsonObjectAll.getJSONArray("weather");
                        for(int i=0;i<weatherJSONArray.length();i++)
                        {
                            JSONObject tmpWeather = weatherJSONArray.getJSONObject(i);
                            HashMap<String,String> tmpHashMap = new HashMap<>();
                            tmpHashMap.put(zip_codeString,tmpWeather.getString(zip_codeString));
                            tmpHashMap.put(dd_DateTimeString,tmpWeather.getString(dd_DateTimeString));
                            tmpHashMap.put(TString,tmpWeather.getString(TString));
                            tmpHashMap.put(RHString,tmpWeather.getString(RHString));
                            tmpHashMap.put(PoP6hString,tmpWeather.getString(PoP6hString));
                            tmpHashMap.put(CIString,tmpWeather.getString(CIString));
                            tmpHashMap.put(WxString,tmpWeather.getString(WxString));
                            tmpHashMap.put(WDString,tmpWeather.getString(WDString));
                            weatherAllData.add(tmpHashMap);
                        }


                        JSONArray sunsetJSONArray = jsonObjectAll.getJSONArray("sunset");
                        JSONObject tmpSunset = sunsetJSONArray.getJSONObject(0);
                        sunset.put(areaString,tmpSunset.getString(areaString));
                        sunset.put(ss_DateString,tmpSunset.getString(ss_DateString));
                        sunset.put(sr_TimeString,tmpSunset.getString(sr_TimeString));
                        sunset.put(ss_TimeString,tmpSunset.getString(ss_TimeString));
                    }catch (JSONException e)
                    {
                        Log.e("JSON Error",e.toString());
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            sunsetTextView.setText(sunset.get(ss_TimeString));

                            ArrayList<Entry> tempCharData = new ArrayList<>();
                            ArrayList<String> tempChartLabels = new ArrayList<>();
                            for(int i=0;i<weatherAllData.size();i++)
                            {
                                tempCharData.add(new Entry(Integer.parseInt(weatherAllData.get(i).get(TString)),i));
                                tempChartLabels.add(dateUtil.formatDate(weatherAllData.get(i).get(dd_DateTimeString)));
                            }
                            LineDataSet tempLineDataSet = new LineDataSet(tempCharData,"溫度預測");
                            ArrayList<LineDataSet> tempDataSets = new ArrayList<>();

                            tempLineDataSet.setLineWidth(1f);
                            tempLineDataSet.setDrawCircleHole(true);
                            tempLineDataSet.setValueTextSize(10f);
                            tempLineDataSet.setValueFormatter(new IntegerValueFormatter());
                            tempDataSets.add(tempLineDataSet);
                            tempChart.setDescription("");
                            tempChart.setData(new LineData(tempChartLabels,tempDataSets));

                            //_____________________________________

                            ArrayList<Entry> RainCharData = new ArrayList<>();
                            ArrayList<String> RainChartLabels = new ArrayList<>();
                            int tmpCount = 0;
                            for(int i=0;i<weatherAllData.size();i++)
                            {
                                if(!weatherAllData.get(i).get(PoP6hString).equals("null"))
                                {
                                    RainCharData.add(new Entry(Integer.parseInt(weatherAllData.get(i).get(PoP6hString)),tmpCount));
                                    tmpCount++;
                                    RainChartLabels.add(dateUtil.formatDate(weatherAllData.get(i).get(dd_DateTimeString)));
                                }
                            }
                            LineDataSet RainLineDataSet = new LineDataSet(RainCharData,"降雨機率(%)");
                            ArrayList<LineDataSet> RainDataSets = new ArrayList<>();

                            RainLineDataSet.setLineWidth(1f);
                            RainLineDataSet.setDrawCircleHole(true);
                            RainLineDataSet.setValueTextSize(10f);
                            RainLineDataSet.setValueFormatter(new IntegerValueFormatter());
                            RainDataSets.add(RainLineDataSet);
                            rainChart.setDescription("");
                            rainChart.setData(new LineData(RainChartLabels,RainDataSets));
                        }
                    });

                }
                else
                {
                    data = DB_Connector.updatingData(weather7SunsetURL +
                            placeNameString + "=" + placeName + "&&" +
                            cityString + "=" + city + "&&" +
                            dateString + "=" + date);

                    try
                    {
                        JSONObject jsonObjectAll = new JSONObject(data);
                        JSONArray weatherJSONArray = jsonObjectAll.getJSONArray("weather");
                        for(int i=0;i<weatherJSONArray.length();i++)
                        {
                            JSONObject tmp = weatherJSONArray.getJSONObject(i);
                            HashMap<String,String> tmpHashMap = new HashMap<>();
                            tmpHashMap.put(zip_codeString,tmp.getString(zip_codeString));
                            tmpHashMap.put(dd_DateTimeString,tmp.getString(dd_DateTimeString));
                            tmpHashMap.put(TString,tmp.getString(TString));
                            tmpHashMap.put(RHString,tmp.getString(RHString));
                            tmpHashMap.put(PoP24hString,tmp.getString(PoP24hString));
                            tmpHashMap.put(CIString,tmp.getString(CIString));
                            tmpHashMap.put(WxString,tmp.getString(WxString));
                            tmpHashMap.put(WDString,tmp.getString(WDString));
                            weatherAllData.add(tmpHashMap);
                        }

                        JSONArray sunsetJSONArray = jsonObjectAll.getJSONArray("sunset");
                        JSONObject tmpSunset = sunsetJSONArray.getJSONObject(0);
                        sunset.put(areaString,tmpSunset.getString(areaString));
                        sunset.put(ss_DateString,tmpSunset.getString(ss_DateString));
                        sunset.put(sr_TimeString,tmpSunset.getString(sr_TimeString));
                        sunset.put(ss_TimeString,tmpSunset.getString(ss_TimeString));
                    }catch (JSONException e)
                    {
                        Log.e("JSON Error",e.toString());
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            sunsetTextView.setText(sunset.get(ss_TimeString));

                            ArrayList<Entry> tempCharData = new ArrayList<>();
                            ArrayList<String> tempChartLabels = new ArrayList<>();
                            for(int i=0;i<weatherAllData.size();i++)
                            {
                                tempCharData.add(new Entry(Integer.parseInt(weatherAllData.get(i).get(TString)),i));
                                tempChartLabels.add(dateUtil.formatDate(weatherAllData.get(i).get(dd_DateTimeString)));
                            }
                            LineDataSet tempLineDataSet = new LineDataSet(tempCharData,"溫度預測");
                            ArrayList<LineDataSet> tempDataSets = new ArrayList<>();
                            tempLineDataSet.setLineWidth(1f);
                            tempLineDataSet.setDrawCircleHole(true);
                            tempLineDataSet.setValueTextSize(10f);
                            tempLineDataSet.setValueFormatter(new IntegerValueFormatter());
                            tempDataSets.add(tempLineDataSet);
                            tempChart.setData(new LineData(tempChartLabels,tempDataSets));
                            tempChart.setDescription("");

                            //_____________________________________

                            ArrayList<Entry> RainCharData = new ArrayList<>();
                            ArrayList<String> RainChartLabels = new ArrayList<>();
                            int tmpCount = 0;
                            for(int i=0;i<weatherAllData.size();i++)
                            {
                                if(!weatherAllData.get(i).get(PoP24hString).equals("null"))
                                {
                                    RainCharData.add(new Entry(Float.parseFloat(weatherAllData.get(i).get(PoP24hString)),i));
                                    tmpCount++;
                                    RainChartLabels.add(dateUtil.formatDate(weatherAllData.get(i).get(dd_DateTimeString)));
                                }
                            }

                            LineDataSet RainLineDataSet = new LineDataSet(RainCharData,"降雨機率(%)");
                            ArrayList<LineDataSet> RainDataSets = new ArrayList<>();
                            RainLineDataSet.setLineWidth(1f);
                            RainLineDataSet.setDrawCircleHole(true);
                            RainLineDataSet.setValueTextSize(10f);
                            RainLineDataSet.setValueFormatter(new IntegerValueFormatter());
                            RainDataSets.add(RainLineDataSet);
                            rainChart.setData(new LineData(RainChartLabels,RainDataSets));
                            rainChart.setDescription("");

                        }
                    });
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setExtendWeatherButton();

                        dialog.cancel();
                    }
                });
            }
        });
        thread.start();
    }

    private boolean InThreeDay(String compareDate)
    {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String [] DateArr = compareDate.split("-");
        int cmpYear = Integer.parseInt(DateArr[0]);
        int cmpMonth = Integer.parseInt(DateArr[1]);
        int cmpDay = Integer.parseInt(DateArr[2]);

        if(cmpYear == year && cmpMonth == month && cmpDay <= day+2 && cmpDay>= day)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private class IntegerValueFormatter implements ValueFormatter{

        private DecimalFormat decimalFormat;

        public IntegerValueFormatter()
        {
            decimalFormat = new DecimalFormat("###,###,##0");
        }


        @Override
        public String getFormattedValue(float value) {
            return decimalFormat.format(value);
        }
    }

    private class DateUtil{
        public String formatDate(String str){
            return str.substring(5, 16);
        }
    }
}
