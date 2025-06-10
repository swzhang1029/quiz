package com.example.quiz.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.quiz.R;
import com.example.quiz.databinding.ActivityLoginBinding;
import com.example.quiz.models.User;
import com.example.quiz.viewmodels.AuthViewModel;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private ActivityLoginBinding binding;
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        setupViews();
        observeViewModel();
    }

    private void setupViews() {
        binding.loginButton.setOnClickListener(v -> attemptLogin());
        binding.registerButton.setOnClickListener(v -> {
            // TODO: Implement registration
            Toast.makeText(this, "Registration not implemented yet", Toast.LENGTH_SHORT).show();
        });
    }

    private void observeViewModel() {
        viewModel.getCurrentUser().observe(this, this::handleUserChange);
        viewModel.getIsLoading().observe(this, this::handleLoading);
        viewModel.getError().observe(this, this::handleError);
    }

    private void attemptLogin() {
        String email = binding.emailEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();

        if (validateInput(email, password)) {
            Log.d(TAG, "Attempting login with email: " + email);
            viewModel.signIn(email, password);
        }
    }

    private boolean validateInput(String email, String password) {
        boolean isValid = true;

        if (email.isEmpty()) {
            binding.emailLayout.setError("Email is required");
            isValid = false;
        } else {
            binding.emailLayout.setError(null);
        }

        if (password.isEmpty()) {
            binding.passwordLayout.setError("Password is required");
            isValid = false;
        } else {
            binding.passwordLayout.setError(null);
        }

        return isValid;
    }

    private void handleUserChange(User user) {
        if (user != null) {
            Log.d(TAG, "User logged in successfully. Role: " + user.getRole());
            Intent intent;
            if ("admin".equals(user.getRole())) {
                Log.d(TAG, "Navigating to AdminDashboardActivity");
                intent = new Intent(this, AdminDashboardActivity.class);
            } else {
                Log.d(TAG, "Navigating to PlayerDashboardActivity");
                intent = new Intent(this, PlayerDashboardActivity.class);
            }
            startActivity(intent);
            finish();
        } else {
            Log.d(TAG, "User is null");
        }
    }

    private void handleLoading(Boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.loginButton.setEnabled(!isLoading);
        binding.registerButton.setEnabled(!isLoading);
    }

    private void handleError(String error) {
        if (error != null) {
            Log.e(TAG, "Error during login: " + error);
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }
} 