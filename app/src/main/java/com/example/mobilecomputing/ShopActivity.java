package com.example.mobilecomputing;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;

public class ShopActivity extends AppCompatActivity {
    private Button shareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        shareButton = findViewById(R.id.shareButton); // Make sure this ID matches your button in XML

        shareButton.setOnClickListener(v -> shareCardImage());
    }

    private void shareCardImage() {
        // Replace with actual card image path
        File imageFile = new File(getFilesDir(), "card_image.jpg");

        Uri imageUri = FileProvider.getUriForFile(this, "com.example.mobilecomputing.fileprovider", imageFile);

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("image/jpeg");
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setPackage("com.android.bluetooth"); // Open the Bluetooth share dialog directly

        startActivity(Intent.createChooser(shareIntent, "Share Card Image"));
    }
}
