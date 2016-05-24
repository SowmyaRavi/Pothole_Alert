package edu.sdsu.cs.pothole.pothole_sowmya;

import android.content.Intent;

import android.graphics.Bitmap;

import android.net.Uri;

import android.os.Environment;
import android.provider.MediaStore;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SendData extends AppCompatActivity {

    Button showLocation,submit,clear;
    GPSTracker gps;
    EditText tv1, tv2;
    EditText tv3,tv4,tv5;
    Spinner tv6;
    private ImageView last_photo;
    private ImageButton btn_photo;
    static final int REQUEST_TAKE_PHOTO = 1;
    private Uri outputFileUri;
    private String mCurrentPhotoPath;
    Spinner spinner;
    private Bitmap bitmap;

    double latitude,longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_data);

        tv1 = (EditText) findViewById(R.id.longitude);
        tv2 = (EditText) findViewById(R.id.latitude);
        tv3= (EditText) findViewById(R.id.edit_desc);
        tv4=(EditText) findViewById(R.id.report_type);
        tv5=(EditText) findViewById(R.id.user_id);
        tv6=(Spinner) findViewById(R.id.spinner_image_type);


        spinner = (Spinner) findViewById(R.id.spinner_image_type);
        showLocation = (Button) findViewById(R.id.get_location);
        submit=(Button) findViewById(R.id.btn_submit);
        clear=(Button) findViewById(R.id.button4);
        btn_photo = (ImageButton) findViewById(R.id.btn_photo);
        last_photo = (ImageView) findViewById(R.id.last_photo1);
        last_photo.setDrawingCacheEnabled(true);

        showLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gps = new GPSTracker(SendData.this);
                if (gps.canGetLocation()) {
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                    tv1.setText("" + longitude);
                    tv2.setText("" + latitude);
                }
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv1.setText("");
                tv2.setText("");
                tv3.setText("");
                tv4.setText("");
                tv5.setText("");
                last_photo.setImageResource(0);


            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    btn_photo.setVisibility(View.VISIBLE);
                    btn_photo.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            File file = getOutputMediaFile();
                            outputFileUri = Uri.fromFile(file);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                            mCurrentPhotoPath = file.getAbsolutePath();
                            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                        }
                    });
                } else {
                    btn_photo.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postJsonObject();

            }
        });
    }



    private void postJsonObject() {

        String username = String.valueOf(tv5.getText());
        String imagetype = String.valueOf(tv6.getSelectedItem());
        String description = String.valueOf(tv3.getText());
        String report_type = String.valueOf(tv4.getText());


        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] image=stream.toByteArray();
        System.out.println("byte array:"+image);

        String img_str = Base64.encodeToString(image, Base64.NO_WRAP);
        System.out.println("string:"+img_str);

        String url = "http://bismarck.sdsu.edu/city/report";

        JSONObject data = new JSONObject();
        try {
            data.put("type", report_type);

            data.put("latitude", latitude);
            data.put("longitude", longitude);
            data.put("user", username);
            data.put("imagetype", imagetype);
            data.put("description", description);
            data.put("image", img_str);
        } catch (JSONException error) {
            Log.e("rew", "JSON eorror", error);
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST,url,data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                        Toast.makeText(getApplicationContext(), "Report Submitted", Toast.LENGTH_LONG).show();
                        //hideProgressDialog();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("TAG", "Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(), "Error Sending Data", Toast.LENGTH_LONG).show();
                        // hideProgressDialog();
                    }
                });

        queue.add(jsObjRequest);
    }


    private File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "RoadReporter");

        if (!mediaStorageDir.exists()) {

            mediaStorageDir.mkdir();
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                .format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".png");

        return mediaFile;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        System.out.print("Activity Result");
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            System.out.print("Photo Taken and Result OK");
            if (outputFileUri != null) {

                try {
                    GetImageThumbnail getImageThumbnail = new GetImageThumbnail();
                    bitmap = getImageThumbnail
                            .getThumbnail(outputFileUri, this);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                last_photo.setImageBitmap(bitmap);
                last_photo.setVisibility(View.VISIBLE);
            }
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
