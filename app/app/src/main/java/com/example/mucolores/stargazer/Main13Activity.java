package com.example.mucolores.stargazer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main13Activity extends AppCompatActivity {

    private ImageButton searchStarImgBtn;
    private Dialog dialog7;
    private ListView resultListView,seasonalListView;
    private EditText keyWordSearchEditText;

    private ArrayList<String> starArrayList;
    private List<Map<String,String>> list;
    private ListViewLayout2ListViewAdapter listViewLayout2ListViewAdapter;
    private String seasonalStarUrl = "http://34.80.175.22/stargazer/13_starSeason.php?";
    private String seasonString = "season",currentSeason;
    private String seasonalStarData;
    private int currentDay,currentMonth;

    private Dialog downloadDialog;
    private String searchStarUrl = "http://34.80.175.22/stargazer/13_relativeStar.php?";
    private String keywordString = "keyword";
    private String searchKeyword;
    private List<Map<String,String>> searchStarList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main13);

        searchStarImgBtn = findViewById(R.id.searchStarImgBtn_ID);
        keyWordSearchEditText = findViewById(R.id.keyWordSearchEditText_ID);
        setSearchStarImgBtn();
        setDialog7();
        resultListView = dialog7.findViewById(R.id.resultListView_ID);
        setResultListView();
        seasonalListView = findViewById(R.id.seasonalListView_ID);

        downloadCurrentSeasonStar();
        parseDataToStarArrayList();
        setSeasonalListView();

    }

    private void setDialog7()
    {
        dialog7 = new Dialog(Main13Activity.this);
        dialog7.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog7.setContentView(R.layout.activity_main7);
        dialog7.setCancelable(true);

        Window window7 = dialog7.getWindow();
        DisplayMetrics displayMetrics7 = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics7);
        WindowManager.LayoutParams layoutParams7 = window7.getAttributes();
        layoutParams7.width = (int)(displayMetrics7.widthPixels*0.9);
        layoutParams7.height = (int)(displayMetrics7.heightPixels*0.7);

        window7.setAttributes(layoutParams7);
    }

    private void setSearchStarImgBtn()
    {
        searchStarImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchKeyword = keyWordSearchEditText.getText().toString();
                searchStarList = new ArrayList<>();
                searchStarList.clear();
                resultListView.setAdapter(null);
                if(searchKeyword.length()==0)
                {
                    Toast.makeText(Main13Activity.this, "搜尋的字串不可為空", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(searchKeyword.length()>5)
                    {
                        Toast.makeText(Main13Activity.this,"建議搜尋的字串不要太長，避免無法尋找相關資料",Toast.LENGTH_LONG).show();
                    }
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run()
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    downloadDialog = new Dialog(Main13Activity.this);
                                    downloadDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    downloadDialog.setContentView(R.layout.activity_main12);
                                    downloadDialog.setCancelable(true);

                                    Window window = downloadDialog.getWindow();
                                    DisplayMetrics displayMetrics = new DisplayMetrics();
                                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                                    WindowManager.LayoutParams layoutParams12 = window.getAttributes();
                                    layoutParams12.width = (int)(displayMetrics.widthPixels*0.7);
                                    layoutParams12.height = (int)(displayMetrics.heightPixels*0.4);
                                    window.setAttributes(layoutParams12);
                                }
                            });
                            String searchData = DB_Connector.updatingData(searchStarUrl + keywordString + "=" + searchKeyword);
                            if(searchData.equals("no"))
                            {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(Main13Activity.this,"抱歉沒有相關的資料，或許可以更改搜尋內容，再尋找看看",Toast.LENGTH_LONG).show();

                                    }
                                });
                            }
                            else
                            {
                                try
                                {
                                    JSONObject jsonObjectAll = new JSONObject(searchData);
                                    JSONArray jsonArray = jsonObjectAll.getJSONArray("data");
                                    for(int i=0;i<jsonArray.length();i++)
                                    {
                                        JSONObject rowJSONObject = jsonArray.getJSONObject(i);
                                        Map<String,String> rowMap = new HashMap<>();
                                        rowMap.put("starName",rowJSONObject.getString("star_name_ch"));
                                        searchStarList.add(rowMap);
                                    }
                                }catch(JSONException e)
                                {
                                    Log.e("JSON Error",e.toString());
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        SimpleAdapter simpleAdapter = new SimpleAdapter
                                                (
                                                        dialog7.getContext(),
                                                        searchStarList,
                                                        R.layout.listview_layout1,
                                                        new String[]{"starName"},
                                                        new int[]{R.id.listViewLayout1TextView1_ID}
                                                );
                                        resultListView.setAdapter(simpleAdapter);
                                        downloadDialog.cancel();
                                        dialog7.show();
                                    }
                                });
                            }
                        }
                    });
                    thread.start();
                }

            }
        });
    }

    private void setResultListView()
    {
        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent8 = new Intent();
                intent8.setClass(Main13Activity.this,Main8Activity.class);
                Bundle bundle8 = new Bundle();
                bundle8.putString(getResources().getString(R.string.bundle_selected_star_key),searchStarList.get(position).get("starName"));
                intent8.putExtras(bundle8);
                startActivity(intent8);
            }
        });
    }

    private void setCurrentSeason()
    {
        Calendar calendar = Calendar.getInstance();
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        currentMonth = calendar.get(Calendar.MONTH)+1;
        if((currentMonth>=4 && currentMonth<=5)||(currentMonth==3 && currentDay>=21) || (currentMonth==6 && currentDay<=21))
        {
            currentSeason="Spring";
        }
        if((currentMonth>=7 && currentMonth<=8)||(currentMonth==6 && currentDay>=22) || (currentMonth==9 && currentDay<=23))
        {
            currentSeason="Summer";
        }
        if((currentMonth>=10 && currentMonth<=11)||(currentMonth==9 && currentDay>=24) || (currentMonth==12 && currentDay<=22))
        {
            currentSeason="Fall";
        }
        if((currentMonth>=1 && currentMonth<=2)||(currentMonth==12 && currentDay>=23) || (currentMonth==3 && currentDay<=20))
        {
            currentSeason="Winter";
        }
    }

    private void downloadCurrentSeasonStar()
    {
        setCurrentSeason();
        Thread downloadSeasonalStar = new Thread(new Runnable() {
            @Override
            public void run() {
                seasonalStarData = DB_Connector.updatingData(seasonalStarUrl + seasonString + "=" + currentSeason);
            }
        });
        Dialog dialog = new Dialog(Main13Activity.this);
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
        downloadSeasonalStar.start();
        try {
            downloadSeasonalStar.join();
            dialog.cancel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void parseDataToStarArrayList()
    {
        starArrayList = new ArrayList<>();
        try {
            JSONObject jsonObjectAll = new JSONObject(seasonalStarData);
            JSONArray jsonArray = jsonObjectAll.getJSONArray("data");
            for(int i=0;i<jsonArray.length();i++)
            {
                starArrayList.add(jsonArray.getJSONObject(i).getString("star_name_ch"));
            }
        } catch (JSONException e) {
            Log.e("JSON error",e.toString());
        }
    }

    private void setSeasonalListView()
    {
        list = new ArrayList<>();
        for(int i=0;i<starArrayList.size();i++)
        {
            Map<String,String> map = new HashMap<>();
            map.put("starName",starArrayList.get(i));
            list.add(map);
        }

        listViewLayout2ListViewAdapter = new ListViewLayout2ListViewAdapter(Main13Activity.this,starArrayList);
        seasonalListView.setAdapter(listViewLayout2ListViewAdapter);
    }

    public class ListViewLayout2ListViewAdapter extends BaseAdapter {

        private Context MainContext;
        private ArrayList<String> starArrayList;
        private int selectedStarPosition;
        private ViewHolder holder;

        class ViewHolder
        {
            Button Button1,Button2,Button3;
        }

        ListViewLayout2ListViewAdapter(Context contextRes,ArrayList<String> dataResource)
        {
            this.MainContext = contextRes;
            this.starArrayList = dataResource;
        }

        @Override
        public int getCount() {
            if(starArrayList.size()%3 == 0)
            {
                return starArrayList.size()/3;
            }
            else
            {
                return (starArrayList.size()/3)+1;
            }
        }

        @Override
        public Object getItem(int position) {
            return starArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(MainContext == null)
            {
                MainContext = convertView.getContext();
            }
            if(convertView == null)
            {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_layout2,null);
                holder.Button1 = convertView.findViewById(R.id.listViewLayout2Button1_ID);
                holder.Button2 = convertView.findViewById(R.id.listViewLayout2Button2_ID);
                holder.Button3 = convertView.findViewById(R.id.listViewLayout2Button3_ID);
                convertView.setTag(holder);
            }
            holder = (ViewHolder) convertView.getTag();
            if((3*position) < starArrayList.size())
            {
                holder.Button1.setText(starArrayList.get(3*position));
            }
            else
            {
                holder.Button1.setText(null);
            }
            if((3*position)+1 < starArrayList.size())
            {
                holder.Button2.setText(starArrayList.get(3*position+1));
            }
            else
            {
                holder.Button2.setText(null);
            }
            if((3*position)+2 < starArrayList.size())
            {
                holder.Button3.setText(starArrayList.get(3*position+2));
            }
            else
            {
                holder.Button3.setText(null);
            }

            holder.Button1.setOnClickListener(new StarNameOnClick(MainContext,3*position));
            holder.Button2.setOnClickListener(new StarNameOnClick(MainContext,3*position+1));
            holder.Button3.setOnClickListener(new StarNameOnClick(MainContext,3*position+2));
            return convertView;
        }

        class StarNameOnClick implements View.OnClickListener
        {
            Context context;
            private int serialNumber;
            StarNameOnClick(Context InContext,int position)
            {
                this.context = InContext;
                serialNumber = position;
            }
            @Override
            public void onClick(View v) {
                selectedStarPosition = serialNumber;
                if(selectedStarPosition>=0 && selectedStarPosition<starArrayList.size())
                {
                    Intent intent8 = new Intent();
                    intent8.setClass(Main13Activity.this,Main8Activity.class);
                    Bundle bundle8 = new Bundle();
                    bundle8.putString(getResources().getString(R.string.bundle_selected_star_key),starArrayList.get(selectedStarPosition));
                    intent8.putExtras(bundle8);
                    startActivity(intent8);
                }
            }
        }
    }

}