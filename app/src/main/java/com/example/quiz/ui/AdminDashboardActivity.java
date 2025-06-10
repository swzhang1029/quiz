package com.example.quiz.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.quiz.R;
import com.example.quiz.databinding.ActivityAdminDashboardBinding;
import com.example.quiz.viewmodels.AuthViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.quiz.ui.adapters.TournamentAdapter;
import com.example.quiz.models.Tournament;
import com.example.quiz.viewmodels.TournamentViewModel;
import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;

public class AdminDashboardActivity extends AppCompatActivity {
    private ActivityAdminDashboardBinding binding;
    private AuthViewModel authViewModel;
    private TournamentViewModel tournamentViewModel;
    private TournamentAdapter tournamentAdapter;
    private List<Tournament> tournamentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Admin Dashboard");

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        tournamentViewModel = new ViewModelProvider(this).get(TournamentViewModel.class);

        setupRecyclerView();
        setupViews();
        observeTournaments();
    }

    private void setupViews() {
        binding.createQuizButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, TournamentEditActivity.class);
            startActivity(intent);
        });


        binding.logoutButton.setOnClickListener(v -> {
            authViewModel.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void setupRecyclerView() {
        tournamentAdapter = new TournamentAdapter(tournamentList, new TournamentAdapter.OnTournamentActionListener() {
            @Override
            public void onEdit(Tournament tournament) {
                Intent intent = new Intent(AdminDashboardActivity.this, TournamentEditActivity.class);
                intent.putExtra("tournamentId", tournament.getTournamentId());
                startActivity(intent);
            }
            @Override
            public void onDelete(Tournament tournament) {
                new AlertDialog.Builder(AdminDashboardActivity.this)
                    .setTitle("Delete Tournament")
                    .setMessage("Are you sure you want to delete this tournament?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        tournamentViewModel.deleteTournament(tournament.getTournamentId());
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            }
        });
        binding.tournamentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.tournamentsRecyclerView.setAdapter(tournamentAdapter);
    }

    private void observeTournaments() {
        tournamentViewModel.getTournaments().observe(this, tournaments -> {
            tournamentList.clear();
            if (tournaments != null && !tournaments.isEmpty()) {
                tournamentList.addAll(tournaments);
                binding.tournamentsRecyclerView.setVisibility(android.view.View.VISIBLE);
                binding.emptyView.setVisibility(android.view.View.GONE);
            } else {
                binding.tournamentsRecyclerView.setVisibility(android.view.View.GONE);
                binding.emptyView.setVisibility(android.view.View.VISIBLE);
            }
            tournamentAdapter.notifyDataSetChanged();
        });
        tournamentViewModel.getError().observe(this, error -> {
            if (error != null) {
                android.widget.Toast.makeText(this, "Error: " + error, android.widget.Toast.LENGTH_LONG).show();
            }
        });
        tournamentViewModel.getIsLoading().observe(this, isLoading -> {
            // Optionally show a loading indicator
        });
        tournamentViewModel.loadTournaments();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
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