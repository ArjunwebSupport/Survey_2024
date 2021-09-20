package com.gosurveyrastra.survey.ui;

public class SurveyListModel {

    String FormId;

    public String getChecktruefalse() {
        return checktruefalse;
    }

    public void setChecktruefalse(String checktruefalse) {
        this.checktruefalse = checktruefalse;
    }

    String checktruefalse;

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getNoofRegistrations() {
        return NoofRegistrations;
    }

    public void setNoofRegistrations(String noofRegistrations) {
        NoofRegistrations = noofRegistrations;
    }

    String UserId,NoofRegistrations;

    public String getFormId() {
        return FormId;
    }

    public void setFormId(String formId) {
        FormId = formId;
    }

    public String getFormName() {
        return FormName;
    }

    public void setFormName(String formName) {
        FormName = formName;
    }

    public String getStartDate() {
        return StartDate;
    }

    public void setStartDate(String startDate) {
        StartDate = startDate;
    }

    public String getEndDate() {
        return EndDate;
    }

    public void setEndDate(String endDate) {
        EndDate = endDate;
    }

    public String getBannerUrl() {
        return BannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        BannerUrl = bannerUrl;
    }

    public String getContactEmail() {
        return ContactEmail;
    }

    public void setContactEmail(String contactEmail) {
        ContactEmail = contactEmail;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getStateName() {
        return StateName;
    }

    public void setStateName(String stateName) {
        StateName = stateName;
    }

    public String getCountryName() {
        return CountryName;
    }

    public void setCountryName(String countryName) {
        CountryName = countryName;
    }

    public String getIsFormRegistration() {
        return IsFormRegistration;
    }

    public void setIsFormRegistration(String isFormRegistration) {
        IsFormRegistration = isFormRegistration;
    }

    public String getIsActive() {
        return IsActive;
    }

    public void setIsActive(String isActive) {
        IsActive = isActive;
    }

    String FormName;
    String StartDate;
    String EndDate;
    String BannerUrl;
    String ContactEmail;
    String Address;
    String City;
    String StateName;
    String CountryName;
    String IsFormRegistration;
    String IsActive;


}
