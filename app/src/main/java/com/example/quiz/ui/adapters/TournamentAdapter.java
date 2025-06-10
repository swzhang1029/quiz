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
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TournamentAdapter extends RecyclerView.Adapter<TournamentAdapter.TournamentViewHolder> {
    public interface OnTournamentActionListener {
        void onEdit(Tournament tournament);
        void onDelete(Tournament tournament);
    }

    private List<Tournament> tournaments;
    private final OnTournamentActionListener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    public TournamentAdapter(List<Tournament> tournaments, OnTournamentActionListener listener) {
        this.tournaments = tournaments;
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

        holder.editButton.setOnClickListener(v -> listener.onEdit(tournament));
        holder.deleteButton.setOnClickListener(v -> listener.onDelete(tournament));
    }

    @Override
    public int getItemCount() {
        return tournaments != null ? tournaments.size() : 0;
    }

    static class TournamentViewHolder extends RecyclerView.ViewHolder {
        TextView tournamentNameTextView, categoryTextView, difficultyTextView, dateRangeTextView, likesCountTextView;
        ImageButton editButton, deleteButton;
        TournamentViewHolder(@NonNull View itemView) {
            super(itemView);
            tournamentNameTextView = itemView.findViewById(R.id.tournamentNameTextView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            difficultyTextView = itemView.findViewById(R.id.difficultyTextView);
            dateRangeTextView = itemView.findViewById(R.id.dateRangeTextView);
            likesCountTextView = itemView.findViewById(R.id.likesCountTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
} 