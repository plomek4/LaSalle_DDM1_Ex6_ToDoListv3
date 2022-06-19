package com.example.lasalle_ddm1_ex6_todolistv3.Controller;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lasalle_ddm1_ex6_todolistv3.API.ApiController;
import com.example.lasalle_ddm1_ex6_todolistv3.API.Client;
import com.example.lasalle_ddm1_ex6_todolistv3.Adapters.Adapter;
import com.example.lasalle_ddm1_ex6_todolistv3.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton fab;
    private RecyclerView tasks;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private SharedPreferences sharedPrefs;
    private Client client;

    private ArrayList<ApiController> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.client = Client.getInstance();

        this.sharedPrefs = getSharedPreferences("TodoList",
                MODE_PRIVATE);

        if (!this.sharedPrefs.getString("Tasks", "").isEmpty()) {
            this.getTasksFromSharedPrefs();
        } else {
            this.getTasks();
        }

        this.fab = findViewById(R.id.button);
        this.fab.setOnClickListener(view -> this.showCreateTaskDialog());

        this.tasks = findViewById(R.id.recycle);
        this.layoutManager = new LinearLayoutManager(this);
        this.tasks.setLayoutManager(layoutManager);

        this.adapter = new Adapter(this.list, this.client);
        this.tasks.setAdapter(this.adapter);
    }

    private void showCreateTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.create);

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton(R.string.save, (dialog, which) -> {
            if (!input.getText().toString().isEmpty()) {
                ApiController todo = new ApiController(input.getText().toString());
                this.list.add(todo);
                this.client.addTodo(todo, new Callback<ApiController>() {
                    @Override
                    public void onResponse(Call<ApiController> call, Response<ApiController> response) {
                        Toast.makeText(getApplicationContext(), "Todo added to API",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<ApiController> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "ERROR: Cannot add TODO to the API",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void getTasksFromSharedPrefs() {
        String json = this.sharedPrefs.getString("Tasks", "");
        Type listType = new TypeToken<ArrayList<ApiController>>() {
        }.getType();

        this.list.clear();
        this.list = new Gson().fromJson(json, listType);
    }

    private void getTasks() {
        this.client.getTodos(new Callback<List<ApiController>>() {
            @Override
            public void onResponse(Call<List<ApiController>> call, Response<List<ApiController>> response) {
                if (response.body() != null) {
                    list.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<ApiController>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "ERROR: Cannot get TODOs from the API",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = this.sharedPrefs.edit();

        String json = new Gson().toJson(this.list);

        editor.remove("Tasks");
        editor.putString("Tasks", json);
        editor.apply();
    }
}