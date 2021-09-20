package com.gosurveyrastra.survey.service;

public class Surverys {

    public Surverys(int id,String questionid, String question_type_name, String question_name, String answer, String surveyids, String salary,String formname,String currentDateandTime) {
        this.questionid = questionid;
        this.question_type_name = question_type_name;
        this.question_name = question_name;
        this.answer = answer;
        this.surveyids = surveyids;
        this.salary = salary;
        this.id = id;
        this.formname = formname;
        this.currentDateandTime = currentDateandTime;
    }

    public String getFormname() {
        return formname;
    }

    public void setFormname(String formname) {
        this.formname = formname;
    }

    String formname;

    public String getCurrentDateandTime() {
        return currentDateandTime;
    }

    public void setCurrentDateandTime(String currentDateandTime) {
        this.currentDateandTime = currentDateandTime;
    }

    String currentDateandTime;
    public String getQuestionid() {
        return questionid;
    }

    public void setQuestionid(String questionid) {
        this.questionid = questionid;
    }

    public String getQuestion_type_name() {
        return question_type_name;
    }

    public void setQuestion_type_name(String question_type_name) {
        this.question_type_name = question_type_name;
    }
 
    public String getQuestion_name() {
        return question_name;
    }

    public void setQuestion_name(String question_name) {
        this.question_name = question_name;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getSurveyids() {
        return surveyids;
    }

    public void setSurveyids(String surveyids) {
        this.surveyids = surveyids;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    String questionid,question_type_name,question_name,answer,surveyids,salary;
    int id;
}
