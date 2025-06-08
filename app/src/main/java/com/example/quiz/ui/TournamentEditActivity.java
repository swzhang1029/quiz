package com.example.quiz.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.quiz.databinding.ActivityTournamentEditBinding;
import com.example.quiz.models.Tournament;
import com.example.quiz.viewmodels.TournamentViewModel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TournamentEditActivity extends AppCompatActivity {
    private ActivityTournamentEditBinding binding;
    private TournamentViewModel viewModel;
    private Calendar startDateCalendar;
    private Calendar endDateCalendar;
    private SimpleDateFormat dateFormat;
    private Tournament editingTournament;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTournamentEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Create Tournament");

        viewModel = new ViewModelProvider(this).get(TournamentViewModel.class);

        setupDatePickers();
        setupSpinners();
        setupViews();
        observeViewModel();
    }

    private void setupDatePickers() {
        startDateCalendar = Calendar.getInstance();
        endDateCalendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

        binding.startDateButton.setOnClickListener(v -> showDatePicker(true));
        binding.endDateButton.setOnClickListener(v -> showDatePicker(false));
    }

    private void setupSpinners() {
        // Categories
        String[] categories = {"General Knowledge", "Science", "History", "Geography", "Sports"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, categories);
        binding.categoryAutoComplete.setAdapter(categoryAdapter);

        // Difficulties
        String[] difficulties = {"Easy", "Medium", "Hard"};
        ArrayAdapter<String> difficultyAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, difficulties);
        binding.difficultyAutoComplete.setAdapter(difficultyAdapter);
    }

    private void setupViews() {
        binding.saveButton.setOnClickListener(v -> saveTournament());
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? android.view.View.VISIBLE : android.view.View.GONE);
            binding.saveButton.setEnabled(!isLoading);
        });

        viewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showDatePicker(boolean isStartDate) {
        Calendar calendar = isStartDate ? startDateCalendar : endDateCalendar;
        DatePickerDialog.OnDateSetListener listener = (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            updateDateButton(isStartDate);
        };

        new DatePickerDialog(this, listener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDateButton(boolean isStartDate) {
        Calendar calendar = isStartDate ? startDateCalendar : endDateCalendar;
        if (isStartDate) {
            binding.startDateButton.setText(dateFormat.format(calendar.getTime()));
        } else {
            binding.endDateButton.setText(dateFormat.format(calendar.getTime()));
        }
    }

    private void saveTournament() {
        String name = binding.nameEditText.getText().toString().trim();
        String category = binding.categoryAutoComplete.getText().toString().trim();
        String difficulty = binding.difficultyAutoComplete.getText().toString().trim();

        if (validateInput(name, category, difficulty)) {
            Tournament tournament = new Tournament();
            tournament.setName(name);
            tournament.setCategory(category);
            tournament.setDifficulty(difficulty);
            tournament.setStartDate(startDateCalendar.getTime());
            tournament.setEndDate(endDateCalendar.getTime());

            viewModel.createTournament(tournament);
            finish();
        }
    }

    private boolean validateInput(String name, String category, String difficulty) {
        boolean isValid = true;

        if (name.isEmpty()) {
            binding.nameLayout.setError("Name is required");
            isValid = false;
        } else {
            binding.nameLayout.setError(null);
        }

        if (category.isEmpty()) {
            binding.categoryLayout.setError("Category is required");
            isValid = false;
        } else {
            binding.categoryLayout.setError(null);
        }

        if (difficulty.isEmpty()) {
            binding.difficultyLayout.setError("Difficulty is required");
            isValid = false;
        } else {
            binding.difficultyLayout.setError(null);
        }

        if (startDateCalendar.getTime().after(endDateCalendar.getTime())) {
            Toast.makeText(this, "Start date must be before end date", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        return isValid;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 