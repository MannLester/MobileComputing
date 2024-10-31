package com.example.mobilecomputing;

public class Card {
    private int imageResId;
    private String name;

    public Card(int imageResId, String name) {
        this.imageResId = imageResId;
        this.name = name;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getName() {
        return name;
    }
}
