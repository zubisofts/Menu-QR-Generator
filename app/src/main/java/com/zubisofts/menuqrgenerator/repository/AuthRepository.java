package com.zubisofts.menuqrgenerator.repository;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.zubisofts.menuqrgenerator.model.Response;
import com.zubisofts.menuqrgenerator.model.User;
import com.zubisofts.menuqrgenerator.ui.auth.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

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
        AuthCredential credential = GoogleAuthProvider.getCredential(result.getIdToken(), null);

        mAuth.fetchSignInMethodsForEmail(result.getEmail())
                .addOnSuccessListener(signInMethodQueryResult -> {
                    List<String> signInMethods = signInMethodQueryResult.getSignInMethods();

                    if (signInMethods.contains("password")) {
                        userResponse.postValue(new Response(
                                true,
                                "User already exists with this account"
                        ));

                    } else {
                        loginWithCredentials(credential, userResponse);
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

    private static void loginWithCredentials(AuthCredential credential, MutableLiveData<Response> userResponse) {

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


    public static void loginUserWithFacebook(AccessToken accessToken, MutableLiveData<Response> userResponse) {


        GraphRequest request = GraphRequest.newMeRequest(
                accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("TAG", object.toString());
                        try {
//                            String first_name = object.getString("first_name");
//                            String last_name = object.getString("last_name");
                            String email = object.getString("email");
//                            String id = object.getString("id");
//                            String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";

                            final AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
                            mAuth.fetchSignInMethodsForEmail(email)
                                    .addOnSuccessListener(signInMethodQueryResult -> {
                                        List<String> signInMethods = signInMethodQueryResult.getSignInMethods();

                                        if (signInMethods.contains("password")) {
                                            userResponse.postValue(new Response(
                                                    true,
                                                    "User already exists with this account"
                                            ));

                                        } else {
                                            loginWithCredentials(credential, userResponse);
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


                        } catch (JSONException e) {
                            e.printStackTrace();
                            userResponse.postValue(new Response(
                                    true,
                                    e.getMessage()
                            ));
                        }

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();

    }

}

