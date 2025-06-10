package com.example.quiz.repository;

import com.example.quiz.api.OpenTDBService;
import com.example.quiz.api.RetrofitClient;
import com.example.quiz.models.QuestionResponse;
import com.example.quiz.models.Tournament;
import com.example.quiz.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;

public class QuizRepository {
    private static QuizRepository instance;
    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firestore;
    private final OpenTDBService openTDBService;

    private QuizRepository() {
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        openTDBService = RetrofitClient.getInstance().getOpenTDBService();
    }

    public static synchronized QuizRepository getInstance() {
        if (instance == null) {
            instance = new QuizRepository();
        }
        return instance;
    }

    // User Authentication Methods
    public void signIn(String email, String password, OnAuthCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = task.getResult().getUser().getUid();
                        getUserData(userId, callback);
                    } else {
                        callback.onError(task.getException());
                    }
                });
    }

    private void getUserData(String userId, OnAuthCallback callback) {
        firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    callback.onSuccess(user);
                })
                .addOnFailureListener(callback::onError);
    }

    // Tournament Methods
    public void createTournament(Tournament tournament, OnTournamentCallback callback) {
        firestore.collection("tournaments")
                .document(tournament.getTournamentId())
                .set(tournament)
                .addOnSuccessListener(aVoid -> callback.onSuccess(tournament))
                .addOnFailureListener(callback::onError);
    }

    public void deleteTournament(String tournamentId, OnTournamentCallback callback) {
        firestore.collection("tournaments")
                .document(tournamentId)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onError);
    }

    public void getTournaments(OnTournamentsCallback callback) {
        firestore.collection("tournaments")
                .orderBy("startDate", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Tournament> tournaments = queryDocumentSnapshots.toObjects(Tournament.class);
                    callback.onSuccess(tournaments);
                })
                .addOnFailureListener(callback::onError);
    }

    public void likeTournament(String tournamentId, String userId, OnTournamentCallback callback) {
        firestore.collection("tournaments")
                .document(tournamentId)
                .update("likes", com.google.firebase.firestore.FieldValue.arrayUnion(userId))
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onError);
    }

    public void unlikeTournament(String tournamentId, String userId, OnTournamentCallback callback) {
        firestore.collection("tournaments")
                .document(tournamentId)
                .update("likes", com.google.firebase.firestore.FieldValue.arrayRemove(userId))
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onError);
    }

    // OpenTDB API Methods
    public void fetchQuestions(int amount, String category, String difficulty, String type,
                             Callback<QuestionResponse> callback) {
        openTDBService.getQuestions(amount, category, difficulty, type)
                .enqueue(callback);
    }

    // Callback Interfaces
    public interface OnAuthCallback {
        void onSuccess(User user);
        void onError(Exception e);
    }

    public interface OnTournamentCallback {
        void onSuccess(Tournament tournament);
        void onError(Exception e);
    }

    public interface OnTournamentsCallback {
        void onSuccess(List<Tournament> tournaments);
        void onError(Exception e);
    }
} 