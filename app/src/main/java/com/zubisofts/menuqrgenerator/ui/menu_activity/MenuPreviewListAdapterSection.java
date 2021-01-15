package com.zubisofts.menuqrgenerator.ui.menu_activity;

import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zubisofts.menuqrgenerator.R;
import com.zubisofts.menuqrgenerator.model.FoodMenuItem;
import com.zubisofts.menuqrgenerator.model.Restaurant;

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class MenuPreviewListAdapterSection extends Section {

    private ArrayList<FoodMenuItem> foodMenuItems;
    private Restaurant restaurant;

    public MenuPreviewListAdapterSection(ArrayList<FoodMenuItem> foodMenuItems, Restaurant restaurant) {
        super(SectionParameters.builder()
                .itemViewWillBeProvided()
                .headerResourceId(R.layout.preview_header_item)
                .build());

        this.foodMenuItems=foodMenuItems;
        this.restaurant=restaurant;
    }

    @Override
    public View getItemView(ViewGroup parent) {
        int resId=resolveItemResourceId(restaurant.getPreferences().get("textAlignment").toString());
        View view= LayoutInflater.from(parent.getContext()).inflate(resId, parent,false);
        return view;
    }

    private int resolveItemResourceId(String textAlignment) {
        int alignment=resolveTextAlignment(textAlignment);
        switch (alignment){
            case Gravity.CENTER:
                return R.layout.menu_item_list_center;
            case Gravity.END:
                return R.layout.menu_item_list_right;
            default:
                return R.layout.menu_item_list_left;
        }
    }

    @Override
    public int getContentItemsTotal() {
        return foodMenuItems.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new MenuPreviewItemHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new CategoryHeaderViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {

        MenuPreviewItemHolder menuHolder = (MenuPreviewItemHolder) holder;

        final FoodMenuItem menuItem = foodMenuItems.get(position);
        if (menuItem != null) {

            HashMap settings=restaurant.getPreferences();

            menuHolder.txtName.setText(menuItem.getName());
            Typeface typeface = Typeface.createFromAsset(holder.itemView.getContext().getAssets(), "fonts/"+restaurant.getPreferences().get("textFont")+".ttf");
            menuHolder.txtName.setTypeface(typeface);
            menuHolder.txtName.setTextColor(Integer.parseInt(settings.get("textColor").toString()));

            menuHolder.txtDescription.setText(menuItem.getDescription());
            menuHolder.txtDescription.setTextColor(Integer.parseInt(settings.get("textColor").toString()));
            menuHolder.txtDescription.setTypeface(typeface);

            NumberFormat currencyConverter=NumberFormat.getNumberInstance();
            currencyConverter.setMinimumFractionDigits(2);
            String format = currencyConverter.format(menuItem.getPrice());
            menuHolder.txtPrice.setText(MessageFormat.format("{0} {1}", format, restaurant.getCurrency()));

            menuHolder.txtPrice.setTextColor(Integer.parseInt(settings.get("textColor").toString()));
            menuHolder.txtPrice.setTypeface(typeface);

            menuHolder.btnRemove.setVisibility(View.GONE);
            menuHolder.btnEdit.setVisibility(View.GONE);

        }
    }

    private int resolveTextAlignment(String textAlignment) {
        if (textAlignment.equals("Right")){
            return Gravity.END;
        }else if(textAlignment.equals("Center")){
            return Gravity.CENTER;
        }else{
            return Gravity.START;
        }
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        CategoryHeaderViewHolder categoryHeaderViewHolder=(CategoryHeaderViewHolder)holder;
        categoryHeaderViewHolder.text.setText(foodMenuItems.get(0).getCategory());
        Typeface typeface = Typeface.createFromAsset(holder.itemView.getContext().getAssets(), "fonts/"+restaurant.getPreferences().get("headerFont")+".ttf");
        categoryHeaderViewHolder.text.setTypeface(typeface);
        categoryHeaderViewHolder.text.setTextColor(Integer.parseInt(restaurant.getPreferences().get("headerColor").toString()));

        categoryHeaderViewHolder.text.setGravity(resolveTextAlignment(restaurant.getPreferences().get("textAlignment").toString()));
    }

    class MenuPreviewItemHolder extends RecyclerView.ViewHolder{

        private TextView txtName, txtPrice, txtDescription;
        private ImageView btnRemove, btnEdit;

        public MenuPreviewItemHolder(@NonNull View itemView) {
            super(itemView);

            txtName=itemView.findViewById(R.id.txtFoodName);
            txtDescription=itemView.findViewById(R.id.txtDescription);
            txtPrice=itemView.findViewById(R.id.txtFoodPrice);
            btnRemove=itemView.findViewById(R.id.btnRemoveItem);
            btnEdit=itemView.findViewById(R.id.btnEditItem);
        }
    }

    class CategoryHeaderViewHolder extends RecyclerView.ViewHolder{

        private TextView text;

        public CategoryHeaderViewHolder(@NonNull View itemView) {
            super(itemView);

            text=itemView.findViewById(R.id.txtHeader);

        }
    }
}


