package com.example.mobilecomputing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.ImageFormat;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

public class HomeActivity extends AppCompatActivity {
    private FirebaseFirestore db;

    private TextView playerName;
    private ImageView background, darkBackground;
    private ImageButton mode_light, mode_dark, inventoryButton, shopButton, workshopButton;
    private ImageButton calculator, infrared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = FirebaseFirestore.getInstance();

        playerName = findViewById(R.id.player_name);
        background = findViewById(R.id.background);
        darkBackground = findViewById(R.id.background_dark);
        mode_light = findViewById(R.id.modes_light);
        mode_dark = findViewById(R.id.modes_dark);
        inventoryButton = findViewById(R.id.button1);
        shopButton = findViewById(R.id.button2);
        workshopButton = findViewById(R.id.button3);
        calculator = findViewById(R.id.calculator);
        infrared = findViewById(R.id.infrared);

        calculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, CalculatorActivity.class);
                startActivity(intent);
            }
        });

        infrared.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, InfraredActivity.class);
                startActivity(intent);
            }
        });

        inventoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, InventoryActivity.class);
                startActivity(intent);
            }
        });

        shopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ShopActivity.class);
                startActivity(intent);
            }
        });

        workshopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, WorkshopActivity.class);
                startActivity(intent);
            }
        });

        mode_light.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                darkBackground.setVisibility(View.VISIBLE);
                mode_dark.setVisibility(View.VISIBLE);
                background.setVisibility(View.INVISIBLE);
                mode_light.setVisibility(View.INVISIBLE);
            }
        });

        mode_dark.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                background.setVisibility(View.VISIBLE);
                mode_light.setVisibility(View.VISIBLE);
                darkBackground.setVisibility(View.INVISIBLE);
                mode_dark.setVisibility(View.INVISIBLE);
            }
        });
        getLoggedInUser();
    }

    private void getLoggedInUser() {
        db.collection("accounts")
                .whereEqualTo("loggedIn", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot querySnapshot = task.getResult();

                            if(!querySnapshot.isEmpty()){
                                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                String username = document.getString("username");
                                String documentId = document.getId();
                                if(username !=  null){
                                    playerName.setText(username);

                                    inventoryButton.setOnClickListener(view -> {
                                        Intent intent = new Intent(HomeActivity.this, InventoryActivity.class);
                                        intent.putExtra("documentId", documentId);
                                        startActivity(intent);
                                    });

                                    shopButton.setOnClickListener(view -> {
                                        Intent intent = new Intent(HomeActivity.this, ShopActivity.class);
                                        intent.putExtra("documentId", documentId);
                                        startActivity(intent);
                                    });
                                }else{
                                    Toast.makeText(HomeActivity.this, " Username not Found", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(HomeActivity.this, "No LoggedIn Users Found", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Log.e("HomeActivity", "Error getting documents: ", task.getException());
                            Toast.makeText(HomeActivity.this, "Error Occurred. Try Again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
