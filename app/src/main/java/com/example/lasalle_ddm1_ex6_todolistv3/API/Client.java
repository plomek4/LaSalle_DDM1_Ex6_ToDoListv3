package com.example.lasalle_ddm1_ex6_todolistv3.API;

import java.util.List;

import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {
    private static Client saved;
    private Retrofit retrofit;
    private placeHolderApi service;

    public static Client getInstance() {
        if(saved == null) {
            saved = new Client();
        }
        return saved;
    }

    public Client() {
        this.retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.service = this.retrofit.create(placeHolderApi.class);
    }

    public void getTodos(Callback<List<ApiController>> callback) {
        this.service.getTodos().enqueue(callback);
    }

    public void addTodo(ApiController todo, Callback<ApiController> callback) {
        this.service.addTodo(todo).enqueue(callback);
    }

    public void updateTodo(ApiController todo, Callback<ApiController> callback) {
        this.service.updateTodo(todo.getId(), todo).enqueue(callback);
    }

    public void deleteTodo(ApiController todo, Callback<ApiController> callback) {
        this.service.deleteTodo(todo.getId()).enqueue(callback);
    }
}
