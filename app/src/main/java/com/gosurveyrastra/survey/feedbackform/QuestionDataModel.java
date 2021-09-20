package com.gosurveyrastra.survey.feedbackform;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QuestionDataModel
{
    @SerializedName("lstRegisterFields")
    private List<QuestionsItem> questions;

    public List<QuestionsItem> getQuestions()
    {
        return questions;
    }

    public void setQuestions(List<QuestionsItem> questions)
    {
        this.questions = questions;
    }

    @SerializedName("msg")
    private String message;

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}