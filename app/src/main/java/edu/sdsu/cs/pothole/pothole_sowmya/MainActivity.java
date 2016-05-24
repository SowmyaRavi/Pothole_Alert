package edu.sdsu.cs.pothole.pothole_sowmya;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void clickthis(View v){
        Button b=(Button) findViewById(R.id.button);
        Intent go= new Intent(MainActivity.this,SendData.class);
        startActivity(go);
    }

    public void get_report(View v){
        Button b=(Button) findViewById(R.id.button3);
        Intent go= new Intent(MainActivity.this,GetReports.class);
        startActivity(go);
    }
}
