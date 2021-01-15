package com.zubisofts.menuqrgenerator.ui.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.PatternsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.zubisofts.menuqrgenerator.R;
import com.zubisofts.menuqrgenerator.model.Response;
import com.zubisofts.menuqrgenerator.model.User;
import com.zubisofts.menuqrgenerator.ui.main.MainActivity;
import com.zubisofts.menuqrgenerator.viewmodel.MainViewModel;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText edtEmail;
    private TextInputEditText edtPassword;
    private TextInputEditText edtDisplayName;
    private ProgressDialog progress;

    private MainViewModel mainViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mainViewModel= new ViewModelProvider.NewInstanceFactory().create(MainViewModel.class);

        edtEmail=findViewById(R.id.edtEmail);
        edtPassword=findViewById(R.id.edtPassword);
        edtDisplayName=findViewById(R.id.edtDisplayName);

        progress = new ProgressDialog(this);

        mainViewModel.getUserResponse().observe(this, new Observer<Response>() {
            @Override
            public void onChanged(Response response) {
                if (!response.isError()){
                    User user= (User) response.getData();
                    Intent intent=new Intent(RegisterActivity.this, MainActivity.class);
                    intent.putExtra("user",user);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(RegisterActivity.this, response.getData().toString(), Toast.LENGTH_SHORT).show();
                }
                progress.hide();
            }
        });

        findViewById(R.id.btnRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();
            }
        });

        findViewById(R.id.txtLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

    }

    private void createUser(){
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();
        String displayName = edtDisplayName.getText().toString();

        if (!PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()) {
            ((TextInputLayout)findViewById(R.id.inputEmail)).setError("Invalid email format");
            return;
        }

        if (password.length() < 6) {
            ((TextInputLayout)findViewById(R.id.inputPassword)).setError("Password must be at least 6 characters");
            return;
        }

        if (displayName.length() < 3) {
            ((TextInputLayout)findViewById(R.id.inputDisplayName)).setError("Display name must be at least 3 characters");
            return;
        }

        progress.setMessage("Creating account...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        mainViewModel.createUser(email, password, displayName);
    }
}