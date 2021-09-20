package com.gosurveyrastra.survey.ui;

public class DashboardUsersModel {

    String LastName, Date;
    int UserId, NoofUserCount;

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public int getNoofUserCount() {
        return NoofUserCount;
    }

    public void setNoofUserCount(int noofUserCount) {
        NoofUserCount = noofUserCount;
    }
}
