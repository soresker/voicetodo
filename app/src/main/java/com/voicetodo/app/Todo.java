package com.voicetodo.app;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "todos")
public class Todo {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String text;
    private boolean isDone;
    private long timestamp;
    private Category category;
    private Priority priority;
    private Date scheduledDate;
    private boolean hasScheduledTime;
    private boolean hasScheduledDate;
    
    public Todo() {
        this.timestamp = System.currentTimeMillis();
        this.isDone = false;
        this.category = Category.PERSONAL; // Default kategori
        this.priority = Priority.NORMAL; // Default öncelik
    }
    
    public Todo(String text) {
        this();
        
        // Tarih/saat parsing
        DateTimeParser.ParsedDateTime parsed = DateTimeParser.parseDateTime(text);
        
        this.text = parsed.getCleanText().isEmpty() ? text : parsed.getCleanText();
        this.category = Category.fromVoiceCommand(text); // Akıllı kategori tespiti
        this.priority = Priority.fromVoiceCommandWithCategory(text, this.category); // Akıllı öncelik tespiti
        this.scheduledDate = parsed.getDate();
        this.hasScheduledTime = parsed.hasTime();
        this.hasScheduledDate = parsed.hasDate();
    }
    
    public Todo(String text, Category category) {
        this();
        this.text = text;
        this.category = category;
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
    
    public Category getCategory() {
        return category;
    }
    
    public void setCategory(Category category) {
        this.category = category;
    }
    
    public Priority getPriority() {
        return priority;
    }
    
    public void setPriority(Priority priority) {
        this.priority = priority;
    }
    
    public Date getScheduledDate() {
        return scheduledDate;
    }
    
    public void setScheduledDate(Date scheduledDate) {
        this.scheduledDate = scheduledDate;
    }
    
    public boolean hasScheduledTime() {
        return hasScheduledTime;
    }
    
    public void setHasScheduledTime(boolean hasScheduledTime) {
        this.hasScheduledTime = hasScheduledTime;
    }
    
    public boolean hasScheduledDate() {
        return hasScheduledDate;
    }
    
    public void setHasScheduledDate(boolean hasScheduledDate) {
        this.hasScheduledDate = hasScheduledDate;
    }
    
    public boolean hasScheduledDateTime() {
        return hasScheduledTime || hasScheduledDate;
    }
    
    public String getFormattedScheduledDateTime() {
        return DateTimeParser.formatDateTime(scheduledDate, hasScheduledTime, hasScheduledDate);
    }
}