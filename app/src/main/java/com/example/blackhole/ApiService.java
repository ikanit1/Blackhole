package com.example.blackhole;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @GET("api/validate_connection")
    Call<Boolean> validateConnection(
            @Query("ip") String ip,
            @Query("port") String port,
            @Query("username") String username,
            @Query("password") String password
    );

    // Метод для сохранения уведомления на сервере
    @FormUrlEncoded
    @POST("api/save_notification")
    Call<Void> saveNotification(
            @Field("packageName") String packageName,
            @Field("title") String title,
            @Field("text") String text,
            @Field("postTime") long postTime
    );

    // Метод для отправки SMS (можно оставить без изменений)
    @POST("api/message")
    Call<Void> sendMessage(@Body MessageData messageData);
}
