package com.voicetodo.app;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
    private boolean animateItems = true;
    
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
        
        // Animate item only for new items
        if (animateItems && position == todos.size() - 1) {
            Animation slideIn = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.slide_in_bottom);
            holder.itemView.startAnimation(slideIn);
        }
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
                // Animate deletion
                Animation slideOut = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.slide_out_right);
                slideOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}
                    
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (listener != null) {
                            listener.onTodoDeleted(todo);
                        }
                    }
                    
                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                itemView.startAnimation(slideOut);
            });
        }
    }
}