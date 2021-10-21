package com.example.covstats;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.covstats.Adapter.CountryWiseAdapter;
import com.example.covstats.Models.CountryWiseModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Country_Data extends AppCompatActivity {

    private RecyclerView rv_country_wise;
    private CountryWiseAdapter countryWiseAdapter;
    private ArrayList<CountryWiseModel> countryWiseModelArrayList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EditText et_search;
    ProgressDialog progressDialog;

    private String str_country, str_confirmed, str_confirmed_new, str_active, str_active_new, str_recovered, str_recovered_new,
            str_death, str_death_new, str_tests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_data);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Strong_Cyan)));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //setting up the title to actionbar
        getSupportActionBar().setTitle("World Data (Select Country)");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Initialise all views
        Init();

        //Fetch countrtywise data
        FetchCountryWiseData();

        //Setting swipe refresh layout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FetchCountryWiseData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        //Search
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Filter(s.toString());
            }
        });


    }


    private void Filter(String text) {
        ArrayList<CountryWiseModel> filteredList = new ArrayList<>();
        for (CountryWiseModel item : countryWiseModelArrayList) {
            if (item.getCountry().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        countryWiseAdapter.filterList(filteredList, text);
    }

    private void FetchCountryWiseData() {

        //Show progress dialog
        ShowDialog(); //

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String apiURL = "https://coronavirus-19-api.herokuapp.com/countries/";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                apiURL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            countryWiseModelArrayList.clear();
                            for (int i=0;i<response.length(); i++){
                                System.out.println("=============="+i);
                                JSONObject countryJSONObject = response.getJSONObject(i);

                                str_country = countryJSONObject.getString("country");
                                str_confirmed = countryJSONObject.getString("cases");
                                str_confirmed_new = countryJSONObject.getString("todayCases");
                                str_active = countryJSONObject.getString("active");
                                str_recovered = countryJSONObject.getString("recovered");
                                str_death = countryJSONObject.getString("deaths");
                                str_death_new = countryJSONObject.getString("todayDeaths");
                                str_tests = countryJSONObject.getString("totalTests");

                                //Creating an object of our country model class and passing the values in the constructor
                                CountryWiseModel countryWiseModel  = new CountryWiseModel(str_country, str_confirmed, str_confirmed_new, str_active,
                                        str_death, str_death_new, str_recovered, str_tests);
                                //adding data to our arraylist
                                countryWiseModelArrayList.add(countryWiseModel);
                            }
                            Collections.sort(countryWiseModelArrayList, new Comparator<CountryWiseModel>() {
                                @Override
                                public int compare(CountryWiseModel o1, CountryWiseModel o2) {
                                    if (Integer.parseInt(o1.getConfirmed())>Integer.parseInt(o2.getConfirmed())){
                                        return -1;
                                    } else {
                                        return 1;
                                    }
                                }
                            });

                            Handler makeDelay = new Handler();
                            makeDelay.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    countryWiseAdapter.notifyDataSetChanged();
                                    DismissDialog();
                                }
                            }, 1000);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        requestQueue.add(jsonArrayRequest);

    }

    private void DismissDialog() {
        progressDialog.dismiss();
    }

    private void ShowDialog() {
        //setting up progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void Init() {

        swipeRefreshLayout = findViewById(R.id.activity_country_wise_swipe_refresh_layout);
        et_search = findViewById(R.id.activity_country_wise_search_editText);

        rv_country_wise = findViewById(R.id.activity_country_wise_recyclerview);
        rv_country_wise.setHasFixedSize(true);
        rv_country_wise.setLayoutManager(new LinearLayoutManager(this));

        countryWiseModelArrayList = new ArrayList<>();
        countryWiseAdapter = new CountryWiseAdapter(getApplicationContext(), countryWiseModelArrayList);//maybe changed
        rv_country_wise.setAdapter(countryWiseAdapter);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}