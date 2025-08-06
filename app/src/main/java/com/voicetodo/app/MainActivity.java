package com.voicetodo.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initViews();
        initDatabase();
        setupRecyclerView();
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
                    addTodo(spokenText.trim());
                }
            }
        }
        
        tvStatus.setText(getString(R.string.hint_todo));
    }
    
    private void addTodo(String text) {
        Todo todo = new Todo(text);
        
        // Save to database in background thread
        new Thread(() -> {
            todoDao.insertTodo(todo);
            runOnUiThread(this::loadTodos);
        }).start();
        
        Toast.makeText(this, "Todo eklendi: " + text, Toast.LENGTH_SHORT).show();
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