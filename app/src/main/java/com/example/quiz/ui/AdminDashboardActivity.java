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

public class AdminDashboardActivity extends AppCompatActivity {
    private ActivityAdminDashboardBinding binding;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Admin Dashboard");

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        setupViews();
    }

    private void setupViews() {
        binding.createQuizButton.setOnClickListener(v -> {
            // TODO: Implement create quiz functionality
        });

        binding.viewQuizzesButton.setOnClickListener(v -> {
            // TODO: Implement view quizzes functionality
        });

        binding.viewPlayersButton.setOnClickListener(v -> {
            // TODO: Implement view players functionality
        });

        binding.logoutButton.setOnClickListener(v -> {
            authViewModel.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
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
} 