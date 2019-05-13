package com.example.kimhk.aoi;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
    private static final String TAG_ID = "user_id";
    private static final String TAG_TRAVEL_NUMBER = "travel_number";
    private static final String TAG_TERM="term";
    private static final String TAG_LOCATION="location";
    private static final String TAG_TRAVEL_PROGRESS="travel_progress";

    public static Intent Map_intent, Item_select_intent, add_travel_intent;

    JSONArray trasvels = null;
    ArrayList<HashMap<String, String>> travelArrayList;

    ListView travelList;
    Button btnDate, btnRemove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        btnDate = (Button) findViewById(R.id.btn_date);
        btnRemove = (Button) findViewById(R.id.btn_remove);
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_travel_intent = new Intent(getApplicationContext(), AddTravelList.class);
                startActivity(add_travel_intent);
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData("http://jun6726.cafe24.com/php_folder/marker_delete.php");
            }
        });

        travelList = (ListView) findViewById(R.id.Travel_List);
        travelArrayList = new ArrayList<HashMap<String, String>>();
        getData("http://jun6726.cafe24.com/php_folder/select_TravelList.php"); //수정 필요
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
            public void onPostExecute(String result) {
                myJSON = result;
                showList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

    public void showList() {
                try {
                    JSONObject jsonObj = new JSONObject(myJSON);
                    trasvels = jsonObj.getJSONArray(TAG_RESULTS);

                    for (int i = 0; i < trasvels.length(); i++) {
                        JSONObject c = trasvels.getJSONObject(i);

                        String user_id = c.getString(TAG_ID);
                        String travel_number = c.getString(TAG_TRAVEL_NUMBER);
                        String term = c.getString(TAG_TERM);
                        String location = c.getString(TAG_LOCATION);
                        String travel_progress = c.getString(TAG_TRAVEL_PROGRESS);

                        HashMap<String, String> persons = new HashMap<String, String>();

                        persons.put(TAG_ID, user_id);
                        persons.put(TAG_TRAVEL_NUMBER, travel_number);
                        persons.put(TAG_TERM, term);
                        persons.put(TAG_LOCATION, location);
                        persons.put(TAG_TRAVEL_PROGRESS,travel_progress);

                        travelArrayList.add(persons);
                    }
                    ListAdapter adapter = new SimpleAdapter(
                            Mypage.this, travelArrayList, R.layout.list_item,
                            new String[]{TAG_ID, TAG_TRAVEL_NUMBER, TAG_TERM, TAG_LOCATION ,TAG_TRAVEL_PROGRESS},
                    new int[]{R.id.ID, R.id.Travel_no, R.id.Term, R.id.Location, R.id.Travel_progress}
            );
            travelList.setAdapter(adapter);

            travelList.setOnItemClickListener(new ListView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Item_select_intent = new Intent(getApplicationContext(), ItemSelect.class);
                    Item_select_intent.putExtra("position", travelArrayList.get(position).get(TAG_ID));
                    startActivity(Item_select_intent);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}