package com.zubisofts.menuqrgenerator.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zubisofts.menuqrgenerator.R;
import com.zubisofts.menuqrgenerator.model.Restaurant;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MenuListAdapter extends RecyclerView.Adapter<MenuListAdapter.MenuListItemHolder> {

    private ArrayList<Restaurant> restaurants=new ArrayList<>();
    private RestaurantListAdapterClickListener listAdapterClickListener;

    public MenuListAdapter(RestaurantListAdapterClickListener listAdapterClickListener) {
        this.listAdapterClickListener = listAdapterClickListener;
    }

    @NonNull
    @Override
    public MenuListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_list_item, parent, false);
        return new MenuListItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuListItemHolder holder, int position) {

        final Restaurant restaurant=restaurants.get(position);
        if (restaurant != null){
            holder.txtName.setText(restaurant.getName());
            holder.txtMenuCount.setText(MessageFormat.format("{0} menus created", restaurant.getMenuItems().size()));
            SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            holder.txtDate.setText(dateFormat.format(restaurant.getTimestamp()));

            Glide.with(holder.resLogo)
                    .load(restaurant.getIconUrl())
                    .into(holder.resLogo);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listAdapterClickListener.onRestaurantItemClicked(restaurant);
                }
            });

            holder.btnOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listAdapterClickListener.onOptionsButtonClicked(holder.btnOptions, restaurant);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public void setRestaurants(ArrayList<Restaurant> restaurants) {
        this.restaurants = restaurants;
        notifyDataSetChanged();
    }

    class MenuListItemHolder extends RecyclerView.ViewHolder{

        private ImageView resLogo, btnOptions;
        private TextView txtName, txtMenuCount, txtDate;

        public MenuListItemHolder(@NonNull View itemView) {
            super(itemView);

            resLogo=itemView.findViewById(R.id.imgResLogo);
            btnOptions=itemView.findViewById(R.id.btnOptions);
            txtName=itemView.findViewById(R.id.txtResName);
            txtDate=itemView.findViewById(R.id.txtDate);
            txtMenuCount=itemView.findViewById(R.id.txtMenuCount);
        }
    }

    public interface RestaurantListAdapterClickListener{
        public void onRestaurantItemClicked(Restaurant restaurant);
        public void onOptionsButtonClicked(View view, Restaurant restaurant);
    }
}
