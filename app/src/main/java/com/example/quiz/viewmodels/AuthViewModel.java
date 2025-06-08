package com.example.quiz.viewmodels;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.quiz.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AuthViewModel extends ViewModel {
    private static final String TAG = "AuthViewModel";
    private final FirebaseAuth auth;
    private final FirebaseFirestore db;
    private final MutableLiveData<User> currentUser;
    private final MutableLiveData<Boolean> isLoading;
    private final MutableLiveData<String> error;

    public AuthViewModel() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = new MutableLiveData<>();
        isLoading = new MutableLiveData<>(false);
        error = new MutableLiveData<>();

        // Check if user is already signed in
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null) {
            loadUserData(firebaseUser.getUid());
        }
    }

    public void signIn(String email, String password) {
        isLoading.setValue(true);
        error.setValue(null);

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = task.getResult().getUser();
                    if (firebaseUser != null) {
                        loadUserData(firebaseUser.getUid());
                    }
                } else {
                    Log.e(TAG, "Sign in failed", task.getException());
                    error.setValue(task.getException() != null ? 
                        task.getException().getMessage() : "Authentication failed");
                }
                isLoading.setValue(false);
            });
    }

    private void loadUserData(String userId) {
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                User user = documentSnapshot.toObject(User.class);
                if (user != null) {
                    currentUser.setValue(user);
                } else {
                    error.setValue("User data not found");
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error loading user data", e);
                error.setValue("Failed to load user data");
            });
    }

    public void signOut() {
        auth.signOut();
        currentUser.setValue(null);
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }
} 