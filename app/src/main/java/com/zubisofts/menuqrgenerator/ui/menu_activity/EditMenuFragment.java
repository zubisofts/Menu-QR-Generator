package com.zubisofts.menuqrgenerator.ui.menu_activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mynameismidori.currencypicker.CurrencyPicker;
import com.mynameismidori.currencypicker.CurrencyPickerListener;
import com.zubisofts.menuqrgenerator.R;
import com.zubisofts.menuqrgenerator.model.FoodMenuItem;
import com.zubisofts.menuqrgenerator.model.Response;
import com.zubisofts.menuqrgenerator.model.Restaurant;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class EditMenuFragment extends Fragment implements AddItemFragment.OnSaveClickListener, MenuItemListAdapter.MenuItemClickListener {

    private static final String TAG = "EditMenuFragment";
    private static final int PICK_PHOTO_INTENT = 2021;
    private MenuItemListAdapter adapter;

    private EditMenuViewModel editMenuViewModel;
    private ImageView imgLogo;
    private TextInputEditText edtRestaurantName;

    private static PageChangedListener pageChangedListener;

    private static Restaurant restaurant;

    private Uri logoUri;
    private ArrayList<FoodMenuItem> menuItems = new ArrayList<>();
    private String currency;
    private String resName;

    public static EditMenuFragment newInstance(PageChangedListener listener, Restaurant res) {
        pageChangedListener = listener;
        restaurant = res;
        return new EditMenuFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        editMenuViewModel = new ViewModelProvider.NewInstanceFactory().create(EditMenuViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edit_menu_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imgLogo = view.findViewById(R.id.imgLogo);
        edtRestaurantName = view.findViewById(R.id.edtRestaurantName);

        RecyclerView foodList = view.findViewById(R.id.itemsList);
        foodList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        adapter = new MenuItemListAdapter(this);
        foodList.setAdapter(adapter);
        if (getActivity().getIntent().getBooleanExtra("editing", false)) {
            ((TextView) view.findViewById(R.id.txtSave)).setText("Update Menu");
        }
        setViewInitials(view);

        edtRestaurantName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                resName = charSequence.toString();
                initData(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        view.findViewById(R.id.btnSelectCurrency).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final CurrencyPicker picker = CurrencyPicker.newInstance("Select Currency");  // dialog title
                picker.setListener(new CurrencyPickerListener() {
                    @Override
                    public void onSelectCurrency(String name, String code, String symbol, int flagDrawableResID) {
                        currency = code;
                        ((TextView) view.findViewById(R.id.txtCurrency)).setText(name + " (" + code + ")");
                        ((ImageView) view.findViewById(R.id.imgCurrency)).setImageResource(flagDrawableResID);
                        initData(false);
                        picker.dismiss();
                    }
                });
                picker.show(getParentFragmentManager(), "CURRENCY_PICKER");
            }
        });


        view.findViewById(R.id.btnAddItem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddItemFragment addItemFragment=new AddItemFragment(EditMenuFragment.this, null);
                addItemFragment.show(getParentFragmentManager(), "Add Item");
            }
        });

        editMenuViewModel.getItems().observe(getViewLifecycleOwner(), new Observer<ArrayList<FoodMenuItem>>() {
            @Override
            public void onChanged(ArrayList<FoodMenuItem> foodMenuItems) {
                if (foodMenuItems.isEmpty()) {
                    view.findViewById(R.id.emptyLayout).setVisibility(View.VISIBLE);
                } else {
                    view.findViewById(R.id.emptyLayout).setVisibility(View.GONE);
                    adapter.setMenuItems(foodMenuItems);
                    menuItems = foodMenuItems;
                    restaurant.setMenuItems(foodMenuItems);
                    initData(false);
                }
            }
        });

        editMenuViewModel.getMenuItemsResponse().observe(getViewLifecycleOwner(), new Observer<Response>() {
            @Override
            public void onChanged(Response response) {
                if (!response.isError()){
                    ArrayList<FoodMenuItem> it=(ArrayList<FoodMenuItem>) response.getData();
                    editMenuViewModel.setItem(it);
                    menuItems = it;
                    restaurant.setMenuItems(it);
//                    Toast.makeText(CreateMenuActivity.this, ""+it.size(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        view.findViewById(R.id.btnUploadLogo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_PHOTO_INTENT);
            }
        });

        view.findViewById(R.id.btnComplete).setOnClickListener(v -> {
            if (TextUtils.isEmpty(edtRestaurantName.getText().toString())) {
                ((TextInputLayout) view.findViewById(R.id.inputRestaurantName)).setError("Name must not be empty");
                return;
            }

            if (TextUtils.isEmpty(currency)) {
                Snackbar.make(view, "You must select a currency", Snackbar.LENGTH_SHORT).show();
                return;
            }

            if (menuItems.isEmpty()) {
                Snackbar.make(view, "You must add at least one menu item", Snackbar.LENGTH_SHORT).show();
                return;
            }

            if (!getActivity().getIntent().getBooleanExtra("editing", false)) {
                if (logoUri == null) {
                    Snackbar.make(view, "You must upload your restaurant ic_logo or image", Snackbar.LENGTH_SHORT).show();
                    return;
                }
            }
            initData(true);
        });
    }

    private void setViewInitials(@NonNull View view) {

        resName = restaurant.getName();
//        menuItems = restaurant.getMenuItems();
        if (!restaurant.getId().isEmpty()) {
            edtRestaurantName.setText(resName);
            editMenuViewModel.getMenuItems(restaurant.getId());
        }

//        Toast.makeText(getContext(), menuItems.size()+"", Toast.LENGTH_SHORT).show();
        currency = restaurant.getCurrency();

//        if (!menuItems.isEmpty()) {
//            for (FoodMenuItem menuItem : menuItems) {
//                editMenuViewModel.addItem(menuItem);
//            }
//        }


        if (!restaurant.getIconUrl().isEmpty()) {
            Glide.with(this)
                    .load(restaurant.getIconUrl())
                    .into(imgLogo);
        }

        ((TextView) view.findViewById(R.id.txtCurrency)).setText(currency + " (" + currency + ")");
//        ((ImageView) view.findViewById(R.id.imgCurrency)).setImageResource(flagDrawableResID);
    }

    private void initData(boolean done) {
        restaurant.setName(resName);
        restaurant.setCurrency(currency);
        if(logoUri != null) {
            restaurant.setIconUrl(String.valueOf(logoUri));
        }
//        restaurant.setMenuItems(menuItems);

        Log.i(TAG, "initData: items:"+menuItems.toString());

        pageChangedListener.onFormCompleted(restaurant, done, logoUri != null);
    }

    @Override
    public void onSaveClicked(FoodMenuItem item, int index) {

        if (index >= 0) {
            editMenuViewModel.modifyItem(item, index);
        }else{
            editMenuViewModel.addItem(item);
        }
    }

    @Override
    public void onItemRemoved(FoodMenuItem foodMenuItem, int position) {
        editMenuViewModel.removeItem(foodMenuItem);
    }

    @Override
    public void onEditItemClicked(FoodMenuItem foodMenuItem, int position) {

        AddItemFragment addItemFragment=new AddItemFragment(EditMenuFragment.this, foodMenuItem);
        Bundle arguments = new Bundle();
//        arguments.putString("name", foodMenuItem.getName());
//        arguments.putString("category", foodMenuItem.getCategory());
//        arguments.putString("description", foodMenuItem.getDescription());
//        arguments.putDouble("price", foodMenuItem.getPrice());
//        arguments.putLong("timestamp",foodMenuItem.getTimestamp());
        arguments.putInt("index", position);
        addItemFragment.setArguments(arguments);
        addItemFragment.show(getParentFragmentManager(), "Add Item");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PHOTO_INTENT && resultCode == RESULT_OK) {
            imgLogo.setImageURI(data.getData());
            logoUri = data.getData();
//            initData(false);
        }
    }


}