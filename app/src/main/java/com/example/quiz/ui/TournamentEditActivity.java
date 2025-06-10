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
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class TournamentEditActivity extends AppCompatActivity {
    private ActivityTournamentEditBinding binding;
    private TournamentViewModel viewModel;
    private Calendar startDateCalendar;
    private Calendar endDateCalendar;
    private SimpleDateFormat dateFormat;
    private Tournament editingTournament;
    private String editingTournamentId = null;

    private static final String[] CATEGORY_NAMES = {"General Knowledge", "Science & Nature", "History", "Geography", "Sports"};
    private static final String[] CATEGORY_IDS = {"9", "17", "23", "22", "21"};
    private static final String[] DIFFICULTIES = {"Easy", "Medium", "Hard"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTournamentEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewModel = new ViewModelProvider(this).get(TournamentViewModel.class);

        // Check if editing
        editingTournamentId = getIntent().getStringExtra("tournamentId");
        if (editingTournamentId != null) {
            getSupportActionBar().setTitle("Edit Tournament");
            loadTournamentForEdit(editingTournamentId);
        } else {
            getSupportActionBar().setTitle("Create Tournament");
        }

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
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, CATEGORY_NAMES);
        binding.categoryAutoComplete.setAdapter(categoryAdapter);

        ArrayAdapter<String> difficultyAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, DIFFICULTIES);
        binding.difficultyAutoComplete.setAdapter(difficultyAdapter);
    }

    private void setupViews() {
        binding.saveButton.setOnClickListener(v -> saveTournament());
        binding.backButton.setOnClickListener(v -> onBackPressed());
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

    private void loadTournamentForEdit(String tournamentId) {
        FirebaseFirestore.getInstance().collection("tournaments").document(tournamentId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                Tournament t = documentSnapshot.toObject(Tournament.class);
                if (t != null) {
                    binding.nameEditText.setText(t.getName());
                    binding.categoryAutoComplete.setText(t.getCategory(), false);
                    binding.difficultyAutoComplete.setText(t.getDifficulty(), false);
                    startDateCalendar.setTime(t.getStartDate());
                    endDateCalendar.setTime(t.getEndDate());
                    updateDateButton(true);
                    updateDateButton(false);
                    editingTournament = t;
                }
            });
    }

    private void saveTournament() {
        String name = binding.nameEditText.getText().toString().trim();
        String categoryName = binding.categoryAutoComplete.getText().toString().trim();
        String difficulty = binding.difficultyAutoComplete.getText().toString().trim();
        String categoryId = "9"; // Default to General Knowledge
        for (int i = 0; i < CATEGORY_NAMES.length; i++) {
            if (CATEGORY_NAMES[i].equals(categoryName)) {
                categoryId = CATEGORY_IDS[i];
                break;
            }
        }

        if (validateInput(name, categoryName, difficulty)) {
            Tournament tournament = editingTournament != null ? editingTournament : new Tournament();
            if (editingTournament == null) {
                tournament.setTournamentId(UUID.randomUUID().toString());
            }
            tournament.setName(name);
            tournament.setCategory(categoryName);
            tournament.setDifficulty(difficulty);
            tournament.setStartDate(startDateCalendar.getTime());
            tournament.setEndDate(endDateCalendar.getTime());

            // Show loading
            binding.progressBar.setVisibility(android.view.View.VISIBLE);
            binding.saveButton.setEnabled(false);

            if (editingTournament != null) {
                // Just update the tournament (do not fetch new questions)
                viewModel.createTournament(tournament);
                showMessageAndFinish("Tournament updated successfully!");
            } else {
                // Fetch 10 questions from OpenTDB (omit type for mixed)
                viewModel.fetchQuestions(categoryId, difficulty.toLowerCase(), null);
                viewModel.getQuestions().observe(this, questions -> {
                    if (questions != null && !questions.isEmpty()) {
                        tournament.setQuestions(questions);
                        viewModel.createTournament(tournament);
                        showMessageAndFinish("Tournament created successfully!");
                    } else if (viewModel.getError().getValue() != null) {
                        showMessage("Failed to fetch questions: " + viewModel.getError().getValue());
                        binding.progressBar.setVisibility(android.view.View.GONE);
                        binding.saveButton.setEnabled(true);
                    }
                });
            }
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

    private void showMessage(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_LONG).show());
    }

    private void showMessageAndFinish(String message) {
        runOnUiThread(() -> {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            finish();
        });
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