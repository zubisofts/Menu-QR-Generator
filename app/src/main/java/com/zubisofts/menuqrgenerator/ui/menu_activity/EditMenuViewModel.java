package com.zubisofts.menuqrgenerator.ui.menu_activity;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.zubisofts.menuqrgenerator.model.FoodMenuItem;
import com.zubisofts.menuqrgenerator.model.Response;
import com.zubisofts.menuqrgenerator.model.Restaurant;
import com.zubisofts.menuqrgenerator.repository.DataRepository;

import java.util.ArrayList;
import java.util.Arrays;

public class EditMenuViewModel extends ViewModel {

    private MutableLiveData<ArrayList<FoodMenuItem>> foodItems = new MutableLiveData<>();
    private ArrayList<FoodMenuItem> items=new ArrayList<>();

    private MutableLiveData<Response> menuResponse=new MutableLiveData<>();
    private MutableLiveData<Response> menuItemsResponse=new MutableLiveData<>();

    public void setItem(ArrayList<FoodMenuItem> foodMenuItems) {
        items=foodMenuItems;
        foodItems.postValue(items);
    }

    public void addItem(FoodMenuItem foodMenuItem) {
        items.add(foodMenuItem);
        foodItems.postValue(items);
    }

    public void  modifyItem(FoodMenuItem foodMenuItem, int index) {
        items.set(index, foodMenuItem);
        foodItems.postValue(items);
    }

    public void removeItem(FoodMenuItem foodMenuItem) {
        items.remove(foodMenuItem);
        foodItems.postValue(items);
    }

    public LiveData<ArrayList<FoodMenuItem>> getItems() {
        return foodItems;
    }

    public void createMenu(Restaurant restaurant){
        DataRepository.saveRestaurantDetails(restaurant, menuResponse);
    }

    public void getRestaurantMenu(String restaurantId){
        DataRepository.fetchRestaurantMenu(restaurantId, menuResponse);
    }

    public void getMenuItems(String restaurantId){
        DataRepository.fetchRestaurantMenu(restaurantId, menuItemsResponse);
    }

    public MutableLiveData<Response> getMenuResponse(){
        return menuResponse;
    }

    public MutableLiveData<Response> getMenuItemsResponse(){
        return menuItemsResponse;
    }

    public void updateMenu(Restaurant restaurant, boolean includeLogo) {

        DataRepository.updateRestaurantDetails(restaurant.getOwnerId(),restaurant, menuResponse, includeLogo);
    }
}