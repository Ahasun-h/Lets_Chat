package com.example.letschat.network;

import com.example.letschat.notification.MyResponce;
import com.example.letschat.notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface  APIService {

    @Headers({"AAAAjCVRvFM:APA91bGc5N95Mtrl754Wb3nB4t64pn02zNMEIX7hQuNvUD4IAgZAQMv57G_QCCt7Jxx-rqngR2_L5iIi_LSi4KvM9KVKoTjB67IQtnA84cspEdDzqJ1zvqPvjqbJt512vrFosDRHg-8u",
            "Content-Type:application/json"})
    @POST("fcm/send")
    Call<MyResponce> sendNotification(@Body Sender body);

}
