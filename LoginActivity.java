package com.example.brumb.firebasetester;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.provider.DocumentsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

    public EditText user, pass;
    Button signIn, signUp;
    FirebaseFirestore DB = FirebaseFirestore.getInstance();
    CollectionReference users = DB.collection("Users");
    byte[] hash;
    Context c = this;
    String MY_PREFS_NAME = "ActivityPREF";
    public static String EXTRA_MESSAGE = "com.example.firebasetester.EXTRA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final SharedPreferences pref = getApplicationContext().getSharedPreferences(MY_PREFS_NAME, 0); // 0 - for private mode
        final SharedPreferences.Editor editor = pref.edit();

        if(isLoggedIn()) {
            //not first launch
            Intent intent = new Intent(c, MainActivity.class);
            intent.putExtra(EXTRA_MESSAGE, pref.getString("UserName", null));
            startActivity(intent);
            finish();
        }
        else {
            // layout components
            user = (EditText) findViewById(R.id.UserName);
            pass = (EditText) findViewById(R.id.Password);

            signIn = (Button) findViewById(R.id.SignIn);
            signUp = (Button) findViewById(R.id.SignUp);

            //ask for permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 99);
            }

            signIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //hashing the password
                    try {
                        hash = Hash(pass.getText().toString());
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }

                    //doing the
                    Task<DocumentSnapshot> doc = users.document(user.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                if (new String(hash).equals(documentSnapshot.get("Hash").toString())) {

                                    Toast.makeText(c, R.string.LoggedIn, Toast.LENGTH_LONG).show();

                                    //save current user
                                    Intent intent = new Intent(c, MainActivity.class);
                                    intent.putExtra(EXTRA_MESSAGE, user.getText().toString());

                                    //save prefs for next launch
                                    editor.putString("UserName", user.getText().toString());
                                    editor.putBoolean("IsFirstTime", true);
                                    editor.apply();
                                    startActivity(intent);

                                    finish();
                                } else
                                    Toast.makeText(c, R.string.LoginErrorCrodentials, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(c, R.string.LoginErrorUser, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            });

            signUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent registerIntent = new Intent(c, RegisterActivity.class);
                    startActivity(registerIntent);
                }
            });
        }

    }
    public byte[] Hash(String plainText) throws NoSuchAlgorithmException {
        //hashes by sha-256
        byte[] message = plainText.getBytes();
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(message);
        return digest;
    }
    public boolean isLoggedIn(){
        //checks the shared preferences for past logins
        final SharedPreferences pref = getApplicationContext().getSharedPreferences(MY_PREFS_NAME, 0); // 0 - for private mode
        return pref.getBoolean("IsFirstTime", false);
    }
}
