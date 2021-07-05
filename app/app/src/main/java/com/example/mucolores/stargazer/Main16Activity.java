package com.example.mucolores.stargazer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class Main16Activity extends AppCompatActivity {

    private ListView listView16;
    private Dialog dialog17;
    private Button goBtn17;
    private DatePicker datePicker17;
    private ListViewLayout3ListViewAdapter listViewLayout3ListViewAdapter;

    private ArrayList<String> PlaceArrayList,AddressArrayList;
    private ArrayList<RowDataSet> rowDataSetsArrayList;

    private String userFavoriteData;
    private String favoriteUrl = "http://34.80.175.22/stargazer/16_getFavorite.php?";
    private String deleteFavoriteUrl = "http://34.80.175.22/stargazer/16_deleteFavorite.php?";
    private String userString = "user",placeString = "place";
    private String userName;
    private String place_nameString = "place_name",IDString="ID",zip_codeString = "zip_code",other_AddressString = "other_Address";
    private ArrayList<String> placeNameArrayList;
    private String getPlaceLocationUrl = "http://34.80.175.22/stargazer/16_getPlaceLocation.php?";
    private String city,place;
    private String placeData;
    private String city_countyString = "city/county";
    private String year,month,day,date;

    private Dialog downLoadDialog;

    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main16);

        listView16 = findViewById(R.id.listView16_ID);
        listView16.setOnItemClickListener(listView16OICL);

        setDialog17();

        datePicker17 = dialog17.findViewById(R.id.stargazeDatePicker_ID);

        goBtn17 = dialog17.findViewById(R.id.Button17_ID);

        setDownLoadDialog();


        goBtn17.setOnClickListener(goBtn17OCL);

        PlaceArrayList = new ArrayList<>();
        AddressArrayList = new ArrayList<>();
        rowDataSetsArrayList = new ArrayList<>();

        SharedPreferences sharedPreferences  = getSharedPreferences(getResources().getString(R.string.sharedPreference_Key),MODE_PRIVATE);
        userName = sharedPreferences.getString(getResources().getString(R.string.user),"Non existing user");
        setListViewData();
    }



    private void setDownLoadDialog()
    {
        downLoadDialog = new Dialog(Main16Activity.this);
        downLoadDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        downLoadDialog.setContentView(R.layout.dialog_layout1);
        downLoadDialog.setCanceledOnTouchOutside(true);

        Window window = downLoadDialog.getWindow();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = (int)(displayMetrics.widthPixels*0.7);
        layoutParams.height = (int)(displayMetrics.heightPixels*0.4);
        window.setAttributes(layoutParams);
    }

    private void setDialog17()
    {
        dialog17 = new Dialog(Main16Activity.this);
        dialog17.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog17.setContentView(R.layout.activity_main17);
        dialog17.setCancelable(true);
        Window window17 = dialog17.getWindow();
        DisplayMetrics displayMetrics17 = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics17);
        WindowManager.LayoutParams layoutParams17 = window17.getAttributes();
        layoutParams17.width = (int)(displayMetrics17.widthPixels*0.9);
        layoutParams17.height = (int)(displayMetrics17.heightPixels*0.8);
        window17.setAttributes(layoutParams17);
    }

    AdapterView.OnItemClickListener listView16OICL = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    };

    View.OnClickListener goBtn17OCL = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            bundle = new Bundle();
            dialog17.cancel();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            downLoadDialog.show();
                        }
                    });
                    placeData = DB_Connector.updatingData(getPlaceLocationUrl + placeString + "=" + place);
                    try
                    {
                        JSONObject jsonObjectAll = new JSONObject(placeData);
                        JSONArray jsonArray = jsonObjectAll.getJSONArray("data");
                        JSONObject dataJSONObject = jsonArray.getJSONObject(0);
                        city = dataJSONObject.getString(city_countyString);
                    }
                    catch(JSONException e)
                    {
                        Log.e("JSON Error",e.toString());
                    }
                    year = String.valueOf(datePicker17.getYear());
                    if((datePicker17.getMonth()+1)<10)
                    {
                        month = '0' + String.valueOf(datePicker17.getMonth()+1);
                    }
                    else{
                        month = String.valueOf(datePicker17.getMonth());
                    }
                    if(datePicker17.getDayOfMonth()<10)
                    {
                        day = '0' + String.valueOf(datePicker17.getDayOfMonth());
                    }
                    else
                    {
                        day = String.valueOf(datePicker17.getDayOfMonth());
                    }
                    date = year + '-' + month + '-' + day;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bundle.putString(getResources().getString(R.string.bundle_date_key),date);
                            bundle.putString(getResources().getString(R.string.bundle_place_key),place);
                            bundle.putString(getResources().getString(R.string.bundle_city_key),city);
                            Intent intent11 = new Intent();
                            intent11.putExtras(bundle);
                            intent11.setClass(Main16Activity.this,Main11Activity.class);
                            downLoadDialog.cancel();
                            startActivity(intent11);
                            finish();
                        }
                    });
                }
            });
            thread.start();
        }
    };

    class RowDataSet
    {
        String serialNumber;
        String NameString,AddressString;

        RowDataSet(String number,String name,String address)
        {
            serialNumber = number;
            NameString = name;
            AddressString = address;
        }
    }

    public class ListViewLayout3ListViewAdapter extends BaseAdapter {

        private Context MainContext;
        private ViewHolder viewHolder;
        private ArrayList<RowDataSet> rowDataSetsArrayListRes;

        class ViewHolder
        {
            TextView numberTextView;
            TextView textView1,textView2;
            ImageButton deleteButton;
            LinearLayout listView_layout3LinearLayout1;
        }

        ListViewLayout3ListViewAdapter(Context InContext,ArrayList<RowDataSet> RowResources)
        {
            this.MainContext = InContext;
            this.rowDataSetsArrayListRes = RowResources;

        }

        @Override
        public int getCount() {
            return rowDataSetsArrayListRes.size();
        }

        @Override
        public Object getItem(int position) {
            return rowDataSetsArrayListRes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(MainContext == null)
            {
                MainContext = convertView.getContext();
            }
            if(convertView == null)
            {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_layout3,parent,false);
                viewHolder.numberTextView = convertView.findViewById(R.id.listViewLayout3TextView1_ID);
                viewHolder.textView1 = convertView.findViewById(R.id.listViewLayout3PlaceNameTextView_ID);
                viewHolder.textView2 = convertView.findViewById(R.id.listViewLayout3PlaceAddressTextView_ID);
                viewHolder.deleteButton = convertView.findViewById(R.id.listViewLayout3ImageButton_ID);
                viewHolder.listView_layout3LinearLayout1 = convertView.findViewById(R.id.listView_layout3LinearLayout1_ID);
                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.numberTextView.setText(rowDataSetsArrayListRes.get(position).serialNumber);
            viewHolder.textView1.setText(rowDataSetsArrayListRes.get(position).NameString);
            viewHolder.textView2.setText(rowDataSetsArrayListRes.get(position).AddressString);
            viewHolder.deleteButton.setOnClickListener(new deleteFavoritePlaceOCL(MainContext,position));
            viewHolder.listView_layout3LinearLayout1.setOnClickListener(new SelectedPlaceOCL(MainContext,position));
            return convertView;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        class deleteFavoritePlaceOCL implements View.OnClickListener
        {
            Context context;
            int positionNumber;

            deleteFavoritePlaceOCL(Context InContext,int InPosition)
            {
                this.context = InContext;
                this.positionNumber = InPosition;
            }

            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setDownLoadDialog();
                                downLoadDialog.show();
                                //Toast.makeText(Main16Activity.this,deleteFavoriteUrl + userString + "=" + userName + "&&" + placeString + "=" + PlaceArrayList.get(positionNumber),Toast.LENGTH_LONG).show();
                                Toast.makeText(Main16Activity.this,"已刪除地點："+PlaceArrayList.get(positionNumber),Toast.LENGTH_LONG).show();
                            }
                        });

                        DB_Connector.updatingData(deleteFavoriteUrl + userString + "=" + userName + "&&" + placeString + "=" + PlaceArrayList.get(positionNumber));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setListViewData();
                                downLoadDialog.cancel();
                            }
                        });
                    }
                });
                thread.start();
            }
        }

        class SelectedPlaceOCL implements View.OnClickListener
        {
            Context context;
            int positionNum;

            SelectedPlaceOCL(Context InContext,int InPosition)
            {
                this.context = InContext;
                this.positionNum = InPosition;
            }

            @Override
            public void onClick(View v) {
                dialog17.show();
                place = PlaceArrayList.get(positionNum);
            }
        }
    }

    private void setListViewData()
    {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        downLoadDialog.show();
                    }
                });
                rowDataSetsArrayList.clear();
                PlaceArrayList.clear();
                AddressArrayList.clear();
                userFavoriteData = DB_Connector.updatingData(favoriteUrl + userString + "=" + userName);
                try {
                    JSONObject jsonObjectAll = new JSONObject(userFavoriteData);
                    JSONArray jsonArray = jsonObjectAll.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObjectRow = jsonArray.getJSONObject(i);
                        PlaceArrayList.add(jsonObjectRow.getString(place_nameString));
                        AddressArrayList.add(jsonObjectRow.getString(other_AddressString));
                        RowDataSet rowDataSet = new RowDataSet(String.valueOf(i+1),PlaceArrayList.get(i),AddressArrayList.get(i));
                        rowDataSetsArrayList.add(rowDataSet);
                    }
                }catch (JSONException e)
                {
                    Log.e("JSON Error",e.toString());
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listViewLayout3ListViewAdapter = new ListViewLayout3ListViewAdapter(Main16Activity.this,rowDataSetsArrayList);
                        listView16.setAdapter(null);
                        listView16.setAdapter(listViewLayout3ListViewAdapter);
                        downLoadDialog.cancel();
                    }
                });
            }
        });
        thread.start();
    }
}
