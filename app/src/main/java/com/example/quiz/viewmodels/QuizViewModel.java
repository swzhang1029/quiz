package com.example.quiz.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.quiz.models.Question;
import java.util.List;

public class QuizViewModel extends BaseViewModel {
    private final MutableLiveData<List<Question>> questions = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentQuestionIndex = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> score = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> isQuizComplete = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> showFeedback = new MutableLiveData<>(false);

    public LiveData<List<Question>> getQuestions() {
        return questions;
    }

    public LiveData<Integer> getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public LiveData<Integer> getScore() {
        return score;
    }

    public LiveData<Boolean> getIsQuizComplete() {
        return isQuizComplete;
    }

    public LiveData<Boolean> getShowFeedback() {
        return showFeedback;
    }

    public void setQuestions(List<Question> questionList) {
        questions.setValue(questionList);
        currentQuestionIndex.setValue(0);
        score.setValue(0);
        isQuizComplete.setValue(false);
    }

    public Question getCurrentQuestion() {
        List<Question> questionList = questions.getValue();
        if (questionList != null && !questionList.isEmpty()) {
            int index = currentQuestionIndex.getValue();
            if (index >= 0 && index < questionList.size()) {
                return questionList.get(index);
            }
        }
        return null;
    }

    public void answerQuestion(String answer) {
        Question currentQuestion = getCurrentQuestion();
        if (currentQuestion != null) {
            currentQuestion.setUserAnswer(answer);
            currentQuestion.setAnswered(true);
            
            if (currentQuestion.isCorrect()) {
                score.setValue(score.getValue() + 1);
            }
            
            showFeedback.setValue(true);
        }
    }

    public void nextQuestion() {
        List<Question> questionList = questions.getValue();
        if (questionList != null) {
            int nextIndex = currentQuestionIndex.getValue() + 1;
            if (nextIndex < questionList.size()) {
                currentQuestionIndex.setValue(nextIndex);
                showFeedback.setValue(false);
            } else {
                isQuizComplete.setValue(true);
            }
        }
    }

    public void resetQuiz() {
        currentQuestionIndex.setValue(0);
        score.setValue(0);
        isQuizComplete.setValue(false);
        showFeedback.setValue(false);
        
        List<Question> questionList = questions.getValue();
        if (questionList != null) {
            for (Question question : questionList) {
                question.setAnswered(false);
                question.setUserAnswer(null);
            }
        }
    }
} 