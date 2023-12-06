package com.imegga.suitcase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DelegationActivity extends AppCompatActivity {

    Button btn_send;
    EditText et_contact, et_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        btn_send = (Button)findViewById(R.id.send_button);
        et_contact = (EditText)findViewById(R.id.phone_number);

        PermissionToConnect();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = et_contact.getText().toString();

                Intent intent = getIntent();
                String name = intent.getStringExtra("name");
                String description = intent.getStringExtra("description");
                String price = intent.getStringExtra("price");
                String imageUrl = intent.getStringExtra("imageUrl");

                String msg = "Hey, Check out this item \n" + "Name: " + name + "\n" +
                        "Description: " + description + "\n" + "Price: " + price;

                try{
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(number, null, msg, null, null);
                    Toast.makeText(DelegationActivity.this, "Sent", Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    Toast.makeText(DelegationActivity.this, "Sending Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void PermissionToConnect(){
        if(ContextCompat.checkSelfPermission(DelegationActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(DelegationActivity.this, Manifest.permission.SEND_SMS)){
                ActivityCompat.requestPermissions(DelegationActivity.this, new String[]{Manifest.permission.SEND_SMS}, 1);
            }else{
                ActivityCompat.requestPermissions(DelegationActivity.this, new String[]{Manifest.permission.SEND_SMS}, 1);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(DelegationActivity.this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Access", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}