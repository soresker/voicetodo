package com.voicetodo.app;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TodoDao {
    
    @Query("SELECT * FROM todos ORDER BY priority DESC, timestamp DESC")
    List<Todo> getAllTodos();
    
    @Insert
    void insertTodo(Todo todo);
    
    @Update
    void updateTodo(Todo todo);
    
    @Delete
    void deleteTodo(Todo todo);
    
    @Query("DELETE FROM todos WHERE id = :id")
    void deleteTodoById(int id);
    
    @Query("SELECT * FROM todos WHERE category = :category ORDER BY timestamp DESC")
    List<Todo> getTodosByCategory(Category category);
    
    @Query("SELECT DISTINCT category FROM todos ORDER BY category")
    List<Category> getAllCategories();
    
    @Query("SELECT COUNT(*) FROM todos WHERE category = :category")
    int getTodoCountByCategory(Category category);
    
    @Query("SELECT * FROM todos WHERE scheduledDate IS NOT NULL AND scheduledDate <= :endTime ORDER BY scheduledDate ASC")
    List<Todo> getScheduledTodos(long endTime);
    
    @Query("SELECT * FROM todos WHERE scheduledDate IS NOT NULL ORDER BY scheduledDate ASC")
    List<Todo> getAllScheduledTodos();
    
    @Query("SELECT * FROM todos WHERE DATE(scheduledDate/1000, 'unixepoch') = DATE(:date/1000, 'unixepoch') ORDER BY scheduledDate ASC")
    List<Todo> getTodosByDate(long date);
    
    @Query("SELECT * FROM todos WHERE priority = :priority ORDER BY timestamp DESC")
    List<Todo> getTodosByPriority(Priority priority);
    
    @Query("SELECT * FROM todos ORDER BY priority DESC, scheduledDate ASC, timestamp DESC")
    List<Todo> getAllTodosByPriority();
    
    @Query("SELECT * FROM todos WHERE id = :id LIMIT 1")
    Todo getTodoById(int id);
}