package com.example.mobilecomputing;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    private List<Card> allCards = new ArrayList<>(); // Full list of cards
    private EditText searchBar;
    private TextView noCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        searchBar = findViewById(R.id.searchBar);
        recyclerView = findViewById(R.id.recyclerView);
        noCard = findViewById(R.id.no_card);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns

        String documentId = getIntent().getStringExtra("documentId");
        if (documentId != null) {
            fetchCards(documentId);
        } else {
            Toast.makeText(this, "No Document ID Received", Toast.LENGTH_SHORT).show();
        }

        setupSearchBar();
    }

    private void fetchCards(String documentId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference cardsRef = db.collection("cards");

        cardsRef.whereEqualTo("ownerId", documentId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        allCards.clear(); // Ensure list is empty before adding
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String imageName = document.getString("imageUrl");
                            String cardName = document.getString("cardName");
                            int resId = getResources().getIdentifier(imageName, "drawable", getPackageName());
                            allCards.add(new Card(resId, cardName));
                        }
                        adapter = new InventoryAdapter(this, allCards);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(this, "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupSearchBar() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCards(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterCards(String query) {
        List<Card> filteredList = new ArrayList<>();
        for (Card card : allCards) {
            if (card.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(card);
            }
        }

        if (filteredList.isEmpty()) {
            noCard.setVisibility(View.VISIBLE);
        } else {
            noCard.setVisibility(View.INVISIBLE);
        }

        adapter.updateList(filteredList);
    }

}
