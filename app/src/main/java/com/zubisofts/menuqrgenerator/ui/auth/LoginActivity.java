package com.zubisofts.menuqrgenerator.ui.auth;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.PatternsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.zubisofts.menuqrgenerator.R;
import com.zubisofts.menuqrgenerator.model.Response;
import com.zubisofts.menuqrgenerator.model.User;
import com.zubisofts.menuqrgenerator.ui.main.MainActivity;
import com.zubisofts.menuqrgenerator.viewmodel.MainViewModel;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private static final int RC_SIGN_IN = 2021;
    private TextInputEditText edtEmail;
    private TextInputEditText edtPassword;
    private ProgressDialog progress;

    private MainViewModel mainViewModel;
    private GoogleSignInClient signInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mainViewModel = new ViewModelProvider.NewInstanceFactory().create(MainViewModel.class);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);

        progress = new ProgressDialog(this);

        mainViewModel.getUserResponse().observe(this, new Observer<Response>() {
            @Override
            public void onChanged(Response response) {
                if (!response.isError()) {
                    User user = (User) response.getData();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, response.getData().toString(), Toast.LENGTH_SHORT).show();
                }
                progress.hide();
            }
        });

        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        findViewById(R.id.btnGoogle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });

        findViewById(R.id.txtSignup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });
    }

    private void login() {
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();

        if (!PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()) {
            ((TextInputLayout) findViewById(R.id.inputEmail)).setError("Invalid email format");
            return;
        }

        if (password.length() < 6) {
            ((TextInputLayout) findViewById(R.id.inputPassword)).setError("Password must be at least 6 characters");
            return;
        }

        progress.setMessage("Signing in...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        mainViewModel.loginUser(email, password);
    }

    private void signInWithGoogle() {
        GoogleSignInOptions googleSignOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestProfile()
                .requestEmail()
                .build();
        signInClient = GoogleSignIn.getClient(this, googleSignOptions);

        Intent signInIntent = signInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        if (task.isSuccessful()) {
            GoogleSignInAccount result = task.getResult();
            mainViewModel.loginUserWithGoogle(result);
            if (signInClient != null){
                signInClient.signOut();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
}