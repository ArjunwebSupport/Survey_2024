package com.gosurveyrastra.survey.service;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

public abstract interface UploadService {


    @Multipart
    @POST("/PollAPI/PostAduio")
    public abstract void upload(@Part("file") TypedFile paramTypedFile,@Query("FormName") String FormName,
                                @Query("FormRegisterId") int FormRegisterId, Callback<Response> paramCallback);

    @Multipart
    @POST("/PollAPI/PostAduio")
    public abstract Response uploadSync(@Part("file") TypedFile paramTypedFile);


}