package com.nimblefix.empapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nimblefix.ControlMessages.AuthenticationMessage;

public class OTP extends AppCompatActivity{

    EditText[] edittexts;
    Button next;

    String otp ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        ((ThisApplication)getApplication()).setCurrentContext(this);

        edittexts = new EditText[6];

        edittexts[0]=findViewById(R.id.T1);
        edittexts[1]=findViewById(R.id.T2);
        edittexts[2]=findViewById(R.id.T3);
        edittexts[3]=findViewById(R.id.T4);
        edittexts[4]=findViewById(R.id.T5);
        edittexts[5]=findViewById(R.id.T6);

        for(int i = 0; i< edittexts.length;i++) {
            edittexts[i].setOnKeyListener(new KeyEnteredArray(edittexts,i,OTP.this));
            edittexts[i].setCursorVisible(false);
        }
        edittexts[0].requestFocus();
    }

    public void setOTPvar() {
        otp="";
        for(int i = 0; i<6;i++)
            otp+=edittexts[i].getText().toString();
        if(otp.length()==6){
            checkOTP(otp);
        }
    }

    private void checkOTP(final String otp) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AuthenticationMessage authmsg = new AuthenticationMessage(AuthenticationMessage.Staff, AuthenticationMessage.Response, ((ThisApplication)getApplication()).getEmployeeID(),otp);
                authmsg.setMESSAGEBODY(((ThisApplication)getApplication()).getOrganisationID());

                ((ThisApplication)getApplication()).mobileClient.writeObject(authmsg);

                final Object o = ((ThisApplication)getApplication()).mobileClient.readNext();
                if(o instanceof AuthenticationMessage){
                    if (((AuthenticationMessage)o).getMESSAGEBODY().equals("INVALID"))
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(OTP.this,"Invalid OTP",Toast.LENGTH_SHORT).show();
                            }
                        });

                    else if(((AuthenticationMessage)o).getMESSAGEBODY().contains("VALID")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                savetoLocalPref(((ThisApplication)getApplication()).getOrganisationID(),((ThisApplication)getApplication()).getEmployeeID(),((AuthenticationMessage) o).getMESSAGEBODY().substring(5,55),((AuthenticationMessage) o).getMESSAGEBODY().substring(55));
                                ((ThisApplication)getApplication()).setEmployeeEmail(((AuthenticationMessage) o).getMESSAGEBODY().substring(55));
                                Intent i = new Intent(OTP.this,pendTask.class);
                                startActivity(i);
                            }
                        });
                    }
                }
            }
        }).start();
    }

    private void savetoLocalPref(String orgID, String userID, String token, String email) {

        SharedPreferences shrdPref = getSharedPreferences("NimbleFixEmpAppData",MODE_PRIVATE);
        SharedPreferences.Editor shrdPrefsEditor = shrdPref.edit();

        shrdPrefsEditor.putString("ID",userID);
        shrdPrefsEditor.putString("ORG",orgID);
        shrdPrefsEditor.putString("EMAIL",email);
        shrdPrefsEditor.putString("TOKEN",token);

        shrdPrefsEditor.apply();
    }
}

class KeyEnteredArray implements View.OnKeyListener {

    EditText[] editTexts=null;
    int index;

    OTP currInst;

    KeyEnteredArray(EditText[] editTexts,int i,OTP currInst){
        this.currInst=currInst;
        this.editTexts = editTexts;
        this.index = i;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(keyCode==67 &&event.getAction()==1){
            if(editTexts[index].getText().length()==1) {
                editTexts[index].setText("");
            }
            else{
                if(index!=0){
                    editTexts[index-1].setText("");
                    editTexts[index-1].requestFocus();
                }
            }
        }
        else if(keyCode>=7&&keyCode<=16 && event.getAction()==1) {
            if(index==5&&editTexts[5].getText().length()==1){currInst.setOTPvar(); return true;}
            editTexts[index].setText(String.valueOf(keyCode-7));
            if(index==5){currInst.setOTPvar(); return true;}
            editTexts[index + 1].requestFocus();
        }

        return true;
    }
}