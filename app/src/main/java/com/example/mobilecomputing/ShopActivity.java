package com.example.mobilecomputing;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShopActivity extends AppCompatActivity {
    private ImageView plusIcon;
    private Button shareButton;
    private List<Card> userCards = new ArrayList<>();
    private Card selectedCard;
    private static final int REQUEST_WRITE_PERMISSION = 1001;
    private static final int REQUEST_READ_PERMISSION = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_PERMISSION);
        }


        plusIcon = findViewById(R.id.plus_icon);
        shareButton = findViewById(R.id.share_button);

        String documentId = getIntent().getStringExtra("documentId");
        if (documentId != null) {
            fetchUserCards(documentId);
        } else {
            Toast.makeText(this, "No Document ID Received", Toast.LENGTH_SHORT).show();
        }

        plusIcon.setOnClickListener(v -> showCardSelectionDialog());
        shareButton.setOnClickListener(v -> shareSelectedCard());
    }

    private void fetchUserCards(String documentId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference cardsRef = db.collection("cards");

        cardsRef.whereEqualTo("ownerId", documentId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userCards.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String imageName = document.getString("imageUrl");
                            String cardName = document.getString("cardName");
                            int resId = getResources().getIdentifier(imageName, "drawable", getPackageName());
                            userCards.add(new Card(resId, cardName));
                        }
                    } else {
                        Toast.makeText(this, "Error getting cards: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showCardSelectionDialog() {
        if (userCards.isEmpty()) {
            Toast.makeText(this, "No cards to display", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_card_selection, null);
        RecyclerView recyclerView = dialogView.findViewById(R.id.card_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create the adapter and set the listener
        ShopAdapter adapter = new ShopAdapter(this, userCards);
        recyclerView.setAdapter(adapter);

        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        dialog.show();

        adapter.setOnItemClickListener(card -> {
            selectedCard = card;
            Toast.makeText(this, "Selected: " + card.getName(), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }

    private void shareSelectedCard() {
        if (selectedCard == null) {
            Toast.makeText(this, "Please select a card to share", Toast.LENGTH_SHORT).show();
            return;
        }

        String selectedCardName = selectedCard.getName();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference cardsRef = db.collection("cards");

        cardsRef.whereEqualTo("cardName", selectedCardName).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String imageUrl = document.getString("imageUrl");

                            if (imageUrl == null) {
                                Toast.makeText(this, "Image URL not found in document", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Proceed with image sharing using imageUrl
                            shareImageFromResource(imageUrl);
                            return;
                        }
                    } else {
                        Toast.makeText(this, "No document found with card name: " + selectedCardName, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void shareImageFromResource(String imageName) {
        File imageFile = new File(getCacheDir(), imageName + ".jpg");

        if (!imageFile.exists()) {
            try {
                FileOutputStream fos = new FileOutputStream(imageFile);
                int resId = getResources().getIdentifier(imageName, "drawable", getPackageName());

                if (resId == 0) {
                    Log.e("Share", "Resource not found: " + imageName);
                    Toast.makeText(this, "Error: Image resource not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();

                Log.d("Share", "Image file created: " + imageFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error creating file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Uri contentUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", imageFile);
        Log.d("Share", "Content URI: " + contentUri);

        if (contentUri == null) {
            Log.e("Share", "Content URI is null");
            Toast.makeText(this, "Error creating content URI", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/png");
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setPackage("com.android.bluetooth");

        try {
            startActivity(Intent.createChooser(shareIntent, "Share Image"));
        } catch (ActivityNotFoundException e) {
            Log.e("Share", "Activity not found: " + e.getMessage());
            Toast.makeText(this, "No app available to share image via Bluetooth", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("Share", "Exception: " + e.getMessage());
            Toast.makeText(this, "Error sharing image", Toast.LENGTH_SHORT).show();
        }
    }

}