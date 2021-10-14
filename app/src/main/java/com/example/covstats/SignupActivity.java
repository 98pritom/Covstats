package com.example.covstats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.contentcapture.DataRemovalRequest;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.covstats.Adapter.CountryWiseAdapter;
import com.example.covstats.Models.CountryWiseModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.DatabaseMetaData;
import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {

    String name, email,password;
    private EditText editNameText, editEmailText, editPass;
    private Button signupButton;
    private TextView signupToLogin;
    private TextView skip;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            ((Window) window).setStatusBarColor(getResources().getColor(R.color.white));
        }

        editNameText = findViewById(R.id.nametext);
        editEmailText = findViewById(R.id.emailinput);
        editPass = findViewById(R.id.editpass);
        skip = findViewById(R.id.Skip);
        signupButton = findViewById(R.id.signupbutton);
        signupToLogin = findViewById(R.id.loginbutton);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = editNameText.getText().toString().trim();
                email = editEmailText.getText().toString().trim();
                password = editPass.getText().toString().trim();

                if (name.isEmpty()){
                    editNameText.setError("Please fill out this field");
                    return;
                }
                if (email.isEmpty()){
                    editEmailText.setError("Please fill out this field");
                    return;
                }
                if (password.isEmpty()){
                    editPass.setError("Please fill out this field");
                    return;
                }
                if (password.length() < 6){
                    editPass.setError("Password must be 6 characters or more");
                    return;
                }

                //register the user in firebase & realtime

                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            //realtime
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("Name",name);
                            map.put("Email",email);
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = user.getUid();
                            FirebaseDatabase.getInstance().getReference().child("User").child(uid).setValue(map);
                            Toast.makeText(SignupActivity.this, "User account created", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }
                        else{
                            Toast.makeText(SignupActivity.this, "Error!"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        signupToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Statistic.class));
            }
        });


    }




}