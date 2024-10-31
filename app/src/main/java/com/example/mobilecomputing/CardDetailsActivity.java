package com.example.mobilecomputing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CardDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);

        ImageView cardImageView = findViewById(R.id.cardImageView);
        TextView cardNameTextView = findViewById(R.id.cardNameTextView);
        Button backButton = findViewById(R.id.backButton);

        // Retrieve the data passed from InventoryActivity
        Intent intent = getIntent();
        int imageResId = intent.getIntExtra("imageResId", 0);
        String cardName = intent.getStringExtra("cardName");

        // Set the image and name
        cardImageView.setImageResource(imageResId);
        cardNameTextView.setText(cardName);

        // Set back button functionality
        backButton.setOnClickListener(v -> finish());
    }
}
