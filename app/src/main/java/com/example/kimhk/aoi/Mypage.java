package com.example.kimhk.aoi;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kimhk on 2019-01-19.
 */

public class Mypage extends Activity {
    String myJSON;
    private static final String TAG_RESULTS = "result";
    private static final String TAG_Date = "Date";
    private static final String TAG_Time="Time";
    private static final String TAG_Cost="Cost";

    public static Intent Map_intent;

    JSONArray peoples = null;
    ArrayList<HashMap<String, String>> personList;

    ListView list;
    Button btn_date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        btn_date = (Button) findViewById(R.id.btn_date);
        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map_intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(Map_intent);
            }
        });

        list = (ListView) findViewById(R.id.listView);
        personList = new ArrayList<HashMap<String, String>>();
        getData("http://jun6726.cafe24.com/Travel_list.php"); //수정 필요
    }
    protected void showList() {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);

            for (int i = 0; i < peoples.length(); i++) {
                JSONObject c = peoples.getJSONObject(i);
                String Date = c.getString(TAG_Date);
                String Time = c.getString(TAG_Time);
                String Cost = c.getString(TAG_Cost);


                HashMap<String, String> persons = new HashMap<String, String>();

                persons.put(TAG_Date, Date);
                persons.put(TAG_Time, Time);
                persons.put(TAG_Cost, Cost);

                personList.add(persons);
            }

            ListAdapter adapter = new SimpleAdapter(
                    Mypage.this, personList, R.layout.list_item,
                    new String[]{TAG_Date, TAG_Time, TAG_Cost},
                    new int[]{R.id.Date, R.id.Time, R.id.Cost}
            );

            list.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getData(String url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }

                    return sb.toString().trim();

                } catch (Exception e) {
                    return null;
                }


            }

            @Override
            protected void onPostExecute(String result) {
                myJSON = result;
                showList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }


}
