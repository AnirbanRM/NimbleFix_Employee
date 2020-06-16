package com.nimblefix.empapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nimblefix.ControlMessages.ComplaintMessage;
import com.nimblefix.MobileClientEmp;
import com.nimblefix.core.Complaint;

import java.util.ArrayList;

public class pendTask extends AppCompatActivity {

    ListView pendListV;
    ArrayList<Complaint> complaints;
    private String owner;

    class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return complaints.size();
        }

        @Override
        public Object getItem(int position) {
            return pendTask.this.complaints.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = getLayoutInflater().inflate(R.layout.penditemview,null,false);
            ((TextView)v.findViewById(R.id.date_time)).setText(complaints.get(position).getAssignedDateTime());
            ((TextView)v.findViewById(R.id.invid)).setText(complaints.get(position).getInventoryID());
            ((TextView)v.findViewById(R.id.cno)).setText(complaints.get(position).getComplaintID());
            setOnClick(v,position);
            return v;
        }

        public void setOnClick(View v, final int position){
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(pendTask.this,AboutComplaint.class);
                    i.putExtra("Complaint",complaints.get(position));
                    i.putExtra("Owner",owner);
                    startActivity(i);
                }
            });
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pendtask);
        pendListV = findViewById(R.id.list);

    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                fetch_PendingWork();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setAdapter();
                    }
                });

            }
        }).start();

    }

    private void fetch_PendingWork() {
        MobileClientEmp clientEmp = ((ThisApplication)getApplication()).mobileClient;
        ComplaintMessage complaintMessage = new ComplaintMessage(new ArrayList<Complaint>());
        complaintMessage.setBody("FETCH:"+((ThisApplication)getApplication()).getOrganisationID()+"/"+((ThisApplication)getApplication()).getEmployeeEmail());
        clientEmp.writeObject(complaintMessage);

        try {
            Object o = clientEmp.readNext();
            if(o instanceof ComplaintMessage){
                this.owner = ((ComplaintMessage)o).getBody();
                this.complaints = ((ComplaintMessage) o).getComplaints();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setAdapter();
                    }
                });
            }

        }catch (Exception e){}
    }

    private void setAdapter() {
        ListAdapter adapter = new ListAdapter();
        pendListV.setAdapter(adapter);
    }
}
