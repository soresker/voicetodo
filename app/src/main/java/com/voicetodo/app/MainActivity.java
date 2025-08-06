package com.voicetodo.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TodoAdapter.OnTodoListener {
    
    private static final int REQUEST_SPEECH_INPUT = 1000;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    
    private FloatingActionButton fabVoice;
    private TextView tvStatus;
    private TextView tvEmpty;
    private RecyclerView rvTodos;
    private TodoAdapter todoAdapter;
    
    private TodoDatabase database;
    private TodoDao todoDao;
    private Vibrator vibrator;
    private Animation pulseAnimation;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initViews();
        initDatabase();
        setupRecyclerView();
        initAnimations();
        checkPermissions();
        loadTodos();
    }
    
    private void initViews() {
        fabVoice = findViewById(R.id.fab_voice);
        tvStatus = findViewById(R.id.tv_status);
        tvEmpty = findViewById(R.id.tv_empty);
        rvTodos = findViewById(R.id.rv_todos);
        
        fabVoice.setOnClickListener(v -> startVoiceInput());
    }
    
    private void initDatabase() {
        database = TodoDatabase.getInstance(this);
        todoDao = database.todoDao();
    }
    
    private void setupRecyclerView() {
        todoAdapter = new TodoAdapter(this);
        rvTodos.setLayoutManager(new LinearLayoutManager(this));
        rvTodos.setAdapter(todoAdapter);
        
        // Setup swipe actions
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToActionCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    handleSwipeAction(position, direction);
                }
            }
        });
        itemTouchHelper.attachToRecyclerView(rvTodos);
    }
    
    private void initAnimations() {
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse_animation);
    }
    
    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) 
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, 
                    new String[]{Manifest.permission.RECORD_AUDIO}, 
                    REQUEST_RECORD_AUDIO_PERMISSION);
        }
    }
    
    private void startVoiceInput() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) 
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this, getString(R.string.speech_not_available), Toast.LENGTH_SHORT).show();
            return;
        }
        
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));
        
        try {
            startActivityForResult(intent, REQUEST_SPEECH_INPUT);
            tvStatus.setText(getString(R.string.btn_listening));
            
            // Start pulse animation and haptic feedback
            fabVoice.startAnimation(pulseAnimation);
            if (vibrator != null && vibrator.hasVibrator()) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(100);
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.error_speech), Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) {
                String spokenText = results.get(0);
                if (!spokenText.trim().isEmpty()) {
                    processVoiceCommand(spokenText.trim());
                }
            }
        }
        
        // Stop animation and reset status
        fabVoice.clearAnimation();
        tvStatus.setText(getString(R.string.hint_todo));
    }
    
    private void processVoiceCommand(String spokenText) {
        String command = spokenText.toLowerCase().trim();
        
        // Voice commands for todo operations
        if (command.contains("sil") || command.contains("silme")) {
            deleteLastTodo();
        } else if (command.contains("tamamla") || command.contains("bitir") || command.contains("tamam")) {
            completeLastTodo();
        } else if (command.contains("temizle") || command.contains("temizlik")) {
            clearCompletedTodos();
        } else if (command.contains("say") || command.contains("kaç tane") || command.contains("adet")) {
            announceStats();
        } else if (command.contains("oku") || command.contains("listele")) {
            readTodos();
        } else {
            // Normal todo ekleme
            addTodo(spokenText);
        }
    }
    
    private void addTodo(String text) {
        Todo todo = new Todo(text);
        
        // Save to database in background thread
        new Thread(() -> {
            todoDao.insertTodo(todo);
            runOnUiThread(this::loadTodos);
        }).start();
        
        Toast.makeText(this, "Todo eklendi: " + text, Toast.LENGTH_SHORT).show();
        triggerHapticFeedback();
    }
    
    private void deleteLastTodo() {
        new Thread(() -> {
            List<Todo> todos = todoDao.getAllTodos();
            if (!todos.isEmpty()) {
                Todo lastTodo = todos.get(todos.size() - 1);
                todoDao.deleteTodo(lastTodo);
                runOnUiThread(() -> {
                    loadTodos();
                    Toast.makeText(this, "Son todo silindi: " + lastTodo.getText(), Toast.LENGTH_SHORT).show();
                    triggerHapticFeedback();
                });
            } else {
                runOnUiThread(() -> 
                    Toast.makeText(this, "Silinecek todo bulunamadı", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
    
    private void completeLastTodo() {
        new Thread(() -> {
            List<Todo> todos = todoDao.getAllTodos();
            if (!todos.isEmpty()) {
                Todo lastTodo = todos.get(todos.size() - 1);
                if (!lastTodo.isDone()) {
                    lastTodo.setDone(true);
                    todoDao.updateTodo(lastTodo);
                    runOnUiThread(() -> {
                        loadTodos();
                        Toast.makeText(this, "Todo tamamlandı: " + lastTodo.getText(), Toast.LENGTH_SHORT).show();
                        triggerHapticFeedback();
                    });
                } else {
                    runOnUiThread(() -> 
                        Toast.makeText(this, "Son todo zaten tamamlanmış", Toast.LENGTH_SHORT).show()
                    );
                }
            } else {
                runOnUiThread(() -> 
                    Toast.makeText(this, "Tamamlanacak todo bulunamadı", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
    
    private void clearCompletedTodos() {
        new Thread(() -> {
            List<Todo> todos = todoDao.getAllTodos();
            int deletedCount = 0;
            for (Todo todo : todos) {
                if (todo.isDone()) {
                    todoDao.deleteTodo(todo);
                    deletedCount++;
                }
            }
            final int finalCount = deletedCount;
            runOnUiThread(() -> {
                loadTodos();
                Toast.makeText(this, finalCount + " tamamlanan todo temizlendi", Toast.LENGTH_SHORT).show();
                triggerHapticFeedback();
            });
        }).start();
    }
    
    private void announceStats() {
        new Thread(() -> {
            List<Todo> todos = todoDao.getAllTodos();
            int totalCount = todos.size();
            int completedCount = 0;
            for (Todo todo : todos) {
                if (todo.isDone()) {
                    completedCount++;
                }
            }
            final int finalCompletedCount = completedCount;
            final int pendingCount = totalCount - completedCount;
            
            runOnUiThread(() -> {
                String message = "Toplam " + totalCount + " todo var. " + 
                               finalCompletedCount + " tamamlandı, " + 
                               pendingCount + " bekliyor.";
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            });
        }).start();
    }
    
    private void readTodos() {
        new Thread(() -> {
            List<Todo> todos = todoDao.getAllTodos();
            if (todos.isEmpty()) {
                runOnUiThread(() -> 
                    Toast.makeText(this, "Todo listeniz boş", Toast.LENGTH_SHORT).show()
                );
            } else {
                StringBuilder todoList = new StringBuilder("Todo'larınız:\n");
                for (int i = 0; i < Math.min(todos.size(), 5); i++) {
                    Todo todo = todos.get(i);
                    todoList.append((i + 1)).append(". ").append(todo.getText());
                    if (todo.isDone()) {
                        todoList.append(" (Tamamlandı)");
                    }
                    todoList.append("\n");
                }
                if (todos.size() > 5) {
                    todoList.append("... ve ").append(todos.size() - 5).append(" tane daha");
                }
                
                runOnUiThread(() -> 
                    Toast.makeText(this, todoList.toString(), Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }
    
    private void triggerHapticFeedback() {
        if (vibrator != null && vibrator.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(50);
            }
        }
    }
    
    private void handleSwipeAction(int position, int direction) {
        new Thread(() -> {
            List<Todo> todos = todoDao.getAllTodos();
            if (position < todos.size()) {
                Todo todo = todos.get(position);
                
                if (direction == ItemTouchHelper.RIGHT) {
                    // Swipe right - Complete/Uncomplete
                    todo.setDone(!todo.isDone());
                    todoDao.updateTodo(todo);
                    runOnUiThread(() -> {
                        loadTodos();
                        String message = todo.isDone() ? "Tamamlandı: " : "Tamamlanmadı: ";
                        Toast.makeText(this, message + todo.getText(), Toast.LENGTH_SHORT).show();
                        triggerHapticFeedback();
                    });
                } else if (direction == ItemTouchHelper.LEFT) {
                    // Swipe left - Delete
                    todoDao.deleteTodo(todo);
                    runOnUiThread(() -> {
                        loadTodos();
                        Toast.makeText(this, "Silindi: " + todo.getText(), Toast.LENGTH_SHORT).show();
                        triggerHapticFeedback();
                    });
                }
            }
        }).start();
    }
    
    private void loadTodos() {
        new Thread(() -> {
            List<Todo> todos = todoDao.getAllTodos();
            runOnUiThread(() -> {
                todoAdapter.setTodos(todos);
                updateEmptyView(todos.isEmpty());
            });
        }).start();
    }
    
    private void updateEmptyView(boolean isEmpty) {
        if (isEmpty) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvTodos.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvTodos.setVisibility(View.VISIBLE);
        }
    }
    
    @Override
    public void onTodoChecked(Todo todo, boolean isChecked) {
        todo.setDone(isChecked);
        new Thread(() -> {
            todoDao.updateTodo(todo);
            runOnUiThread(this::loadTodos);
        }).start();
    }
    
    @Override
    public void onTodoDeleted(Todo todo) {
        new Thread(() -> {
            todoDao.deleteTodo(todo);
            runOnUiThread(this::loadTodos);
        }).start();
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Mikrofon izni verildi", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_LONG).show();
            }
        }
    }
}