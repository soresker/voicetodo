package com.voicetodo.app;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TodoDao {
    
    @Query("SELECT * FROM todos ORDER BY timestamp DESC")
    List<Todo> getAllTodos();
    
    @Insert
    void insertTodo(Todo todo);
    
    @Update
    void updateTodo(Todo todo);
    
    @Delete
    void deleteTodo(Todo todo);
    
    @Query("DELETE FROM todos WHERE id = :id")
    void deleteTodoById(int id);
}