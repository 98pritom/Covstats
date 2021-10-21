package com.example.covstats;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import com.google.firebase.database.core.Context;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;

public class Statistic extends AppCompatActivity {

    private TextView tv_confirmed, tv_confirmed_new, tv_active, tv_active_new, tv_recovered, tv_recovered_new, tv_death,
            tv_death_new, tv_tests, tv_tests_new, tv_date, tv_time;

    private LinearLayout lin_state_data, lin_world_data;

    private String str_confirmed, str_confirmed_new, str_active, str_active_new, str_recovered, str_recovered_new,
            str_death, str_death_new, str_tests, str_tests_new, str_last_update_time;

    private SwipeRefreshLayout swipeRefreshLayout;

    ProgressDialog progressDialog;

    private PieChart pieChart;
    private int int_active_new; //for custom new


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Strong_Cyan)));
        getSupportActionBar().setTitle("COVSTATS (Bangladesh)");

        //Initialize
        Init();

        //Fetch data from API
        FetchData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FetchData();
                swipeRefreshLayout.setRefreshing(false);
                //Toast.makeText(MainActivity.this, "Data refreshed!", Toast.LENGTH_SHORT).show();
            }
        });

        lin_world_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Statistic.this,World_Data.class));
            }
        });

    }

    private void FetchData() {

        //show progress dialog
        ShowDialog(this);
        

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://coronavirus-19-api.herokuapp.com/countries/Bangladesh";
        pieChart.clearChart();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                try {

                    //JSONArray jsonArray = new JSONArray(response);

                    JSONObject jsonObject = new JSONObject(response);
                    str_confirmed = jsonObject.getString("cases");   //Confirmed cases
                    str_confirmed_new = jsonObject.getString("todayCases");   //New Confirmed cases from last update time
//                    System.out.println(cnt + "  " + startTime + "------------------------------------------");
                    str_active = jsonObject.getString("active");    //Active cases

                    str_recovered = jsonObject.getString("recovered");  //Total recovered cased
                    str_recovered_new = "55"; //New recovered cases from last update time


                    str_death = jsonObject.getString("deaths");     //Total deaths
                    str_death_new = jsonObject.getString("todayDeaths");    //New death cases from last update time


                    str_last_update_time = "33"; //Last update date and time


                    str_tests = jsonObject.getString("totalTests"); //Total samples tested
                    str_tests_new = jsonObject.getString("testsPerOneMillion");   //New samples tested today

                    Handler delayToshowProgress = new Handler();
                    delayToshowProgress.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Setting text in the textview
                            tv_confirmed.setText(NumberFormat.getInstance().format(Integer.parseInt(str_confirmed)));
                            tv_confirmed_new.setText("+" + NumberFormat.getInstance().format(Integer.parseInt(str_confirmed_new)));

                            tv_active.setText(NumberFormat.getInstance().format(Integer.parseInt(str_active)));

                            tv_active_new.setText("+"+NumberFormat.getInstance().format(int_active_new));

                            tv_recovered.setText(NumberFormat.getInstance().format(Integer.parseInt(str_recovered)));
                            tv_recovered_new.setText("+"+NumberFormat.getInstance().format(Integer.parseInt(str_recovered_new)));

                            tv_death.setText(NumberFormat.getInstance().format(Integer.parseInt(str_death)));
                            tv_death_new.setText("+"+NumberFormat.getInstance().format(Integer.parseInt(str_death_new)));

                            tv_tests.setText(NumberFormat.getInstance().format(Integer.parseInt(str_tests)));
                            tv_tests_new.setText("+"+NumberFormat.getInstance().format(Integer.parseInt(str_tests_new)));

                            pieChart.addPieSlice(new PieModel("Active", Integer.parseInt(str_active), Color.parseColor("#007afe")));
                            pieChart.addPieSlice(new PieModel("Recovered", Integer.parseInt(str_recovered), Color.parseColor("#08a045")));
                            pieChart.addPieSlice(new PieModel("Deceased", Integer.parseInt(str_death), Color.parseColor("#F6404F")));

                            pieChart.startAnimation();

                            DismissDialog();


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

    private void DismissDialog() {
        progressDialog.dismiss();
    }

    private void ShowDialog(Statistic context) {
        //setting up progress dialog
        progressDialog = new ProgressDialog(context);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }


    private void Init() {
        tv_confirmed = findViewById(R.id.activity_main_confirmed_textview);
        tv_confirmed_new = findViewById(R.id.activity_main_confirmed_new_textview);
        tv_active = findViewById(R.id.activity_main_active_textview);
        tv_active_new = findViewById(R.id.activity_main_active_new_textview);
        tv_recovered = findViewById(R.id.activity_main_recovered_textview);
        tv_recovered_new = findViewById(R.id.activity_main_recovered_new_textview);
        tv_death = findViewById(R.id.activity_main_death_textview);
        tv_death_new = findViewById(R.id.activity_main_death_new_textview);
        tv_tests = findViewById(R.id.activity_main_samples_textview);
        tv_tests_new = findViewById(R.id.activity_main_samples_new_textview);
//        tv_date = findViewById(R.id.activity_main_date_textview);
//        tv_time = findViewById(R.id.activity_main_time_textview);

        pieChart = findViewById(R.id.activity_main_piechart);
        swipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
//        lin_state_data = findViewById(R.id.activity_main_statewise_lin);
        lin_world_data = findViewById(R.id.activity_main_world_data_lin);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_about){
            //Toast.makeText(MainActivity.this, "About menu icon clicked", Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(Statistic.this, AboutActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }




}