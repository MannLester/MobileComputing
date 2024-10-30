package com.example.mobilecomputing;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class InventoryActivity extends AppCompatActivity {
    @Override

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        String documentId = getIntent().getStringExtra("documentId");

        if(documentId != null){
            Toast.makeText(this, "Document ID: " + documentId, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "No Document ID Received", Toast.LENGTH_SHORT).show();
        }
    }
}
