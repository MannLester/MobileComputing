package com.example.mobilecomputing;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class InventoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private InventoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns

        String documentId = getIntent().getStringExtra("documentId");
        if (documentId != null) {
            fetchCards(documentId);
        } else {
            Toast.makeText(this, "No Document ID Received", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchCards(String documentId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference cardsRef = db.collection("cards");

        cardsRef.whereEqualTo("ownerId", documentId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Card> cards = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String imageName = document.getString("imageUrl");
                            String cardName = document.getString("name");
                            int resId = getResources().getIdentifier(imageName, "drawable", getPackageName());
                            cards.add(new Card(resId, cardName));
                        }
                        adapter = new InventoryAdapter(cards);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(this, "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
