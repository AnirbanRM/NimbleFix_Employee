package com.nimblefix;

import android.content.Context;

import com.nimblefix.ControlMessages.AuthenticationMessage;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MobileClientEmp {

    final private static String SERVER_IP="10.0.0.2";
    final private static int SERVER_PORT = 2180;

    Context currentContext=null;

    Socket clientSocket;
    ObjectInputStream reader;
    ObjectOutputStream writer;

    public MobileClientEmp(Context context){
        this.currentContext = context;
    }

    public void setCurrentContext(Context currentContext) {
        this.currentContext = currentContext;
    }

    public Context getCurrentContext() {
        return currentContext;
    }

    public void connect(String orgID,String empID){
        try {
            clientSocket = new Socket(SERVER_IP,SERVER_PORT);
            reader = new ObjectInputStream(clientSocket.getInputStream());
            writer = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }

        startAuthenticationProcedure(orgID,empID,null);
    }

    public void connect(String orgID,String empID, String token){
        try {
            clientSocket = new Socket(SERVER_IP,SERVER_PORT);
            reader = new ObjectInputStream(clientSocket.getInputStream());
            writer = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }

        startAuthenticationProcedure(orgID,empID,token);
    }


    private void startAuthenticationProcedure(String orgID, String empID, String token) {
        Object obj = readNext();
        if(obj instanceof AuthenticationMessage){
            AuthenticationMessage authmsg = (AuthenticationMessage) obj;
            if(authmsg.getSource()== AuthenticationMessage.Server&&authmsg.getMessageType()== AuthenticationMessage.Challenge){
                AuthenticationMessage authmsg2 = new AuthenticationMessage(AuthenticationMessage.Staff , AuthenticationMessage.Response,empID,token);
                authmsg2.setMESSAGEBODY(orgID);
                writeObject(authmsg2);
            }
        }
    }

    public void writeObject(Object object){
        try {
            writer.writeUnshared(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object readNext(){
        Object o=null;
        try{
            o = reader.readUnshared();
        }catch (Exception e){}
        return o;
    }
}
