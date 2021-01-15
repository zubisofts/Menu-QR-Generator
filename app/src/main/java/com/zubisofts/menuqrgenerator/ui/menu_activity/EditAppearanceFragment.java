package com.zubisofts.menuqrgenerator.ui.menu_activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;
import com.tiper.MaterialSpinner;
import com.zubisofts.menuqrgenerator.R;
import com.zubisofts.menuqrgenerator.model.Restaurant;

import java.util.Arrays;
import java.util.HashMap;


public class EditAppearanceFragment extends Fragment {

    private EditMenuViewModel editMenuViewModel;
    private int headerColor, textColor, bgColor;
    private String headerFont, textFont, textAlignment;
    private HashMap<String, Object> data = new HashMap<>();

    private static PageChangedListener listener;
    private static Restaurant restaurant;

    private String[] fonts = {"Abril Fatface", "Anton", "Arvo", "Cabin", "Cormorant", "Dosis", "Lato", "Lobster", "Monda", "Montserrat", "Noto Sans", "Nunito", "Old Standard TT", "Open Sans", "Oswald", "Pacifico", "Poiret One", "Pontano Sans", "Poppins", "Prata", "PTSerif", "Redressed"};
    private String[] themes = {"Light", "Dark", "Holo Light", "Navy", "Christmas", "Restaurant"};
    private MaterialSpinner txtHeaderFont, txtTextFont, txtTextAlignment, spnTheme;
    private MaterialCardView btnHeaderColor, btnBgColor, btnTextColor;

