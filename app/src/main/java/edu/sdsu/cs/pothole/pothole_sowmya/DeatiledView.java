package edu.sdsu.cs.pothole.pothole_sowmya;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

public class DeatiledView extends AppCompatActivity {
    double lat;
    double lng;
    ImageView image_view;
    TextView description,id,latitude,longitude,type_1;
    TextView type_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deatiled_view);

        description = (TextView) findViewById(R.id.edit_desc);
        id = (TextView) findViewById(R.id.id2);
        latitude = (TextView) findViewById(R.id.id4);
        longitude = (TextView) findViewById(R.id.id8);
        type_1 = (TextView) findViewById(R.id.id6);
        type_2 = (TextView) findViewById(R.id.id9);

        Button mapView=(Button) findViewById(R.id.button2);


        Intent intent = getIntent();
        Bundle extras=getIntent().getExtras();

        if(extras!=null) {

            String desc = extras.getString("DESC_EXTRA");
            String id_name = extras.getString("ID_EXTRA");
            final String longitude_value=extras.getString("LONG_EXTRA");
            final String latitude_values=extras.getString("LAT_EXTRA");
            String type=extras.getString("TYPE_EXTRA");
            String image_name = extras.getString("IMAGETYPE_EXTRA");

            description.setText(desc);
            id.setText(id_name);
            longitude.setText(longitude_value);
            latitude.setText(latitude_values);
            type_1.setText(type);
            type_2.setText(image_name);
            image_view = (ImageView) findViewById(R.id.imageView);
            String url = "http://bismarck.sdsu.edu/city/image?id="+id_name;


            Response.Listener<Bitmap> success = new Response.Listener<Bitmap>() {
                public void onResponse(Bitmap response) {
                    image_view.setImageBitmap(response);
                }
            };
            Response.ErrorListener failure = new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    Log.d("rew", error.toString());
                }
            };

            ImageRequest ir = new ImageRequest(url,
                    success, 0, 0, ImageView.ScaleType.CENTER_INSIDE, null, failure);
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(ir);



            mapView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    lat = Double.parseDouble(latitude_values.toString());
                    lng = Double.parseDouble(longitude_value.toString());
                    Intent mIntent = new Intent(DeatiledView.this, MapsActivity.class);

                    mIntent.putExtra("lat", lat);
                    mIntent.putExtra("lng", lng);
                    System.out.println("longitude:" + lng);


                    System.out.println("latitude:" + lat);
                    startActivity(mIntent);

                }
            });



        }



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
