package com.example.mobilecomputing;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class InfraredActivity extends AppCompatActivity {

    private TextView statusText;
    private ImageView phoneImage;
    private ConstraintLayout mainLayout;
    private Handler handler;

    private float phoneX = 0;
    private float phoneY = 0;
    private float textX = 0;
    private float textY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infrared);

        // Initialize views
        statusText = findViewById(R.id.statusText);
        phoneImage = findViewById(R.id.phoneImage);
        mainLayout = findViewById(R.id.mainLayout);

        // Get initial position of statusText
        statusText.post(new Runnable() {
            @Override
            public void run() {
                textX = statusText.getX() + statusText.getWidth() / 2;
                textY = statusText.getY() + statusText.getHeight() / 2;
            }
        });

        // Set touch listener for the phone image to move it
        phoneImage.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    // Update the phone's position as it moves
                    phoneX = event.getRawX() - phoneImage.getWidth() / 2;
                    phoneY = event.getRawY() - phoneImage.getHeight() / 2;

                    // Move the phone image on the screen
                    phoneImage.setX(phoneX);
                    phoneImage.setY(phoneY);

                    // Check the infrared range based on the new position
                    updateInfraredRange();

                    break;
            }
            return true;
        });
    }

    // This method calculates the distance between the phone and the statusText
    private void updateInfraredRange() {
        // Calculate distance from phone to the status text
        float distance = (float) Math.sqrt(Math.pow(phoneX - textX, 2) + Math.pow(phoneY - textY, 2));

        // Debugging: Log the distance
        Log.d("InfraredActivity", "Distance: " + distance);

        // Update the screen based on the distance
        if (distance < 200) {
            // Within range: Green color
            updateScreen(Color.GREEN, "In range");
        } else if (distance < 400) {
            // Mid-range: Orange color
            updateScreen(Color.rgb(255, 165, 0), "Near range");
        } else {
            // Too far: Red color
            updateScreen(Color.RED, "Out of range");
        }
    }

    // This method updates the background color and status text of the screen
    private void updateScreen(int color, String status) {
        // Update background color of the layout
        mainLayout.setBackgroundColor(color);
        // Update status text
        statusText.setText("Infrared Range: " + status);
    }
}
