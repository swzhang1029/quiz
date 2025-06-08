package com.example.quiz.viewmodels;

import androidx.lifecycle.ViewModel;
import com.example.quiz.repository.QuizRepository;

public abstract class BaseViewModel extends ViewModel {
    protected final QuizRepository repository;

    public BaseViewModel() {
        repository = QuizRepository.getInstance();
    }
} 