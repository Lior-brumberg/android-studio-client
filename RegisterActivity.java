package com.example.brumb.firebasetester;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
// register name, pass and discription activity
    EditText name, pass, info, fullname;
    byte[] hash;
    FirebaseFirestore DB = FirebaseFirestore.getInstance();
    CollectionReference users = DB.collection("Users");
    Context c = this;
    Button register;
    public static final String EXTRA_MESSAGE = "com.example.firebasetester.EXTRA";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = (EditText) findViewById(R.id.UserNameRegister);
        pass = (EditText) findViewById(R.id.PasswordRegister);
        info = (EditText) findViewById(R.id.UserDiscription);
        register = (Button) findViewById(R.id.RegisterBTN);
        fullname = (EditText) findViewById(R.id.FullName);

        final LoginActivity LA = new LoginActivity();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    hash = LA.Hash(pass.getText().toString());
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                Task<DocumentSnapshot> doc = users.document(name.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            Toast.makeText(c, R.string.UserNameTaken, Toast.LENGTH_LONG).show();
                        } else {
                            if(fullname.getText().toString() == "" || pass.getText().toString() == "" || name.getText().toString() == "")
                            {
                                Toast.makeText(c, R.string.SomethingWentWrong
                                        , Toast.LENGTH_LONG).show();
                            } else {
                                DocumentReference ref = users.document(name.getText().toString());
                                Map<String, String> map = new HashMap<>();
                                map.put("Full Name", fullname.getText().toString());
                                map.put("UserName", name.getText().toString());
                                map.put("Longitude", "N/A");
                                map.put("Latitude", "N/A");
                                map.put("Hash", new String(hash));
                                map.put("Additional Info", info.getText().toString());
                                map.put("IsActive", "False");
                                ref.set(map);
                                Toast.makeText(c, R.string.userNamePassregistered, Toast.LENGTH_LONG).show();

                                //start next activity
                                Intent intent = new Intent(c, EmergencyContact1Activity.class);
                                intent.putExtra(EXTRA_MESSAGE, name.getText().toString() + ":1");
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                });
            }
        });

    }
}
