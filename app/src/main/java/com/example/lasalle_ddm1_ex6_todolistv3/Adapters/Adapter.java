package com.example.lasalle_ddm1_ex6_todolistv3.Adapters;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lasalle_ddm1_ex6_todolistv3.API.ApiController;
import com.example.lasalle_ddm1_ex6_todolistv3.API.Client;
import com.example.lasalle_ddm1_ex6_todolistv3.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{
    private Client client;
    private ArrayList<ApiController> tasks;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public CheckBox checkBox;
        public ImageView crossImage, editImage;

        public ViewHolder(View view) {
            super(view);

            this.title = view.findViewById(R.id.title);
            this.checkBox = view.findViewById(R.id.checkbox);
            this.crossImage = view.findViewById(R.id.crossImage);
            this.editImage = view.findViewById(R.id.editImage);
        }
    }

    public Adapter(ArrayList<ApiController> tasks, Client apiClient) {
        this.tasks = tasks;
        this.client = apiClient;
    }

    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,
                parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
        final ApiController item = this.tasks.get(position);

        holder.checkBox.setChecked(item.isCompleted());
        holder.title.setText(item.getTitle());

        if(item.isCompleted()) {
            holder.title.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }

        holder.checkBox.setOnCheckedChangeListener((checkbox, value) -> {
            if(checkbox.isChecked()) {
                holder.title.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                holder.title.setPaintFlags(holder.title.getPaintFlags() &
                        (~ Paint.STRIKE_THRU_TEXT_FLAG));
            }

            item.setCompleted(value);

            this.client.updateTodo(item, new Callback<ApiController>() {
                @Override
                public void onResponse(Call<ApiController> call, Response<ApiController> response) {
                    Toast.makeText(holder.itemView.getContext(), "Todo updated to API",
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<ApiController> call, Throwable t) {
                    Toast.makeText(holder.itemView.getContext(), "ERROR: Cannot update TODO to API",
                            Toast.LENGTH_SHORT).show();
                }
            });
        });

        holder.crossImage.setOnClickListener(view -> {
            this.tasks.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, this.tasks.size());

            this.client.deleteTodo(item, new Callback<ApiController>() {
                @Override
                public void onResponse(Call<ApiController> call, Response<ApiController> response) {
                    Toast.makeText(holder.itemView.getContext(), "Todo deleted from API",
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<ApiController> call, Throwable t) {
                    Toast.makeText(holder.itemView.getContext(), "ERROR: Cannot delete TODO from API",
                            Toast.LENGTH_SHORT).show();
                }
            });
        });

        holder.editImage.setOnClickListener(view -> {
            this.showDialog(view, position);
        });
    }

    private void showDialog(View view, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle(R.string.create);

        final EditText input = new EditText(view.getContext());
        builder.setView(input);

        builder.setPositiveButton(R.string.edit, (dialog, which) -> {
            if(!input.getText().toString().isEmpty()) {
                ApiController task = this.tasks.get(position);
                task.setTitle(input.getText().toString());
                notifyDataSetChanged();

                this.client.updateTodo(task, new Callback<ApiController>() {
                    @Override
                    public void onResponse(Call<ApiController> call, Response<ApiController> response) {
                        Toast.makeText(view.getContext(), "Todo updated to API",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<ApiController> call, Throwable t) {
                        Toast.makeText(view.getContext(), "ERROR: Cannot update TODO to API",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                dialog.cancel();
            }
        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }

    @Override
    public int getItemCount() {
        return this.tasks.size();
    }
}
