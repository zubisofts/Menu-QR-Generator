package com.zubisofts.menuqrgenerator.ui.menu_activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zubisofts.menuqrgenerator.R;
import com.zubisofts.menuqrgenerator.model.FoodMenuItem;

import java.util.ArrayList;

public class MenuItemListAdapter extends RecyclerView.Adapter<MenuItemListAdapter.MenuItemHolder> {

    private ArrayList<FoodMenuItem> menuItems = new ArrayList<>();
    private MenuItemClickListener menuItemClickListener;

    public MenuItemListAdapter(MenuItemClickListener menuItemClickListener) {
        this.menuItemClickListener = menuItemClickListener;
    }

    @NonNull
    @Override
    public MenuItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item_list_left, parent, false);
        return new MenuItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuItemHolder holder, final int position) {

        final FoodMenuItem menuItem = menuItems.get(position);
        if (menuItem != null) {

            holder.txtName.setText(menuItem.getName());
            holder.txtDescription.setText(menuItem.getDescription());
            holder.txtPrice.setText(String.valueOf(menuItem.getPrice()));

            holder.btnEditItem.setOnClickListener(view -> {
                menuItemClickListener.onEditItemClicked(menuItem, position);
            });

            holder.btnRemove.setOnClickListener(view -> {
                menuItemClickListener.onItemRemoved(menuItem, position);

            });

        }

    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    public void setMenuItems(ArrayList<FoodMenuItem> menuItems) {
        this.menuItems = menuItems;
        notifyDataSetChanged();
    }

    class MenuItemHolder extends RecyclerView.ViewHolder {

        private TextView txtName, txtPrice, txtDescription;
        private ImageView btnRemove, btnEditItem;

        public MenuItemHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtFoodName);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtPrice = itemView.findViewById(R.id.txtFoodPrice);
            btnRemove = itemView.findViewById(R.id.btnRemoveItem);
            btnEditItem = itemView.findViewById(R.id.btnEditItem);
        }
    }

    public interface MenuItemClickListener {
        public void onItemRemoved(FoodMenuItem foodMenuItem, int position);
        public void onEditItemClicked(FoodMenuItem foodMenuItem, int position);
    }
}
