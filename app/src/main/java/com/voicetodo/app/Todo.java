package com.voicetodo.app;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "todos")
public class Todo {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String text;
    private boolean isDone;
    private long timestamp;
    
    public Todo() {
        this.timestamp = System.currentTimeMillis();
        this.isDone = false;
    }
    
    public Todo(String text) {
        this();
        this.text = text;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public boolean isDone() {
        return isDone;
    }
    
    public void setDone(boolean done) {
        isDone = done;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}