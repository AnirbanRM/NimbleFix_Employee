package com.nimblefix.empapp;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.nimblefix.ControlMessages.AuthenticationMessage;
import com.nimblefix.MobileClientEmp;

public class ThisApplication extends Application {

    MobileClientEmp mobileClient;
    private Context currentContext=null;

    private String organisationID=null;
    private String organisationName=null;
    private String employeeID = null;
    private String employeeName = null;
    private String employeeEmail = null;

    public void setOrganisationID(String organisationID) {
        this.organisationID = organisationID;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getOrganisationID() {
        return organisationID;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public String getEmployeeEmail(){return employeeEmail; }

    public void setEmployeeEmail(String email){this.employeeEmail = email; }

    public void connectToServer(final String orgID, final String empID){
        this.organisationID = orgID;
        this.employeeID = empID;

        Thread connectThd = new Thread(new Runnable() {
            @Override
            public void run() {
                mobileClient = new MobileClientEmp(currentContext);
                mobileClient.connect(orgID,empID);

                Object o = mobileClient.readNext();

                if(o instanceof AuthenticationMessage){
                    Log.e("LOL",((AuthenticationMessage) o).getMESSAGEBODY());
                    if( ((AuthenticationMessage)o).getMESSAGEBODY().contains("INVALID")) {
                        ((SignUp)currentContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(currentContext, "Invalid details !", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }
                    if(currentContext instanceof  SignUp)
                        ((SignUp)currentContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((SignUp)currentContext).showOTPScreen();
                            }
                        });
                }
            }
        });
        connectThd.start();
    }

    public void connectToServer(final String orgID, final String empID,final String token,final String email){
        this.organisationID = orgID;
        this.employeeID = empID;
        this.employeeEmail = email;

        Thread connectThd = new Thread(new Runnable() {
            @Override
            public void run() {
                mobileClient = new MobileClientEmp(currentContext);
                mobileClient.connect(orgID,empID,token);

                Object o = mobileClient.readNext();

                if(o instanceof AuthenticationMessage){
                    if(currentContext instanceof  SignUp)
                        ((SignUp)currentContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((SignUp)currentContext).showOTPScreen();
                            }
                        });

                    if(currentContext instanceof SplashScreen){
                        if(((AuthenticationMessage)o).getMESSAGEBODY().equals("VALID"))
                            ((SplashScreen) currentContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((SplashScreen)currentContext).goToPTask();
                                }
                            });
                        else if(((AuthenticationMessage)o).getMESSAGEBODY().equals("INVALID")){
                            ((SplashScreen) currentContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((SplashScreen)currentContext).goToSignUp();
                                }
                            });
                        }
                    }
                }
            }
        });
        connectThd.start();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public Context getCurrentContext() {
        return currentContext;
    }

    public void setCurrentContext(Context currentContext) {
        this.currentContext = currentContext;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
