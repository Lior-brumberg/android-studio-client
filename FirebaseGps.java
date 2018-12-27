package com.example.brumb.firebasetester;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseGps implements LocationListener {
    private FirebaseFirestore DB = FirebaseFirestore.getInstance();
    private String USERNAME;
    FirebaseGps(String u)
    {
        this.USERNAME = u;
    }

    @Override
    public void onLocationChanged(Location location) {
        CollectionReference ref = this.DB.collection("Users");
        ref.document(this.USERNAME).update("Longitude", "" + location.getLongitude() );
        ref.document(this.USERNAME).update("Latitude", "" + location.getLatitude() );
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
