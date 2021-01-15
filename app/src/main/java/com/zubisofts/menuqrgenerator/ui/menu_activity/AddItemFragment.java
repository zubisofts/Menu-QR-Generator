package com.zubisofts.menuqrgenerator.ui.menu_activity;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.zubisofts.menuqrgenerator.R;
import com.zubisofts.menuqrgenerator.model.FoodMenuItem;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;


public class AddItemFragment extends DialogFragment {

    private static OnSaveClickListener onSaveClickListener;
    private static AddItemFragment fragment;
    private TextInputEditText edtName;
    private TextInputEditText edtPrice;
    private TextInputEditText edtDesc;
    private MaterialAutoCompleteTextView edtCategory;

    private static FoodMenuItem menuItem;

    public AddItemFragment(OnSaveClickListener listener, FoodMenuItem foodMenuItem) {
        onSaveClickListener = listener;
        menuItem = foodMenuItem;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Material_Light_Dialog_Alert);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edtCategory = view.findViewById(R.id.edtCategory);
        edtName = view.findViewById(R.id.edtItemName);
        edtPrice = view.findViewById(R.id.edtItemPrice);
        edtDesc = view.findViewById(R.id.edtItemDesc);

        if (menuItem != null) {
            loadData();
        }


        view.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        view.findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveItem(view);

            }
        });
    }

    private void loadData() {
        edtName.setText(menuItem.getName());
        edtCategory.setText(menuItem.getCategory());
        edtDesc.setText(menuItem.getDescription());
        edtPrice.setText(String.valueOf(menuItem.getPrice()));
    }

    private void saveItem(View view) {

        if (TextUtils.isEmpty(edtCategory.getText())) {
            ((TextInputLayout) view.findViewById(R.id.inputItemName)).setError("Dish Category must not be empty");
            return;
        }

        if (TextUtils.isEmpty(edtName.getText())) {
            ((TextInputLayout) view.findViewById(R.id.inputItemName)).setError("Dish name must not be empty");
            return;
        }

        if (TextUtils.isEmpty(edtPrice.getText())) {
            ((TextInputLayout) view.findViewById(R.id.inputItemPrice)).setError("Dish price must not be empty");
            return;
        }

        if (menuItem != null){
            onSaveClickListener.onSaveClicked(new FoodMenuItem(
                    menuItem.getId(),
                    edtName.getText().toString(),
                    edtDesc.getText().toString(),
                    Double.parseDouble(edtPrice.getText().toString()),
                    edtCategory.getText().toString(),
                    menuItem.getTimestamp()
            ),getArguments().getInt("index",-1));
        }else {
            onSaveClickListener.onSaveClicked(new FoodMenuItem(
                    "",
                    edtName.getText().toString(),
                    edtDesc.getText().toString(),
                    Double.parseDouble(edtPrice.getText().toString()),
                    edtCategory.getText().toString(),
                    new Date().getTime()
            ), -1);
        }


        dismiss();

    }

    interface OnSaveClickListener {
        public void onSaveClicked(FoodMenuItem item, int index);
    }
}