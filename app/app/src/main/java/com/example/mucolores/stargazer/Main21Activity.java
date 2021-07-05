package com.example.mucolores.stargazer;

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
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Main21Activity extends AppCompatActivity {

    private String searchFavorite = "http://34.80.175.22/stargazer/21_getFavorite.php?";
    private String userString = "user",userName;
    private String deleteFavoriteUrl = "http://34.80.175.22/stargazer/21_deleteStar.php?";
    private String starString = "star";
    private ListView listView;
    private String userFavoriteData;
    private ArrayList<RowDataSet> dataSetArrayList;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main21);

        dataSetArrayList = new ArrayList<>();
        listView = findViewById(R.id.listView_ID);
        SharedPreferences sharedPreferences  = getSharedPreferences(getResources().getString(R.string.sharedPreference_Key),MODE_PRIVATE);
        userName = sharedPreferences.getString(getResources().getString(R.string.user),"Non existing user");
        listView.setAdapter(null);

        dialog = new Dialog(Main21Activity.this);
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

        setListViewData();
    }

    private void setListViewData()
    {
        dialog.show();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                userFavoriteData = DB_Connector.updatingData(searchFavorite + userString + "=" + userName);
                dataSetArrayList.clear();
                try{
                    JSONObject jsonObjectAll = new JSONObject(userFavoriteData);
                    JSONArray jsonArray = jsonObjectAll.getJSONArray("data");
                    for (int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject jsonObjectRow = jsonArray.getJSONObject(i);
                        dataSetArrayList.add(new RowDataSet(String.valueOf(i+1),jsonObjectRow.getString("star_name_ch"),jsonObjectRow.getString("star_name")));
                    }
                }catch (JSONException e)
                {
                    Log.e("JSON Error",e.toString());
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listView.setAdapter(null);
                        ListViewLayoutAdapter listViewLayoutAdapter = new ListViewLayoutAdapter(Main21Activity.this,dataSetArrayList);
                        listView.setAdapter(listViewLayoutAdapter);
                        dialog.cancel();
                    }
                });
            }
        });
        thread.start();
    }

    class RowDataSet
    {
        String serialNumber;
        String StarChName,StarEnName;

        RowDataSet(String number,String chName,String enName)
        {
            serialNumber = number;
            StarChName = chName;
            StarEnName = enName;
        }
    }

    public class  ListViewLayoutAdapter extends BaseAdapter
    {
        private Context context;
        private ViewHolder viewHolder;
        private ArrayList<RowDataSet> rowDataSetsArrayListRes;

        class ViewHolder
        {
            TextView numberTextView;
            TextView textView1,textView2;
            ImageButton deleteButton;
            LinearLayout linearLayout;
        }

        ListViewLayoutAdapter(Context InContext,ArrayList<RowDataSet> RowResources)
        {
            this.context = InContext;
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
        public View getView(int position, View convertView, ViewGroup parent) {
            if(context == null)
            {
                context = convertView.getContext();
            }
            if(convertView == null)
            {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_layout3,parent,false);
                viewHolder.numberTextView = convertView.findViewById(R.id.listViewLayout3TextView1_ID);
                viewHolder.textView1 = convertView.findViewById(R.id.listViewLayout3PlaceNameTextView_ID);
                viewHolder.textView2 = convertView.findViewById(R.id.listViewLayout3PlaceAddressTextView_ID);
                viewHolder.deleteButton = convertView.findViewById(R.id.listViewLayout3ImageButton_ID);
                viewHolder.linearLayout = convertView.findViewById(R.id.listView_layout3LinearLayout1_ID);
                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.numberTextView.setText(rowDataSetsArrayListRes.get(position).serialNumber);
            viewHolder.textView1.setText(rowDataSetsArrayListRes.get(position).StarChName);
            viewHolder.textView2.setText(rowDataSetsArrayListRes.get(position).StarEnName);
            viewHolder.deleteButton.setOnClickListener(new deleteFavoriteStareOCL(context,position));
            viewHolder.linearLayout.setOnClickListener(new selectedStarOCL(context,position));

            return convertView;
        }

        class deleteFavoriteStareOCL implements View.OnClickListener
        {
            Context context;
            int position;
            deleteFavoriteStareOCL(Context inContext,int inPosition)
            {
                context = inContext;
                position = inPosition;
            }
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.show();
                                //Toast.makeText(Main16Activity.this,deleteFavoriteUrl + userString + "=" + userName + "&&" + placeString + "=" + PlaceArrayList.get(positionNumber),Toast.LENGTH_LONG).show();
                                Toast.makeText(Main21Activity.this,"已刪除星座："+ dataSetArrayList.get(position).StarEnName,Toast.LENGTH_LONG).show();
                            }
                        });
                        DB_Connector.updatingData(deleteFavoriteUrl + userString + "=" + userName + "&&" + starString + "=" + dataSetArrayList.get(position).StarEnName);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setListViewData();
                                dialog.cancel();
                            }
                        });
                    }
                });
                thread.start();

            }
        }

        class selectedStarOCL implements View.OnClickListener
        {
            Context context;
            int position;
            selectedStarOCL(Context inContext,int inPosition)
            {
                context = inContext;
                position = inPosition;
            }
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString(getResources().getString(R.string.bundle_selected_star_key),rowDataSetsArrayListRes.get(position).StarChName);
                intent.putExtras(bundle);
                intent.setClass(Main21Activity.this,Main8Activity.class);
                startActivity(intent);
                finish();
            }
        }
    }
}
