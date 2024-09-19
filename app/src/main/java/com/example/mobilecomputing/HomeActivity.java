package com.example.mobilecomputing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

public class HomeActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    ImageButton accountButton, playButton;
    Button cardsButton;
    TextView accountName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        accountButton = findViewById(R.id.account_button);
        playButton = findViewById(R.id.battle_button);
        cardsButton = findViewById(R.id.cards_button);
        accountName = findViewById(R.id.account_name);
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Query Firestore for a document where loggedIn == true
        db.collection("accounts")
                .whereEqualTo("loggedIn", true)
                .limit(1) // You can limit to 1 since we expect only one user to be logged in at a time
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Check if the query returned a result
                            if (!task.getResult().isEmpty()) {
                                // Loop through the documents and get the username of the logged-in user
                                for (DocumentSnapshot document : task.getResult()) {
                                    String username = document.getString("username");
                                    // Set the button text to the logged-in username
                                    accountName.setText(username);
                                }
                            } else {
                                Toast.makeText(HomeActivity.this, "No logged-in user found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Handle failure
                            Toast.makeText(HomeActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, BattleActivity.class);
                startActivity(intent);
            }
        });

        cardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, CardsActivity.class);
                startActivity(intent);
            }
        });
    }
}
