package com.example.quiz.api;

import com.example.quiz.models.QuestionResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenTDBService {
    @GET("api.php")
    Call<QuestionResponse> getQuestions(
        @Query("amount") int amount,
        @Query("category") String category,
        @Query("difficulty") String difficulty,
        @Query("type") String type
    );
} 