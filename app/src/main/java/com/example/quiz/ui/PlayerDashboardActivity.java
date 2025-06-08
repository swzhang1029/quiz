package com.example.quiz.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.quiz.R;
import com.example.quiz.databinding.ActivityPlayerDashboardBinding;
import com.example.quiz.viewmodels.AuthViewModel;

public class PlayerDashboardActivity extends AppCompatActivity {
    private ActivityPlayerDashboardBinding binding;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Player Dashboard");

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        setupViews();
    }

    private void setupViews() {
        binding.startQuizButton.setOnClickListener(v -> {
            // TODO: Implement start quiz functionality
        });

        binding.viewHistoryButton.setOnClickListener(v -> {
            // TODO: Implement view history functionality
        });

        binding.viewLeaderboardButton.setOnClickListener(v -> {
            // TODO: Implement view leaderboard functionality
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