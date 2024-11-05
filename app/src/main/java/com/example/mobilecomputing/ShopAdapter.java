package com.example.mobilecomputing;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.CardViewHolder> {
    private List<Card> cards;
    private final Context context;
    private Card selectedCard;
    private OnItemClickListener onItemClickListener;

    // Define the interface for click events
    public interface OnItemClickListener {
        void onItemClick(Card card);
    }

    public ShopAdapter(Context context, List<Card> cards) {
        this.context = context;
        this.cards = cards;
    }

    // Method to set the click listener
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Card card = cards.get(position);
        holder.cardImageView.setImageResource(card.getImageResId());
        holder.cardNameTextView.setText(card.getName());

        holder.itemView.setBackgroundColor(card == selectedCard ? Color.LTGRAY : Color.TRANSPARENT);

        // Set up the item click event
        holder.itemView.setOnClickListener(v -> {
            selectedCard = card;
            notifyDataSetChanged(); // Highlight selected item
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(card);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public Card getSelectedCard() {
        return selectedCard;
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        ImageView cardImageView;
        TextView cardNameTextView;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardImageView = itemView.findViewById(R.id.cardImageView);
            cardNameTextView = itemView.findViewById(R.id.cardNameTextView);
        }
    }
}