    public static EditAppearanceFragment newInstance(PageChangedListener pageChangedListener, Restaurant res) {
        listener = pageChangedListener;
        restaurant=res;
        return new EditAppearanceFragment();
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_appearance, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        headerColor = Color.BLACK;
        textColor = Color.BLACK;
        bgColor = Color.BLACK;
        headerFont = fonts[6];
        textFont = fonts[6];
        textAlignment = "Left";

//        TextView txtTitle = view.findViewById(R.id.title);
//        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Monotoon KK.ttf");
//        txtTitle.setTypeface(typeface);

        ArrayAdapter<String> fontsAdapter = new ArrayAdapter<String>
                (getContext(), android.R.layout.select_dialog_item, fonts);

        final ArrayAdapter<String> textAlignAdapter = new ArrayAdapter<String>
                (getContext(), android.R.layout.select_dialog_item, Arrays.asList("Left", "Center", "Right"));

        final ArrayAdapter<String> themeAdapter = new ArrayAdapter<String>
                (getContext(), android.R.layout.select_dialog_item, themes);

        txtHeaderFont = view.findViewById(R.id.edtHeadingFont);
        txtTextFont = view.findViewById(R.id.edtTextFont);
        txtTextAlignment = view.findViewById(R.id.edtTextAlignment);
        spnTheme=view.findViewById(R.id.edtTheme);
        spnTheme.setAdapter(themeAdapter);

        btnHeaderColor=view.findViewById(R.id.btnHeaderColor);
        btnBgColor=view.findViewById(R.id.btnBgColor);
        btnTextColor=view.findViewById(R.id.btnTextColor);

        txtHeaderFont.setAdapter(fontsAdapter);
        txtTextFont.setAdapter(fontsAdapter);
        txtTextAlignment.setAdapter(textAlignAdapter);

//        txtHeaderFont.setKeyListener(null);
//        txtTextFont.setKeyListener(null);
//        txtTextAlignment.setKeyListener(null);

        setViewInitials();

        txtHeaderFont.setOnItemClickListener((adapterView, view1, i, l) -> {
            headerFont = ((TextView) view1).getText().toString();
            Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/" + headerFont + ".ttf");
            txtHeaderFont.setTypeface(typeface);
            setupPreferenceItems(false);
        });

        spnTheme.setOnItemClickListener((adapterView, view1, i, l) -> {
            handleThemeSelection(adapterView,view1,i,l);
        });

        txtTextFont.setOnItemClickListener((adapterView, view1, i, l) -> {
            String text = ((TextView) view1).getText().toString();
            textFont = text;
            Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/" + textFont + ".ttf");
            txtTextFont.setTypeface(typeface);
            setupPreferenceItems(false);
        });

        txtTextAlignment.setOnItemClickListener((adapterView, view1, i, l) -> {
            String text = ((TextView) view1).getText().toString();
                textAlignment = text;
                setupPreferenceItems(false);
        });

        btnHeaderColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                final ColorPicker cp = new ColorPicker(getActivity(), Color.red(headerColor), Color.green(headerColor), Color.blue(headerColor));
                /* Show color picker dialog */
                cp.show();

                cp.enableAutoClose(); // Enable auto-dismiss for the dialog

                /* Set a new Listener called when user click "select" */
                cp.setCallback(new ColorPickerCallback() {
                    @Override
                    public void onColorChosen(@ColorInt int color) {
                        headerColor = color;
                        ((MaterialCardView) view.findViewById(R.id.btnHeaderColor)).setCardBackgroundColor(color);
                        setupPreferenceItems(false);
                    }
                });
            }
        });

        btnTextColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final ColorPicker cp = new ColorPicker(getActivity(), Color.red(textColor), Color.green(textColor), Color.blue(textColor));
                /* Show color picker dialog */
                cp.show();

                cp.enableAutoClose(); // Enable auto-dismiss for the dialog

                /* Set a new Listener called when user click "select" */
                cp.setCallback(new ColorPickerCallback() {
                    @Override
                    public void onColorChosen(@ColorInt int color) {
                        textColor = color;
//                        Toast.makeText(getContext(), ""+color, Toast.LENGTH_SHORT).show();
                        ((MaterialCardView) view.findViewById(R.id.btnTextColor)).setCardBackgroundColor(color);
//                        Toast.makeText(getContext(), ""+color, Toast.LENGTH_SHORT).show();
                        setupPreferenceItems(false);
                    }
                });
            }

        });

        btnBgColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final ColorPicker cp = new ColorPicker(getActivity(), Color.red(bgColor), Color.green(bgColor), Color.blue(bgColor));
                /* Show color picker dialog */
                cp.show();

                cp.enableAutoClose(); // Enable auto-dismiss for the dialog

                /* Set a new Listener called when user click "select" */
                cp.setCallback(new ColorPickerCallback() {
                    @Override
                    public void onColorChosen(@ColorInt int color) {
                        bgColor = color;
                        ((MaterialCardView) view.findViewById(R.id.btnBgColor)).setCardBackgroundColor(color);
                        setupPreferenceItems(false);
                    }
                });
            }
        });

        view.findViewById(R.id.btnContinue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupPreferenceItems(true);
            }
        });

    }

    private void handleThemeSelection(MaterialSpinner adapterView, View view1, int i, long l) {

        switch (i){
            case 0:
                headerColor=-16777216;
                textColor=-16777216;
                bgColor=-1;
                break;
            case 1:
                headerColor=-1;
                textColor=-1;
                bgColor=-16777216;
                break;
            case 2:
                headerColor=-16777216;
                textColor=-16777216;
                bgColor=-921103;
                break;
            case 3:
                headerColor=-1;
                textColor=-1;
                bgColor=-14789745;
                break;
            case 4:
                headerColor=-1;
                textColor=-1;
                bgColor=-54709;
                break;
            case 5:
                headerColor=-29310;
                textColor=-1;
                bgColor=-16768982;
                break;
            default:
                break;
        }

        btnHeaderColor.setCardBackgroundColor(headerColor);
        btnTextColor.setCardBackgroundColor(textColor);
        btnBgColor.setCardBackgroundColor(bgColor);
        setupPreferenceItems(false);

//        Toast.makeText(getContext(), ""+i, Toast.LENGTH_SHORT).show();

    }

    private void setViewInitials() {

        HashMap pref=restaurant.getPreferences();

        textFont=pref.get("textFont").toString();
        headerFont=pref.get("headerFont").toString();
        textAlignment=pref.get("textAlignment").toString();
        textColor=Integer.parseInt(pref.get("textColor").toString());
        headerColor=Integer.parseInt(pref.get("headerColor").toString());
        bgColor=Integer.parseInt(pref.get("bgColor").toString());

        Typeface headerTypeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/" + headerFont + ".ttf");
        Typeface textTypeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/" + textFont + ".ttf");

//        txtHeaderFont.setText(headerFont);
        txtHeaderFont.setSelection(Arrays.asList(fonts).indexOf(headerFont));
        txtHeaderFont.setTypeface(headerTypeface);

//        txtTextFont.setText(textFont);
        txtTextFont.setSelection(Arrays.asList(fonts).indexOf(textFont));
        txtTextFont.setTypeface(textTypeface);

//        txtTextAlignment.setText(textAlignment);
        txtTextAlignment.setSelection(Arrays.asList("Left", "Center", "Right").indexOf(textAlignment));

        btnHeaderColor.setCardBackgroundColor(headerColor);
        btnTextColor.setCardBackgroundColor(textColor);
        btnBgColor.setCardBackgroundColor(bgColor);

    }

    private void setupPreferenceItems(boolean next) {

        restaurant.getPreferences().put("headerFont", headerFont);
        restaurant.getPreferences().put("textFont", textFont);
        restaurant.getPreferences().put("headerColor", headerColor);
        restaurant.getPreferences().put("textAlignment", textAlignment);
        restaurant.getPreferences().put("textColor", textColor);
        restaurant.getPreferences().put("bgColor", bgColor);

//        data.put("headerFont", headerFont);
//        data.put("textFont", textFont);
//        data.put("headerColor", headerColor);
//        data.put("textAlignment", textAlignment);
//        data.put("textColor", textColor);
//        data.put("bgColor", bgColor);

        listener.onNextPageClicked(next);
    }

}