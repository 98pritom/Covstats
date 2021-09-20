package com.example.covstats;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            ((Window) window).setStatusBarColor(getResources().getColor(R.color.white));
        }
        Thread thread = new Thread() {
            public void run() {
                try {

                    sleep(4000);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                    startActivity(intent);
                }
            }
        };thread.start();
    }
}