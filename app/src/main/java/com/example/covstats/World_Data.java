package com.example.covstats;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;

public class World_Data extends AppCompatActivity {

    TextView tv_confirmed, tv_confirmed_new, tv_active, tv_active_new,
            tv_recovered, tv_recovered_new, tv_death, tv_death_new, tv_tests;

    SwipeRefreshLayout swipeRefreshLayout;

    String str_confirmed, str_confirmed_new, str_active, str_active_new, str_recovered, str_recovered_new,
            str_death, str_death_new, str_tests;

    LinearLayout lin_countrywise;

    ProgressDialog progressDialog;

    PieChart pieChart;
    private int int_active_new=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_world_data);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Strong_Cyan)));
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().setTitle("COVSTATS (World)");

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.themeMain));
        }


        //Initialise UI
        Init();

        //Fetch world's data
        FetchWorldData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FetchWorldData();
                swipeRefreshLayout.setRefreshing(false);
                //Toast.makeText(MainActivity.this, "Data refreshed!", Toast.LENGTH_SHORT).show();
            }
        });

        lin_countrywise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "Country wise data", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), Country_Data.class));
            }
        });
        
    }

    private void ShowDialog(){
        //setting up progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

    }

    private void DissmissDialog(){
        progressDialog.dismiss();
    }

    private void FetchWorldData() {

        //show dialog
        ShowDialog();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://coronavirus-19-api.herokuapp.com/countries/World";
        pieChart.clearChart();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                try {

                    //JSONArray jsonArray = new JSONArray(response);

                    JSONObject jsonObject = new JSONObject(response);
                    str_confirmed = jsonObject.getString("cases");
                    str_confirmed_new = jsonObject.getString("todayCases");
                    str_active = jsonObject.getString("active");
                    str_recovered = jsonObject.getString("recovered");
                    str_recovered_new = "1";
                    str_death = jsonObject.getString("deaths");
                    str_death_new = jsonObject.getString("todayDeaths");
                    str_tests = jsonObject.getString("totalTests");

                    pieChart.addPieSlice(new PieModel("Active", Integer.parseInt(str_active), Color.parseColor("#007afe")));
                    pieChart.addPieSlice(new PieModel("Recovered", Integer.parseInt(str_recovered), Color.parseColor("#08a045")));
                    pieChart.addPieSlice(new PieModel("Deceased", Integer.parseInt(str_death), Color.parseColor("#F6404F")));

                    pieChart.startAnimation();
                    DissmissDialog();


                    Handler delayToshowProgress = new Handler();
                    delayToshowProgress.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // setting up texted in the text view
                            tv_confirmed.setText(NumberFormat.getInstance().format(Integer.parseInt(str_confirmed)));
                            tv_confirmed_new.setText("+"+NumberFormat.getInstance().format(Integer.parseInt(str_confirmed_new)));

                            tv_active.setText(NumberFormat.getInstance().format(Integer.parseInt(str_active)));

                            int_active_new = Integer.parseInt(str_confirmed_new)
                                    - (Integer.parseInt(str_recovered_new) + Integer.parseInt(str_death_new));
                            tv_active_new.setText("+"+NumberFormat.getInstance().format(int_active_new));

                            tv_recovered.setText(NumberFormat.getInstance().format(Integer.parseInt(str_recovered)));
                            tv_recovered_new.setText("N/A");

                            tv_death.setText(NumberFormat.getInstance().format(Integer.parseInt(str_death)));
                            tv_death_new.setText("+"+NumberFormat.getInstance().format(Integer.parseInt(str_death_new)));

                            tv_tests.setText(NumberFormat.getInstance().format(Long.parseLong(str_tests)));

                        }
                    },1000);



                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                System.out.println(error);

            }
        }
        );


        requestQueue.add(stringRequest);

    }

    private void Init() {

        tv_confirmed = findViewById(R.id.activity_world_data_confirmed_textView);
        tv_confirmed_new = findViewById(R.id.activity_world_data_confirmed_new_textView);
        tv_active = findViewById(R.id.activity_world_data_active_textView);
        tv_active_new = findViewById(R.id.activity_world_data_active_new_textView);
        tv_recovered = findViewById(R.id.activity_world_data_recovered_textView);
        tv_recovered_new = findViewById(R.id.activity_world_data_recovered_new_textView);
        tv_death = findViewById(R.id.activity_world_data_death_textView);
        tv_death_new = findViewById(R.id.activity_world_data_death_new_textView);
        tv_tests = findViewById(R.id.activity_world_data_tests_textView);
        swipeRefreshLayout = findViewById(R.id.activity_world_data_swipe_refresh_layout);
        pieChart = findViewById(R.id.activity_world_data_piechart);
        lin_countrywise = findViewById(R.id.activity_world_data_countrywise_lin);

    }
    //to ho back to to homepage
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}