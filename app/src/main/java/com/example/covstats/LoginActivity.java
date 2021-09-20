package com.example.covstats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText editEmailText, editPass;
    private Button signinButton;
    private TextView loginToSignup;
    private TextView skip;
    private TextView forgotpassword;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            ((Window) window).setStatusBarColor(getResources().getColor(R.color.white));
        }

        editEmailText = findViewById(R.id.emailInput);
        editPass = findViewById(R.id.loginPass);
        loginToSignup = findViewById(R.id.signinToSignup);
        signinButton = findViewById(R.id.loginbutton);
        skip = findViewById(R.id.skip);
        forgotpassword = findViewById(R.id.forgotpass);
        firebaseAuth = FirebaseAuth.getInstance();

        getSupportActionBar().hide();

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = editEmailText.getText().toString().trim();
                String password = editPass.getText().toString().trim();

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

                //authenticate the user

                firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Login failed"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                //reset password

                forgotpassword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText resetMail = new EditText(view.getContext());
                        AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
                        passwordResetDialog.setTitle("Reset Password");
                        passwordResetDialog.setMessage("Enter your email to receive reset link");
                        passwordResetDialog.setView(resetMail);

                        passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //extract the email and send reset link

                                String mail = resetMail.getText().toString();
                                firebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(LoginActivity.this, "Reset link sent to your email", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(LoginActivity.this, "Error sending mail" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                

                            }

                            private void addOnFailureListener(OnFailureListener onFailureListener) {
                            }
                        });

                        passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //close the dialog
                            }
                        });

                        passwordResetDialog.create().show();
                    }
                });


            }


        });

        loginToSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),SignupActivity.class));
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

    }




}




