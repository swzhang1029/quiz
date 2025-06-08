package com.example.quiz.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.quiz.R;
import com.example.quiz.databinding.ActivityPlayerDashboardBinding;
import com.example.quiz.ui.adapters.PlayerTournamentAdapter;
import com.example.quiz.models.Tournament;
import com.example.quiz.viewmodels.AuthViewModel;
import com.example.quiz.viewmodels.TournamentViewModel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlayerDashboardActivity extends AppCompatActivity {
    private ActivityPlayerDashboardBinding binding;
    private AuthViewModel authViewModel;
    private TournamentViewModel tournamentViewModel;
    private PlayerTournamentAdapter playerTournamentAdapter;
    private List<Tournament> playerTournamentList = new ArrayList<>();
    private String playerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Player Dashboard");

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        tournamentViewModel = new ViewModelProvider(this).get(TournamentViewModel.class);

        playerId = authViewModel.getCurrentUser().getValue() != null ? authViewModel.getCurrentUser().getValue().getUserId() : "";

        setupRecyclerView();
        observeTournaments();
        setupViews();
    }

    private void setupRecyclerView() {
        playerTournamentAdapter = new PlayerTournamentAdapter(playerTournamentList, playerId, new PlayerTournamentAdapter.OnTournamentActionListener() {
            @Override
            public void onParticipate(Tournament tournament) {
                android.widget.Toast.makeText(PlayerDashboardActivity.this, "Starting quiz for: " + tournament.getName(), android.widget.Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PlayerDashboardActivity.this, QuizActivity.class);
                intent.putExtra("tournament", tournament);
                startActivity(intent);
            }
            @Override
            public void onLike(Tournament tournament) {
                // TODO: Implement like logic
            }
            @Override
            public void onUnlike(Tournament tournament) {
                // TODO: Implement unlike logic
            }
        });
        binding.playerTournamentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.playerTournamentsRecyclerView.setAdapter(playerTournamentAdapter);
    }

    private void observeTournaments() {
        tournamentViewModel.getTournaments().observe(this, tournaments -> {
            playerTournamentList.clear();
            if (tournaments != null && !tournaments.isEmpty()) {
                Date now = new Date();
                for (Tournament t : tournaments) {
                    boolean participated = t.getParticipants() != null && t.getParticipants().contains(playerId);
                    if (t.getStartDate() != null && t.getEndDate() != null) {
                        if (now.before(t.getStartDate())) {
                            t.setCategory(t.getCategory() + " (Upcoming)");
                        } else if (now.after(t.getEndDate())) {
                            t.setCategory(t.getCategory() + " (Past)");
                        } else if (participated) {
                            t.setCategory(t.getCategory() + " (Completed)");
                        } else {
                            t.setCategory(t.getCategory() + " (Ongoing)");
                        }
                    }
                    playerTournamentList.add(t);
                }
                binding.playerTournamentsRecyclerView.setVisibility(android.view.View.VISIBLE);
                binding.playerEmptyView.setVisibility(android.view.View.GONE);
            } else {
                binding.playerTournamentsRecyclerView.setVisibility(android.view.View.GONE);
                binding.playerEmptyView.setVisibility(android.view.View.VISIBLE);
            }
            playerTournamentAdapter.notifyDataSetChanged();
        });
        tournamentViewModel.loadTournaments();
    }

    private void setupViews() {

        binding.viewHistoryButton.setOnClickListener(v -> {
            // TODO: Implement view history functionality
        });


        binding.logoutButton.setOnClickListener(v -> {
            authViewModel.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.player_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            authViewModel.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 