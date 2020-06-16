package com.nimblefix.empapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ((ThisApplication)getApplication()).setCurrentContext(this);

        SharedPreferences shrdPref = getSharedPreferences("NimbleFixEmpAppData",MODE_PRIVATE);
        String ID = shrdPref.getString("ID",null);
        if(ID==null)
            startActivity(new Intent(this,SignUp.class));
        else{
            checkToken(shrdPref.getString("ORG",null),shrdPref.getString("ID",null),shrdPref.getString("TOKEN",null),shrdPref.getString("EMAIL",null));
        }

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(i);
            }
        },2000);*/
    }

    private void checkToken(String org, String id, String token, String email) {
        ((ThisApplication)getApplication()).connectToServer(org,id,token,email);
    }

    public void goToPTask(){
        startActivity(new Intent(this, pendTask.class));
    }

    public void goToSignUp(){
        startActivity(new Intent(this,SignUp.class));
    }
}
