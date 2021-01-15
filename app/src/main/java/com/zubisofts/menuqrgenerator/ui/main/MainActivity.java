package com.zubisofts.menuqrgenerator.ui.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.zubisofts.menuqrgenerator.R;
import com.zubisofts.menuqrgenerator.model.Response;
import com.zubisofts.menuqrgenerator.model.Restaurant;
import com.zubisofts.menuqrgenerator.ui.auth.LoginActivity;
import com.zubisofts.menuqrgenerator.ui.menu_activity.CreateMenuActivity;
import com.zubisofts.menuqrgenerator.ui.menu_activity.MenuPreviewDialog;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private MainViewModel mainViewModel;
    private MenuListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkUser();

        mainViewModel=new ViewModelProvider.NewInstanceFactory().create(MainViewModel.class);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            final String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
            mainViewModel.fetchRestaurantMenus(uid);
        }

        findViewById(R.id.btnCreateMenu).setOnClickListener(view -> {
            Intent intent=new Intent(MainActivity.this, CreateMenuActivity.class);
            intent.putExtra("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
            intent.putExtra("editing", false);
            startActivity(intent);
        });

        RecyclerView resList=findViewById(R.id.menuList);
        adapter=new MenuListAdapter(new MenuListAdapter.RestaurantListAdapterClickListener() {
            @Override
            public void onRestaurantItemClicked(Restaurant restaurant) {
                MenuPreviewDialog.newInstance(restaurant, MenuPreviewDialog.PreviewMode.MODE_VIEW).show(getSupportFragmentManager(),"Preview");
            }

            @Override
            public void onOptionsButtonClicked(View view, Restaurant restaurant) {
                PopupMenu popupMenu=new PopupMenu(MainActivity.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.res_item_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    switch (menuItem.getItemId()){
                        case R.id.menu_delete:
                            deleteMenu(restaurant.getOwnerId(), restaurant);
                            return true;
                        case R.id.menu_edit:
                            launchEditActivity(restaurant);
                            return true;
                        default:
                            return false;
                    }
                });
                popupMenu.show();
            }
        });

        resList.setAdapter(adapter);
        mainViewModel.getRestaurantMenus().observe(this, response -> {
            if (!response.isError()){
               ArrayList<Restaurant> list = (ArrayList<Restaurant>) response.getData();
               adapter.setRestaurants(list);

               if(list.isEmpty()){
                   findViewById(R.id.emptyLayout).setVisibility(View.VISIBLE);
               }else{
                   findViewById(R.id.emptyLayout).setVisibility(View.GONE);
               }
            }
        });


    }

    private void launchEditActivity(Restaurant restaurant) {

        Intent intent=new Intent(MainActivity.this, CreateMenuActivity.class);
        intent.putExtra("uid", restaurant.getOwnerId());
        intent.putExtra("restaurant", restaurant);
        intent.putExtra("editing", true);
        startActivity(intent);

    }

    private void deleteMenu(String uid, Restaurant restaurant) {

        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Do you really want to delete this menu?")
                .setPositiveButton("Delete", (dialogInterface, i) -> mainViewModel.deleteRestaurantMenu(uid,restaurant.getId()))
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss()).create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.menu_logout){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkUser(){
        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            Intent intent=new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}