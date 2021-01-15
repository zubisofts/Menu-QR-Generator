package com.zubisofts.menuqrgenerator.model;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Restaurant implements Serializable {

    private String id;
    private String name;
    private String iconUrl;
    private String ownerId;
    private String currency;
    private HashMap<String,Object> preferences;
    private long timestamp;

    private ArrayList<FoodMenuItem> menuItems;

    public Restaurant() {
    }

    public Restaurant(String id, String name, String iconUrl, String ownerId, HashMap<String, Object> preferences,String currency, long timestamp) {
        this.id = id;
        this.name = name;
        this.iconUrl = iconUrl;
        this.ownerId = ownerId;
        this.preferences = preferences;
        this.timestamp = timestamp;
        this.currency=currency;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public HashMap<String, Object> getPreferences() {
        return preferences;
    }

    public void setPreferences(HashMap<String, Object> preferences) {
        this.preferences = preferences;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public ArrayList<FoodMenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(ArrayList<FoodMenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
