package com.example.quiz.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.quiz.R;
import com.example.quiz.databinding.ActivityAdminDashboardBinding;
import com.example.quiz.viewmodels.AuthViewModel;
import com.example.quiz.viewmodels.TournamentViewModel;

public class AdminDashboardActivity extends AppCompatActivity {
    private ActivityAdminDashboardBinding binding;
    private TournamentViewModel tournamentViewModel;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Admin Dashboard");

        tournamentViewModel = new ViewModelProvider(this).get(TournamentViewModel.class);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        setupViews();
        observeViewModel();
    }

    private void setupViews() {
        binding.tournamentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // TODO: Set up RecyclerView adapter

        binding.createTournamentFab.setOnClickListener(v -> {
            Intent intent = new Intent(this, TournamentEditActivity.class);
            startActivity(intent);
        });
    }

    private void observeViewModel() {
        tournamentViewModel.getTournaments().observe(this, tournaments -> {
            // TODO: Update RecyclerView adapter
            binding.emptyView.setVisibility(tournaments.isEmpty() ? View.VISIBLE : View.GONE);
        });

        tournamentViewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        tournamentViewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });
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

    @Override
    protected void onResume() {
        super.onResume();
        tournamentViewModel.loadTournaments();
    }
} 