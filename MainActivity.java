package com.example.brumb.firebasetester;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    Context c =this;
    FirebaseFirestore DB;
    CollectionReference users;
    String CurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DB = FirebaseFirestore.getInstance();
        users = DB.collection("Users");

        Intent intent = getIntent();
        //UserName and contact placment
        CurrentUser = intent.getStringExtra(RegisterActivity.EXTRA_MESSAGE);

        Button start = (Button) findViewById(R.id.angry_btn);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseGps listener = new FirebaseGps(CurrentUser);

                LocationManager maneger = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                if(!isLocationServiceEnabled())
                {
                    Intent gpsOptionsIntent = new Intent(
                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(gpsOptionsIntent);
                }

                DocumentReference User = users.document(CurrentUser);
                User.update("IsActive", "True");

                maneger.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, listener);
            }
        });
    }

    @Override
    public void onPause() {

        super.onPause();
        DocumentReference user = users.document(CurrentUser);
        user.update("IsActive", "False");
    }


    //checks if location is enabled
    public boolean isLocationServiceEnabled(){
        LocationManager locationManager = null;
        boolean gps_enabled= false,network_enabled = false;

        if(locationManager ==null)
            locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
        try{
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch(Exception ex){
            //do nothing...
        }

        try{
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch(Exception ex){
            //do nothing...
        }

        return gps_enabled || network_enabled;

    }

    public void setCurrentUser(String currentUser) {
        CurrentUser = currentUser;
    }

}
