package com.zubisofts.menuqrgenerator.model;

import java.io.Serializable;
import java.util.Objects;

public class FoodMenuItem implements Serializable {

    private String id;
    private String name;
    private String description;
    private double price;
    private String category;
    private long timestamp;

    public FoodMenuItem() {
    }

    public FoodMenuItem(String id, String name, String description, double price, String category, long timestamp) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.timestamp=timestamp;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "FoodMenuItem{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
