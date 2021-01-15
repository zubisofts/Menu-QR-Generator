package com.zubisofts.menuqrgenerator.ui.menu_activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.zubisofts.menuqrgenerator.R;
import com.zubisofts.menuqrgenerator.model.FoodMenuItem;
import com.zubisofts.menuqrgenerator.model.Response;
import com.zubisofts.menuqrgenerator.model.Restaurant;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CreateMenuActivity extends AppCompatActivity implements PageChangedListener{

    private static final String TAG = "CreateMenuActivity";

    private ViewPager viewPager;
    private HashMap<String,Object> preferences;
    private Restaurant restaurant;

    private EditMenuViewModel editMenuViewModel;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_menu);
        editMenuViewModel = new ViewModelProvider.NewInstanceFactory().create(EditMenuViewModel.class);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setDefaults();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(CreateMenuActivity.this)
                        .setTitle("Confirm")
                        .setMessage("You have unsaved menu, do you really want to close this edit?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create().show();
            }
        });


        progressDialog=new ProgressDialog((this));
        progressDialog.setMessage("Saving your menu...");
        progressDialog.setCancelable(false);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        viewPager = findViewById(R.id.view_pager);

        sectionsPagerAdapter.addPage(EditAppearanceFragment.newInstance(this, restaurant));
        sectionsPagerAdapter.addPage(EditMenuFragment.newInstance(this, restaurant));

        viewPager.setAdapter(sectionsPagerAdapter);

        editMenuViewModel.getMenuResponse().observe(this, new Observer<Response>() {
            @Override
            public void onChanged(Response response) {
                if(!response.isError()){
                    Toast.makeText(CreateMenuActivity.this, "Menu saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    Toast.makeText(CreateMenuActivity.this, response.getData().toString(), Toast.LENGTH_SHORT).show();
                }

                progressDialog.dismiss();
            }
        });

    }

    private void setDefaults() {

        boolean isEditing=getIntent().getBooleanExtra("editing",false);

        if(isEditing){

            restaurant= (Restaurant) getIntent().getSerializableExtra("restaurant");
//            Toast.makeText(this, ""+restaurant.getMenuItems().size(), Toast.LENGTH_SHORT).show();

        }else{
            preferences=new HashMap<>();
            preferences.put("headerFont","Lato");
            preferences.put("textFont","Lato");
            preferences.put("headerColor",-16777216);
            preferences.put("textAlignment","Left");
            preferences.put("textColor",-16777216);
            preferences.put("bgColor",-16777216);

            restaurant=new Restaurant(
                    "",
                    "Restaurant Name",
                    "",
                    getIntent().getStringExtra("uid"),
                    preferences,
                    "NGN",
                    0
            );

        }

    }

    @Override
    public void onNextPageClicked(boolean next) {
//        restaurant.setPreferences(pref);
        if (next)
        viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
    }

    @Override
    public void onFormCompleted(Restaurant data, boolean done, boolean includeLogo) {

        restaurant.setName(data.getName());
        restaurant.setCurrency(data.getCurrency());
        restaurant.setIconUrl(data.getIconUrl());
        restaurant.setMenuItems(data.getMenuItems());
        restaurant.setOwnerId(getIntent().getStringExtra("uid"));
        if (data.getTimestamp()==0){
            restaurant.setTimestamp(new Date().getTime());
        }else {
            restaurant.setTimestamp(data.getTimestamp());
        }

        if (done) {
            progressDialog.show();
            if(getIntent().getBooleanExtra("editing",false)){
                editMenuViewModel.updateMenu(restaurant, includeLogo);
            }else {
                editMenuViewModel.createMenu(restaurant);
            }

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.qr_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.menu_preview){
            MenuPreviewDialog.newInstance(restaurant, MenuPreviewDialog.PreviewMode.MODE_EDIT).show(getSupportFragmentManager(),"Menu Preview");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("You have unsaved menu, do you really want to close this edit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();

    }
}