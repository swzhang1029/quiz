package com.example.quiz.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quiz.R;
import com.example.quiz.models.Tournament;
import com.google.android.material.button.MaterialButton;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PlayerTournamentAdapter extends RecyclerView.Adapter<PlayerTournamentAdapter.TournamentViewHolder> {
    public interface OnTournamentActionListener {
        void onParticipate(Tournament tournament);
        void onLike(Tournament tournament);
        void onUnlike(Tournament tournament);
    }

    private List<Tournament> tournaments;
    private final String playerId;
    private final OnTournamentActionListener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    public PlayerTournamentAdapter(List<Tournament> tournaments, String playerId, OnTournamentActionListener listener) {
        this.tournaments = tournaments;
        this.playerId = playerId;
        this.listener = listener;
    }

    public void setTournaments(List<Tournament> tournaments) {
        this.tournaments = tournaments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TournamentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tournament, parent, false);
        return new TournamentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TournamentViewHolder holder, int position) {
        Tournament tournament = tournaments.get(position);
        holder.tournamentNameTextView.setText(tournament.getName());
        holder.categoryTextView.setText(tournament.getCategory());
        holder.difficultyTextView.setText(tournament.getDifficulty());
        String dateRange = dateFormat.format(tournament.getStartDate()) + " - " + dateFormat.format(tournament.getEndDate());
        holder.dateRangeTextView.setText(dateRange);
        holder.likesCountTextView.setText(tournament.getLikes() != null ? String.valueOf(tournament.getLikes().size()) : "0");

        // Like/Unlike button
        boolean liked = tournament.getLikes() != null && tournament.getLikes().contains(playerId);
        holder.likeButton.setImageResource(liked ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
        holder.likeButton.setOnClickListener(v -> {
            if (liked) {
                listener.onUnlike(tournament);
            } else {
                listener.onLike(tournament);
            }
        });

        // Hide edit/delete for player
        holder.editButton.setVisibility(View.GONE);
        holder.deleteButton.setVisibility(View.GONE);

        // Participation logic
        MaterialButton participateButton = holder.itemView.findViewById(R.id.participateButton);
        String cat = tournament.getCategory();
        boolean participated = tournament.getParticipants() != null && tournament.getParticipants().contains(playerId);
        boolean isOngoing = cat.contains("Ongoing");
        boolean isUpcoming = cat.contains("Upcoming");
        boolean isPast = cat.contains("Past");
        boolean isCompleted = cat.contains("Completed");
        if (isOngoing && !participated) {
            participateButton.setVisibility(View.VISIBLE);
            participateButton.setEnabled(true);
            participateButton.setText("Participate");
            participateButton.setOnClickListener(v -> listener.onParticipate(tournament));
        } else {
            participateButton.setVisibility(View.VISIBLE);
            participateButton.setEnabled(false);
            if (isUpcoming) {
                participateButton.setText("Upcoming");
            } else if (isPast) {
                participateButton.setText("Past");
            } else if (isCompleted) {
                participateButton.setText("Completed");
            } else {
                participateButton.setText("Not Available");
            }
            participateButton.setOnClickListener(null);
        }
        // Remove item click for participation, use only the button
        holder.itemView.setOnClickListener(null);
    }

    @Override
    public int getItemCount() {
        return tournaments != null ? tournaments.size() : 0;
    }

    static class TournamentViewHolder extends RecyclerView.ViewHolder {
        TextView tournamentNameTextView, categoryTextView, difficultyTextView, dateRangeTextView, likesCountTextView;
        ImageButton likeButton, editButton, deleteButton;
        TournamentViewHolder(@NonNull View itemView) {
            super(itemView);
            tournamentNameTextView = itemView.findViewById(R.id.tournamentNameTextView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            difficultyTextView = itemView.findViewById(R.id.difficultyTextView);
            dateRangeTextView = itemView.findViewById(R.id.dateRangeTextView);
            likesCountTextView = itemView.findViewById(R.id.likesCountTextView);
            likeButton = itemView.findViewById(R.id.likeButton);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
} 