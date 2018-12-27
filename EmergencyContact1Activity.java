package com.example.brumb.firebasetester;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.Map;

public class EmergencyContact1Activity extends AppCompatActivity {

    String UserName;
    Integer ContactNumber;
    EditText Cname, Cphone, Cemail;
    Button AddBtn;
    Context c = this;
    public static final String EXTRA_MESSAGE = "com.example.firebasetester.EXTRA";
    //extra collection
    FirebaseFirestore DB = FirebaseFirestore.getInstance();
    DocumentReference CurrentUser;
    CollectionReference EC;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact1);

        TextView ECBT = (TextView) findViewById(R.id.EmergencyContactBlock);
        //retrieve Extra Data
        Intent intent = getIntent();
        //UserName and contact placment
        String Details = intent.getStringExtra(RegisterActivity.EXTRA_MESSAGE);
        UserName = Details.split(":")[0];
        ContactNumber = Integer.parseInt(Details.split(":")[1]);

        //collections
        CurrentUser = DB.document("Users/" + UserName);
        EC = CurrentUser.collection("Emergency Contacts");
        //for tracking
        ECBT.append("(#" + ContactNumber + ")");


        Cname = (EditText) findViewById(R.id.ECname1);
        Cphone = (EditText) findViewById(R.id.ECphone1);
        Cemail = (EditText) findViewById(R.id.ECemail1);

        AddBtn = (Button) findViewById(R.id.AddContactBtn);

        AddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Cphone.getText().toString().length() < 10 || Cphone.getText().toString().length() > 10)
                {
                    Toast.makeText(c, R.string.RegisteringErrorCrodentials, Toast.LENGTH_LONG).show();
                }
                else {
                    DocumentReference contact = EC.document(Cname.getText().toString());
                    Map<String, String> map = new HashMap<>();
                    map.put("Phone", Cphone.getText().toString());
                    map.put("Email", Cemail.getText().toString());

                    contact.set(map);

                    if (ContactNumber == 3) {
                        Toast.makeText(c, R.string.EndOfSignUP, Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Intent intent = new Intent(c, EmergencyContact1Activity.class);
                        intent.putExtra(EXTRA_MESSAGE, UserName + ":" + (ContactNumber + 1));
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });

    }
}
