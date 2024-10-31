package com.example.mobilecomputing;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.CardViewHolder> {
    private List<Card> cards;
    private final Context context;

    public InventoryAdapter(Context context, List<Card> cards) {
        this.context = context;
        this.cards = cards;
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

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CardDetailsActivity.class);
            intent.putExtra("imageResId", card.getImageResId());
            intent.putExtra("cardName", card.getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public void updateList(List<Card> filteredCards) {
        this.cards = filteredCards;
        notifyDataSetChanged();
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        ImageView cardImageView;
        TextView cardNameTextView;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardImageView = itemView.findViewById(R.id.cardImageView);
            cardNameTextView = itemView.findViewById(R.id.cardNameTextView);
        }
    }
}
