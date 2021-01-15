package com.zubisofts.menuqrgenerator.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.zubisofts.menuqrgenerator.model.Response;
import com.zubisofts.menuqrgenerator.model.User;

import java.util.List;

import static com.zubisofts.menuqrgenerator.repository.DataRepository.saveUser;

public class AuthRepository {

    private static final String TAG = "AuthRepository";

    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public static void createUser(final String email, String password, final String displayName, final MutableLiveData<Response> userLiveData) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        User user = new User(authResult.getUser().getUid(), email, displayName);
                        saveUser(user, userLiveData);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    public static void loginUser(final String email, String password, final MutableLiveData<Response> userLiveData) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        User user = new User(authResult.getUser().getUid(), email, "");
                        userLiveData.postValue(
                                new Response(
                                        false,
                                        user
                                )
                        );
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        userLiveData.postValue(
                                new Response(
                                        true,
                                        e.getMessage()
                                )
                        );
                    }
                });

    }

    public static void loginUserWithGoogle(GoogleSignInAccount result, MutableLiveData<Response> userResponse) {

        mAuth.fetchSignInMethodsForEmail(result.getEmail())
                .addOnSuccessListener(signInMethodQueryResult -> {
                    List<String> signInMethods = signInMethodQueryResult.getSignInMethods();

                    if (signInMethods.contains("password")) {
                        userResponse.postValue(new Response(
                                true,
                                "User already exists with this account"
                        ));

                    } else {
                        loginWithCredentials(result, userResponse);
                    }

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                userResponse.postValue(new Response(
                        true,
                        e.getMessage()
                ));
            }
        });

    }

    private static void loginWithCredentials(GoogleSignInAccount result, MutableLiveData<Response> userResponse) {

        AuthCredential credential = GoogleAuthProvider.getCredential(result.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    User user = new User(
                            firebaseUser.getUid(),
                            firebaseUser.getEmail(),
                            firebaseUser.getDisplayName()
                    );

                    userResponse.postValue(new Response(
                            false,
                            user
                    ));

                    DataRepository.checkAndSaveUserToDB(user,userResponse);

                }).addOnFailureListener(e -> {
            userResponse.postValue(new Response(
                    true,
                    e.getMessage()
            ));
        });

    }

    private static void saveUserToDB(User user, MutableLiveData<Response> userResponse) {



    }

}
