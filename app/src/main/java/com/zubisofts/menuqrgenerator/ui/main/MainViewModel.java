package com.zubisofts.menuqrgenerator.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.zubisofts.menuqrgenerator.model.FoodMenuItem;
import com.zubisofts.menuqrgenerator.model.Response;
import com.zubisofts.menuqrgenerator.model.Restaurant;
import com.zubisofts.menuqrgenerator.repository.DataRepository;

import java.util.ArrayList;

public class MainViewModel extends ViewModel {

    MutableLiveData<Response> restaurantListResponse=new MutableLiveData<>();

    public void fetchRestaurantMenus(String uid) {
        DataRepository.fetchRestaurantMenus(uid, restaurantListResponse);
    }

    public MutableLiveData<Response> getRestaurantMenus(){
        return restaurantListResponse;
    }

    public void deleteRestaurantMenu(String uid, String resId){
        DataRepository.deleteRestaurantMenus(uid,resId);
    }
}
