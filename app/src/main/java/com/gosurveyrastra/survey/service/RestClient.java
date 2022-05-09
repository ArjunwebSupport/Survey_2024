package com.gosurveyrastra.survey.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public class RestClient {
    private UploadService  uploadService;
    private String URL ="https://prosurvey.in/API/";


    public RestClient(){
        Gson localGson = new GsonBuilder().create();

        this.uploadService = ((UploadService)new RestAdapter.Builder()
                .setEndpoint(URL)
                .setConverter(new GsonConverter(localGson))
                .build().create(UploadService.class));

    }



    public UploadService getService()
    {
        return this.uploadService;
    }


}
