package edu.sdsu.cs.pothole.pothole_sowmya;


import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

public class GetReports extends AppCompatActivity {

    ListView lv;
    RequestQueue requestQueue;
    ArrayList<HashMap<String, String>> list_data = new ArrayList<HashMap<String, String>>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_reports);
        requestQueue = Volley.newRequestQueue(this);
        lv = (ListView) findViewById(R.id.listView);
    }


    public void onStart(){
        super.onStart();
        // Create request queue
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        //  Create json array request
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(Request.Method.GET,"http://bismarck.sdsu.edu/city/batch?type=street&batch-number=0&size=25",new Response.Listener<JSONArray>(){
            public void onResponse(JSONArray jsonArray) {
                // Successfully download json
                // So parse it and populate the listview
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        final JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String id= jsonObject.getString("id").toString();
                        String description = jsonObject.getString("description").toString();
                        String latitude = jsonObject.getString("latitude").toString();
                        String longitude=jsonObject.getString("longitude").toString();
                        String imagetype=jsonObject.getString("imagetype").toString();

                        String type=jsonObject.getString("type").toString();

                        HashMap<String, String> recordData =
                                new HashMap<String, String>();
                        recordData.put("id", id);
                        recordData.put("description", description);
                        recordData.put("latitude", latitude);
                        recordData.put("longitude", longitude);
                        recordData.put("type", type);
                        recordData.put("imagetype",imagetype);
                        list_data.add(recordData);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                String[] from = new String[]{"id", "description"};
                int[] to = new int[]{android.R.id.text1, android.R.id.text2};
                SimpleAdapter adapter = new SimpleAdapter(GetReports.this,
                        list_data, android.R.layout.simple_list_item_2,
                        from, to);
                lv.setAdapter(adapter);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String desc_to_send = list_data.get(position).get("description");
                        String id_to_send = list_data.get(position).get("id");
                        String lat_to_send = list_data.get(position).get("latitude");
                        String long_to_send = list_data.get(position).get("longitude");
                        String type_to_send = list_data.get(position).get("type");
                        String imagetype_to_send=list_data.get(position).get("imagetype");

                        Intent mIntent = new Intent(GetReports.this, DeatiledView.class);

                        mIntent.putExtra("DESC_EXTRA", desc_to_send);
                        mIntent.putExtra("ID_EXTRA", id_to_send);
                        mIntent.putExtra("LAT_EXTRA", lat_to_send);
                        mIntent.putExtra("LONG_EXTRA", long_to_send);
                        mIntent.putExtra("TYPE_EXTRA", type_to_send);
                        mIntent.putExtra("IMAGETYPE_EXTRA", imagetype_to_send);
                        startActivity(mIntent);

                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("Error", "Unable to parse json array");
            }
        });
        // add json array request to the request queue
        requestQueue.add(jsonArrayRequest);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
