package com.example.quiz.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.quiz.models.User;
import com.example.quiz.repository.QuizRepository;

public class AuthViewModel extends BaseViewModel {
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void signIn(String email, String password) {
        isLoading.setValue(true);
        error.setValue(null);

        repository.signIn(email, password, new QuizRepository.OnAuthCallback() {
            @Override
            public void onSuccess(User user) {
                currentUser.postValue(user);
                isLoading.postValue(false);
            }

            @Override
            public void onError(Exception e) {
                error.postValue(e.getMessage());
                isLoading.postValue(false);
            }
        });
    }

    public void signOut() {
        currentUser.setValue(null);
    }
} 