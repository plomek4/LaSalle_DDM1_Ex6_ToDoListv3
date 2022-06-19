package com.example.lasalle_ddm1_ex6_todolistv3.API;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface placeHolderApi {
    @GET("todos")
    Call<List<ApiController>> getTodos();

    @POST("todos")
    Call<ApiController> addTodo(@Body ApiController todo);

    @PATCH("todos/{id}")
    Call<ApiController> updateTodo(@Path("id") Integer id, @Body ApiController todo);

    @DELETE("todos/{id}")
    Call<ApiController> deleteTodo(@Path("id") Integer id);
}
