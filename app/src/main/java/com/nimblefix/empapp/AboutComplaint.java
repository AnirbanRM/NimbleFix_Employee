package com.nimblefix.empapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nimblefix.ControlMessages.AboutInventoryMessage;
import com.nimblefix.ControlMessages.ComplaintMessage;
import com.nimblefix.MobileClientEmp;
import com.nimblefix.core.Complaint;

public class AboutComplaint extends AppCompatActivity {

    Complaint complaint;
    TextView comp_no,dt,title,id,desc;
    ImageView map;
    String owner;
    Button done;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_complaint);

        comp_no = findViewById(R.id.comp_no);
        dt = findViewById(R.id.date_time);
        title = findViewById(R.id.title);
        id = findViewById(R.id.id);
        desc = findViewById(R.id.desc);
        map = findViewById(R.id.map);
        done = findViewById(R.id.done);

        Intent i = getIntent();
        complaint = (Complaint) i.getSerializableExtra("Complaint");
        owner = i.getStringExtra("Owner");

        done.setOnClickListener(done_clicked);

        new Thread(new Runnable() {
            @Override
            public void run() {
                getInventory(complaint.getOrganizationID(),complaint.getInventoryID());
                getMap(complaint);
            }
        }).start();

    }

    View.OnClickListener done_clicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ComplaintMessage msg = new ComplaintMessage(complaint);
                    msg.setBody("DONE"+owner);
                    ((ThisApplication)getApplication()).mobileClient.writeObject(msg);
                    ((ThisApplication)getApplication()).mobileClient.readNext();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onBackPressed();
                        }
                    });
                }
            }).start();
        }
    };

    private void getMap(Complaint complaint) {
        try{
            ComplaintMessage complaintMessage = new ComplaintMessage(complaint);
            complaintMessage.setBody("IMAGE"+owner);
            ((ThisApplication)getApplication()).mobileClient.writeObject(complaintMessage);
        }catch (Exception e){}

        try{
            ComplaintMessage complaintMessage = (ComplaintMessage) ((ThisApplication)getApplication()).mobileClient.readNext();
            byte[] imgbytearray =  complaintMessage.getComplaint().getLocationImage();
            final Bitmap bmp = BitmapFactory.decodeByteArray(imgbytearray,0,imgbytearray.length);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    map.setImageBitmap(bmp);
                }
            });
        }catch (Exception e){}
    }

    private void getInventory(String organizationID, String inventoryID) {
        AboutInventoryMessage msg = new AboutInventoryMessage(organizationID,inventoryID);
        MobileClientEmp clientEmp = ((ThisApplication)getApplication()).mobileClient;
        try{
            clientEmp.writeObject(msg);
            msg = (AboutInventoryMessage) clientEmp.readNext();
        }catch (Exception e){ }

        final AboutInventoryMessage aboutInventory=msg;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                comp_no.setText(complaint.getComplaintID());
                dt.setText(complaint.getAssignedDateTime());
                title.setText(aboutInventory.getTitle());
                id.setText(aboutInventory.getId());
                desc.setText(aboutInventory.getDescription());
            }
        });
    }

}
