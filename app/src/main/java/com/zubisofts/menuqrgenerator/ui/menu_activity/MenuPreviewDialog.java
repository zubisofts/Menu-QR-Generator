package com.zubisofts.menuqrgenerator.ui.menu_activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.zubisofts.menuqrgenerator.R;
import com.zubisofts.menuqrgenerator.model.FoodMenuItem;
import com.zubisofts.menuqrgenerator.model.Response;
import com.zubisofts.menuqrgenerator.model.Restaurant;
import com.zubisofts.menuqrgenerator.util.BarcodeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class MenuPreviewDialog extends BottomSheetDialogFragment {

    private static final String TAG = "MenuPreviewDialog";
    private static int previewMode;

    private static Restaurant restaurant;
    private EditMenuViewModel editMenuViewModel;
    private MenuPreviewListAdapterSection menuPreviewListAdapterSection;

    public static MenuPreviewDialog newInstance(Restaurant res, int mode) {
        previewMode = mode;
        restaurant = res;
        final MenuPreviewDialog fragment = new MenuPreviewDialog();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        editMenuViewModel = new ViewModelProvider.NewInstanceFactory().create(EditMenuViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_preview_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        TextView txtName = view.findViewById(R.id.txtResName);
        ImageView imgLogo = view.findViewById(R.id.imgLogo);
        RecyclerView menuList = view.findViewById(R.id.previewList);

        HashMap settings = restaurant.getPreferences();
        view.findViewById(R.id.btnQrCodePreview).setEnabled(false);
        Glide.with(this)
                .load(restaurant.getIconUrl())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        view.findViewById(R.id.btnQrCodePreview).setEnabled(false);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        BitmapDrawable bitmapDrawable=(BitmapDrawable)resource;
                        imgLogo.setImageBitmap(bitmapDrawable.getBitmap());
                        view.findViewById(R.id.btnQrCodePreview).setEnabled(true);
                        return true;
                    }
                }).into(imgLogo);


        txtName.setText(restaurant.getName());
        txtName.setTextColor(Integer.parseInt(settings.get("headerColor").toString()));

        view.setBackgroundColor(Integer.parseInt(settings.get("bgColor").toString()));

//        Toast.makeText(getActivity(), settings.get("headerFont").toString(), Toast.LENGTH_SHORT).show();
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/" + settings.get("headerFont") + ".ttf");
        txtName.setTypeface(typeface);

        if (previewMode == PreviewMode.MODE_VIEW) {
            String restaurantId = restaurant.getId();
            loadMenu(restaurantId);
        } else {
            if (restaurant.getMenuItems() != null)
                setupFoodMenuList(restaurant.getMenuItems(), menuList);
            if (!restaurant.getIconUrl().equals(""))
                Glide.with(this)
                        .load(Uri.parse(restaurant.getIconUrl()))
                        .into(imgLogo);
        }

        editMenuViewModel.getMenuResponse().observe(getViewLifecycleOwner(), new Observer<Response>() {
            @Override
            public void onChanged(Response response) {
                if (!response.isError()) {
                    ArrayList<FoodMenuItem> items = (ArrayList<FoodMenuItem>) response.getData();
                    if (items != null && !items.isEmpty())
                        setupFoodMenuList(items, menuList);
                }
            }
        });

        view.findViewById(R.id.btnQrCodePreview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapDrawable drawable = (BitmapDrawable) imgLogo.getDrawable();
                if (drawable.getBitmap() != null)
                    previewQrCode(drawable.getBitmap());
//                dismiss();
            }
        });

    }

    private void previewQrCode(Bitmap logo) {

        Bitmap barCode = BarcodeUtils.createQRImage("https://qrmenu-preview.netlify.app/preview/"+restaurant.getOwnerId() + "_" + restaurant.getId(), 700, 700, logo);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_qrcode_preview, null, false);
        ImageView imageView = view.findViewById(R.id.imgQrCode);
        imageView.setImageBitmap(barCode);

        view.findViewById(R.id.btnShareMenu).setOnClickListener(v -> {
//            Toast.makeText(getActivity(), "Qr Code has been saved in: QRMenu/qr/" + ownerId, Toast.LENGTH_LONG).show();

            shareQRCode(barCode);

        });

        new AlertDialog.Builder(getContext())
                .setTitle("QRCode Menu Preview")
                .setView(view)
                .create().show();

    }

    private void shareQRCode(Bitmap barCode) {

        Dexter.withContext(getActivity())
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        BarcodeUtils.saveImage(barCode, restaurant.getOwnerId() + "_" + restaurant.getId(), getActivity());

                        String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), barCode, "Share Menu QRCode", null);
                        Uri uri = Uri.parse(path);
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        shareIntent.putExtra(Intent.EXTRA_TEXT, "Have you seen our food menu recently? Scan the above QRCode to see our latest Food Menu instantly.");
                        shareIntent.setType("image/*");
                        startActivity(Intent.createChooser(shareIntent, "Share this menu"));
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();

    }

    private void setupFoodMenuList(ArrayList<FoodMenuItem> items, RecyclerView menuList) {

        ArrayList<ArrayList<FoodMenuItem>> sections = new ArrayList<>();

        ArrayList<String> tt=new ArrayList<>();

        for (FoodMenuItem foodMenuItem : items) {
            if (!tt.contains(foodMenuItem.getCategory())){
                tt.add(foodMenuItem.getCategory());
            }
        }

        for (String cat : tt) {
            Log.i(TAG, "onChanged: category:" + cat);
            ArrayList<FoodMenuItem> subSection = new ArrayList<>();
            for (FoodMenuItem fmi : items) {
                if (fmi.getCategory().equals(cat)) {
                    subSection.add(fmi);
                }
            }
            Collections.sort(subSection, new Comparator<FoodMenuItem>() {
                @Override
                public int compare(FoodMenuItem foodMenuItem, FoodMenuItem t1) {
                    if (foodMenuItem.getTimestamp() > t1.getTimestamp()){
                        return 0;
                    }
                    return -1;
                }
            });
            if (!subSection.isEmpty()) {
                sections.add(subSection);
            }
        }

        Collections.sort(sections, (t1, t2) -> {

            if (t1.get(0).getTimestamp() > t2.get(0).getTimestamp()){
                return 0;
            }
            return -1;
        });

        SectionedRecyclerViewAdapter adapter = new SectionedRecyclerViewAdapter();

        for (ArrayList<FoodMenuItem> foodMenuItems : sections) {
            menuPreviewListAdapterSection = new MenuPreviewListAdapterSection(foodMenuItems, restaurant);
            adapter.addSection(menuPreviewListAdapterSection);
        }
        menuList.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        menuList.setAdapter(adapter);
    }

    private void loadMenu(String restaurantId) {
        editMenuViewModel.getRestaurantMenu(restaurantId);
    }

    public static class PreviewMode {
        public static int MODE_EDIT = 2020;
        public static int MODE_VIEW = 2021;
    }

}