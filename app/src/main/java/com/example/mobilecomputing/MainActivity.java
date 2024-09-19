package com.example.mobilecomputing;

import androidx.appcompat.app.AppCompatActivity;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get references to each letter TextView and background ImageView
        TextView letterC = findViewById(R.id.letterC);
        TextView letterA = findViewById(R.id.letterA);
        TextView letterR = findViewById(R.id.letterR);
        TextView letterD = findViewById(R.id.letterD);
        TextView letterS = findViewById(R.id.letterS);
        TextView ofPower = findViewById(R.id.ofPower);
        ImageView backgroundImage = findViewById(R.id.background);
        TextView pressToStart = findViewById(R.id.pressToStart);
        Button loginButton = findViewById(R.id.loginButton);
        Button signupButton = findViewById(R.id.signupButton);// Background image view (make sure this exists in your XML)

        // Set letters initially off-screen (bottom-left)
        letterC.setTranslationX(-500f);
        letterC.setTranslationY(1000f);

        letterA.setTranslationX(-500f);
        letterA.setTranslationY(1000f);

        letterR.setTranslationX(-500f);
        letterR.setTranslationY(1000f);

        letterD.setTranslationX(-500f);
        letterD.setTranslationY(1000f);

        letterS.setTranslationX(-500f);
        letterS.setTranslationY(1000f);

        // Animate each letter moving to the center
        ObjectAnimator moveC = ObjectAnimator.ofFloat(letterC, "translationX", 0f);
        ObjectAnimator moveA = ObjectAnimator.ofFloat(letterA, "translationX", 0f);
        ObjectAnimator moveR = ObjectAnimator.ofFloat(letterR, "translationX", 0f);
        ObjectAnimator moveD = ObjectAnimator.ofFloat(letterD, "translationX", 0f);
        ObjectAnimator moveS = ObjectAnimator.ofFloat(letterS, "translationX", 0f);

        ObjectAnimator moveCY = ObjectAnimator.ofFloat(letterC, "translationY", 0f);
        ObjectAnimator moveAY = ObjectAnimator.ofFloat(letterA, "translationY", 0f);
        ObjectAnimator moveRY = ObjectAnimator.ofFloat(letterR, "translationY", 0f);
        ObjectAnimator moveDY = ObjectAnimator.ofFloat(letterD, "translationY", 0f);
        ObjectAnimator moveSY = ObjectAnimator.ofFloat(letterS, "translationY", 0f);

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(pressToStart, "alpha", 1f, 0f);
        fadeOut.setDuration(1000);
        fadeOut.setRepeatCount(ObjectAnimator.INFINITE);
        fadeOut.setRepeatMode(ObjectAnimator.REVERSE);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(pressToStart, "alpha", 0f, 1f);
        fadeIn.setDuration(1000);
        fadeIn.setRepeatCount(ObjectAnimator.INFINITE);
        fadeIn.setRepeatMode(ObjectAnimator.REVERSE);

        AnimatorSet blinkSet = new AnimatorSet();
        blinkSet.playSequentially(fadeOut, fadeIn);
        blinkSet.setInterpolator(new AccelerateDecelerateInterpolator());
        blinkSet.start();

        pressToStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                blinkSet.cancel();
                pressToStart.setAlpha(1f);

                loginButton.setVisibility(View.VISIBLE);
                signupButton.setVisibility(View.VISIBLE);
                pressToStart.setVisibility(View.INVISIBLE);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        // Set durations and interpolators for smooth animation
        moveC.setDuration(1000);
        moveA.setDuration(1000);
        moveR.setDuration(1000);
        moveD.setDuration(1000);
        moveS.setDuration(1000);

        moveCY.setDuration(1000);
        moveAY.setDuration(1000);
        moveRY.setDuration(1000);
        moveDY.setDuration(1000);
        moveSY.setDuration(1000);

        moveC.setInterpolator(new AccelerateDecelerateInterpolator());
        moveA.setInterpolator(new AccelerateDecelerateInterpolator());
        moveR.setInterpolator(new AccelerateDecelerateInterpolator());
        moveD.setInterpolator(new AccelerateDecelerateInterpolator());
        moveS.setInterpolator(new AccelerateDecelerateInterpolator());

        moveCY.setInterpolator(new AccelerateDecelerateInterpolator());
        moveAY.setInterpolator(new AccelerateDecelerateInterpolator());
        moveRY.setInterpolator(new AccelerateDecelerateInterpolator());
        moveDY.setInterpolator(new AccelerateDecelerateInterpolator());
        moveSY.setInterpolator(new AccelerateDecelerateInterpolator());

        // Delay each letter for a staggered animation effect
        moveC.setStartDelay(0);
        moveCY.setStartDelay(0);
        moveA.setStartDelay(200);
        moveAY.setStartDelay(200);
        moveR.setStartDelay(400);
        moveRY.setStartDelay(400);
        moveD.setStartDelay(600);
        moveDY.setStartDelay(600);
        moveS.setStartDelay(800);
        moveSY.setStartDelay(800);

        // Group animations into sets
        AnimatorSet setX = new AnimatorSet();
        AnimatorSet setY = new AnimatorSet();
        setX.playTogether(moveC, moveA, moveR, moveD, moveS);
        setY.playTogether(moveCY, moveAY, moveRY, moveDY, moveSY);

        // Combine both X and Y animations and start
        AnimatorSet finalSet = new AnimatorSet();
        finalSet.playTogether(setX, setY);
        finalSet.start();

        // After letters are in place, animate "of Power" with glow and fade-in
        finalSet.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                // "of Power" text fade-in effect
                ObjectAnimator fadeGlow = ObjectAnimator.ofFloat(ofPower, "alpha", 0f, 1f);
                fadeGlow.setDuration(1000);
                fadeGlow.setInterpolator(new AccelerateDecelerateInterpolator());
                ofPower.setVisibility(View.VISIBLE);
                fadeGlow.start();

                // After "of Power" appears, create a flash of light effect
                fadeGlow.addListener(new android.animation.AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(android.animation.Animator animation) {
                        // Flash effect
                        View flashView = new View(MainActivity.this);
                        flashView.setBackgroundColor(Color.WHITE);
                        flashView.setAlpha(0f);
                        ((ConstraintLayout) findViewById(R.id.constraintLayout)).addView(flashView,
                                new ConstraintLayout.LayoutParams(
                                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                                        ConstraintLayout.LayoutParams.MATCH_PARENT));

                        ObjectAnimator flashIn = ObjectAnimator.ofFloat(flashView, "alpha", 0f, 1f);
                        flashIn.setDuration(100);
                        flashIn.setInterpolator(new AccelerateDecelerateInterpolator());

                        ObjectAnimator flashOut = ObjectAnimator.ofFloat(flashView, "alpha", 1f, 0f);
                        flashOut.setDuration(100);
                        flashOut.setInterpolator(new AccelerateDecelerateInterpolator());

                        AnimatorSet flashSet = new AnimatorSet();
                        flashSet.playSequentially(flashIn, flashOut);
                        flashSet.start();

                        flashSet.addListener(new android.animation.AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(android.animation.Animator animation) {
                                // Remove the flash view after the animation
                                ((ConstraintLayout) findViewById(R.id.constraintLayout)).removeView(flashView);

                                // Make the background image visible after the flash
                                backgroundImage.setVisibility(View.VISIBLE);
                                pressToStart.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                });
            }
        });
    }
}
