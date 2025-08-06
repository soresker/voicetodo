package com.voicetodo.app;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {
    
    private List<Todo> todos = new ArrayList<>();
    private OnTodoListener listener;
    
    public interface OnTodoListener {
        void onTodoChecked(Todo todo, boolean isChecked);
        void onTodoDeleted(Todo todo);
    }
    
    public TodoAdapter(OnTodoListener listener) {
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_todo, parent, false);
        return new TodoViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        Todo todo = todos.get(position);
        holder.bind(todo);
    }
    
    @Override
    public int getItemCount() {
        return todos.size();
    }
    
    public void setTodos(List<Todo> todos) {
        this.todos = todos;
        notifyDataSetChanged();
    }
    
    class TodoViewHolder extends RecyclerView.ViewHolder {
        
        private TextView tvTodoText;
        private TextView tvTimestamp;
        private CheckBox cbDone;
        private ImageButton btnDelete;
        
        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTodoText = itemView.findViewById(R.id.tv_todo_text);
            tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
            cbDone = itemView.findViewById(R.id.cb_done);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
        
        public void bind(Todo todo) {
            tvTodoText.setText(todo.getText());
            
            // Format timestamp
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            tvTimestamp.setText(sdf.format(new Date(todo.getTimestamp())));
            
            // Set checkbox state
            cbDone.setChecked(todo.isDone());
            
            // Apply strikethrough if done
            if (todo.isDone()) {
                tvTodoText.setPaintFlags(tvTodoText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tvTodoText.setAlpha(0.6f);
            } else {
                tvTodoText.setPaintFlags(tvTodoText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                tvTodoText.setAlpha(1.0f);
            }
            
            // Set listeners
            cbDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    listener.onTodoChecked(todo, isChecked);
                }
            });
            
            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTodoDeleted(todo);
                }
            });
        }
    }
}