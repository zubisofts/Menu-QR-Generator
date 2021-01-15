package com.zubisofts.menuqrgenerator.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.zubisofts.menuqrgenerator.model.Response;
import com.zubisofts.menuqrgenerator.repository.AuthRepository;

public class MainViewModel extends ViewModel {

    private MutableLiveData<Response> userResponse=new MutableLiveData<>();

    public void createUser(String email, String password, String displayName){
        AuthRepository.createUser(email,password,displayName,userResponse);
    }

    public void loginUser(String email, String password){
        AuthRepository.loginUser(email,password,userResponse);
    }

    public MutableLiveData<Response> getUserResponse(){
        return userResponse;
    }

    public void loginUserWithGoogle(GoogleSignInAccount result) {

        AuthRepository.loginUserWithGoogle(result,userResponse);

    }
}
