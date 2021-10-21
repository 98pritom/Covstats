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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

//   String eml,pass;
//     EditText fullName, editEmailText, editPass;
    EditText fullName,email,password;
     Button signupButton;
     TextView signupToLogin;
     TextView skip;
     FirebaseAuth firebaseAuth;
    FirebaseFirestore fStore;
    boolean valid = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            ((Window) window).setStatusBarColor(getResources().getColor(R.color.white));
        }

        fullName = findViewById(R.id.nametext);
        email = findViewById(R.id.emailinput);
        password = findViewById(R.id.editpass);
        skip = findViewById(R.id.Skip);
        signupButton = findViewById(R.id.signupbutton);
        signupToLogin = findViewById(R.id.loginbutton);

        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();



        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkField(fullName);
                checkField(email);
                checkField(password);

                if (valid){
                    String eml = email.getText().toString();
                    String pass = password.getText().toString();
                    //star the user reg process
                    firebaseAuth.createUserWithEmailAndPassword(eml,pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {

                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Toast.makeText(SignupActivity.this,"Account is created",Toast.LENGTH_SHORT).show();
                            DocumentReference df = fStore.collection("Users").document(user.getUid());
                            Map<String,Object> userInfo = new HashMap<>();
                            userInfo.put("FullName",fullName.getText().toString());
                            userInfo.put("UserEmail",email.getText().toString());
                            userInfo.put("isUser","1");
                            df.set(userInfo);
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignupActivity.this, "Failed to create account", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

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
    public boolean checkField(EditText textField){
        if (textField.getText().toString().isEmpty()){
            textField.setError("Please fill out this field");
            valid = false;
        }
        else{
            valid = true;
        }
        return valid;
    }


}