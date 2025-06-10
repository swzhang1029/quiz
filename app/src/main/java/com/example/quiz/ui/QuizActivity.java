package com.example.quiz.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.quiz.databinding.ActivityQuizBinding;
import com.example.quiz.models.Question;
import com.example.quiz.models.Tournament;
import com.example.quiz.viewmodels.QuizViewModel;
import java.util.List;

public class QuizActivity extends AppCompatActivity {
    private ActivityQuizBinding binding;
    private QuizViewModel quizViewModel;
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        quizViewModel = new ViewModelProvider(this).get(QuizViewModel.class);

        Tournament tournament = (Tournament) getIntent().getSerializableExtra("tournament");
        if (tournament == null || tournament.getQuestions() == null) {
            Toast.makeText(this, "No questions available.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        Toast.makeText(this, "QuizActivity started for: " + tournament.getName(), Toast.LENGTH_SHORT).show();
        questions = tournament.getQuestions();
        showQuestion();

        binding.nextButton.setOnClickListener(v -> {
            if (binding.answersRadioGroup.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this, "Please select an answer.", Toast.LENGTH_SHORT).show();
                return;
            }
            checkAnswer();
        });
    }

    private void showQuestion() {
        if (currentQuestionIndex < questions.size()) {
            Question q = questions.get(currentQuestionIndex);
            binding.questionNumberTextView.setText("Question " + (currentQuestionIndex + 1) + " of " + questions.size());
            binding.questionTextView.setText(q.getQuestion());
            binding.answersRadioGroup.clearCheck();
            binding.feedbackTextView.setVisibility(View.GONE);
            binding.nextButton.setText("Submit");
            binding.nextButton.setOnClickListener(v -> checkAnswer());
            // Set answers
            List<String> allAnswers = new java.util.ArrayList<>(q.getIncorrectAnswers());
            allAnswers.add(q.getCorrectAnswer());
            java.util.Collections.shuffle(allAnswers);
            binding.answer1RadioButton.setText(allAnswers.get(0));
            binding.answer2RadioButton.setText(allAnswers.get(1));
            if (allAnswers.size() > 2) {
                binding.answer3RadioButton.setVisibility(View.VISIBLE);
                binding.answer3RadioButton.setText(allAnswers.get(2));
            } else {
                binding.answer3RadioButton.setVisibility(View.GONE);
            }
            if (allAnswers.size() > 3) {
                binding.answer4RadioButton.setVisibility(View.VISIBLE);
                binding.answer4RadioButton.setText(allAnswers.get(3));
            } else {
                binding.answer4RadioButton.setVisibility(View.GONE);
            }
        } else {
            showScore();
        }
    }

    private void checkAnswer() {
        Question q = questions.get(currentQuestionIndex);
        int checkedId = binding.answersRadioGroup.getCheckedRadioButtonId();
        String selectedAnswer = null;
        if (checkedId == binding.answer1RadioButton.getId()) selectedAnswer = binding.answer1RadioButton.getText().toString();
        else if (checkedId == binding.answer2RadioButton.getId()) selectedAnswer = binding.answer2RadioButton.getText().toString();
        else if (checkedId == binding.answer3RadioButton.getId()) selectedAnswer = binding.answer3RadioButton.getText().toString();
        else if (checkedId == binding.answer4RadioButton.getId()) selectedAnswer = binding.answer4RadioButton.getText().toString();

        if (selectedAnswer != null && selectedAnswer.equals(q.getCorrectAnswer())) {
            score++;
            binding.feedbackTextView.setText("Correct!");
        } else {
            binding.feedbackTextView.setText("Incorrect! Correct answer: " + q.getCorrectAnswer());
        }
        binding.feedbackTextView.setVisibility(View.VISIBLE);
        binding.nextButton.setText(currentQuestionIndex == questions.size() - 1 ? "Finish" : "Next");
        binding.nextButton.setOnClickListener(v -> {
            currentQuestionIndex++;
            showQuestion();
        });
    }

    private void showScore() {
        binding.questionNumberTextView.setVisibility(View.GONE);
        binding.questionTextView.setVisibility(View.GONE);
        binding.answersRadioGroup.setVisibility(View.GONE);
        binding.feedbackTextView.setVisibility(View.GONE);
        binding.nextButton.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.GONE);
        binding.feedbackTextView.setVisibility(View.GONE);
        binding.questionTextView.setVisibility(View.VISIBLE);
        binding.questionTextView.setText("Quiz complete! Your score: " + score + "/" + questions.size());

        // Show back button
        android.widget.Button backButton = new android.widget.Button(this);
        backButton.setText("Back");
        backButton.setOnClickListener(v -> finish());
        ((androidx.constraintlayout.widget.ConstraintLayout) binding.getRoot()).addView(backButton);
        // Set layout params for the button
        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams params = new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_PARENT,
                androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT);
        params.bottomToBottom = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        params.leftToLeft = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        params.rightToRight = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        params.topMargin = 32;
        backButton.setLayoutParams(params);
    }
} 