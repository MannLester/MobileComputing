package com.example.mobilecomputing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


public class LoginActivity extends AppCompatActivity {

    private EditText loginUsername;
    private EditText loginPassword;
    private Button loginButton;
    private TextView forgotPassword;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = FirebaseFirestore.getInstance();

        loginUsername = findViewById(R.id.loginUsername);
        loginPassword = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.loginButtonForLogin);
        forgotPassword = findViewById(R.id.forgor);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = loginUsername.getText().toString().trim();
                String password = loginPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                } else {
                    checkUsernameAndPassword(username, password);
                }
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showForgotPasswordDialog();
            }
        });
    }

    private void showForgotPasswordDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Forgot Password");

        final EditText input = new EditText(this);
        input.setHint("Enter your username");
        builder.setView(input);

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                String username = input.getText().toString().trim();
                if(!username.isEmpty()) {
                    checkUsernameInDatabase(username);
                }else{
                    Toast.makeText(LoginActivity.this, "Please Enter a Username", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void checkUsernameInDatabase(String username){
        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful() && !task.getResult().isEmpty()){
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            String email = document.getString("email");
                            String password = document.getString("password");

                            if (email != null && password != null){
                                sendEmail(email, password);
                            } else{
                                Toast.makeText(LoginActivity.this, "Email or Password not found", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(LoginActivity.this, "Email Not Found In Database", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("LoginActivity", "Error checking email", e);
                    Toast.makeText(LoginActivity.this, "Error occured. Try again.", Toast.LENGTH_SHORT).show();
                });
    }

    private void sendEmail(String email, String password){
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Password Recovery");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Your password is: " + password);

        try{
            startActivity(Intent.createChooser(emailIntent, "Send email using..."));
            Toast.makeText(LoginActivity.this, "Password sent to: " + email, Toast.LENGTH_SHORT).show();
        }catch (android.content.ActivityNotFoundException ex){
            Toast.makeText(LoginActivity.this, "No email clients installed", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkUsernameAndPassword(String username, String password) {
        db.collection("accounts")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            String storedPassword = document.getString("password");

                            if (storedPassword != null && storedPassword.equals(password)) {
                                db.collection("accounts").document(document.getId())
                                        .update("loggedIn", true)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                            startActivity(intent);
                                        })
                                        .addOnFailureListener(e -> {
                                            // Handle failure to update loggedIn field
                                            Toast.makeText(LoginActivity.this, "Failed to update login status", Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                Toast.makeText(LoginActivity.this, "Invalid password", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Username does not exist
                            Toast.makeText(LoginActivity.this, "Username does not exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("LoginActivity", "Error checking username", e);
                    Toast.makeText(LoginActivity.this, "Error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }
}
