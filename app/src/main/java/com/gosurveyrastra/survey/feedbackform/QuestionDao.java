package com.gosurveyrastra.survey.feedbackform;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.gosurveyrastra.survey.QuestionEntity;

import java.util.List;

@Dao
public interface QuestionDao
{
    @Insert
    void insertAllQuestions(List<QuestionEntity> questions);

    @Query("SELECT * FROM questions")
    List<QuestionEntity> getAllQuestions();

    @Query("DELETE FROM questions")
    void deleteAllQuestions();
}
