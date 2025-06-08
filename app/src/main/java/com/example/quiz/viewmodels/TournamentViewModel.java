package com.example.quiz.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.quiz.models.Question;
import com.example.quiz.models.QuestionResponse;
import com.example.quiz.models.Tournament;
import com.example.quiz.repository.QuizRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class TournamentViewModel extends BaseViewModel {
    private final MutableLiveData<List<Tournament>> tournaments = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<List<Question>> questions = new MutableLiveData<>();

    public LiveData<List<Tournament>> getTournaments() {
        return tournaments;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<List<Question>> getQuestions() {
        return questions;
    }

    public void loadTournaments() {
        isLoading.setValue(true);
        error.setValue(null);

        repository.getTournaments(new QuizRepository.OnTournamentsCallback() {
            @Override
            public void onSuccess(List<Tournament> tournamentList) {
                tournaments.postValue(tournamentList);
                isLoading.postValue(false);
            }

            @Override
            public void onError(Exception e) {
                error.postValue(e.getMessage());
                isLoading.postValue(false);
            }
        });
    }

    public void createTournament(Tournament tournament) {
        isLoading.setValue(true);
        error.setValue(null);

        repository.createTournament(tournament, new QuizRepository.OnTournamentCallback() {
            @Override
            public void onSuccess(Tournament createdTournament) {
                loadTournaments();
            }

            @Override
            public void onError(Exception e) {
                error.postValue(e.getMessage());
                isLoading.postValue(false);
            }
        });
    }

    public void fetchQuestions(String category, String difficulty, String type) {
        isLoading.setValue(true);
        error.setValue(null);

        repository.fetchQuestions(10, category, difficulty, type, new Callback<QuestionResponse>() {
            @Override
            public void onResponse(Call<QuestionResponse> call, Response<QuestionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    questions.postValue(response.body().getQuestions());
                } else {
                    error.postValue("Failed to fetch questions");
                }
                isLoading.postValue(false);
            }

            @Override
            public void onFailure(Call<QuestionResponse> call, Throwable t) {
                error.postValue(t.getMessage());
                isLoading.postValue(false);
            }
        });
    }
} 