package com.voicetodo.app;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Todo.class}, version = 1, exportSchema = false)
public abstract class TodoDatabase extends RoomDatabase {
    
    private static volatile TodoDatabase INSTANCE;
    
    public abstract TodoDao todoDao();
    
    public static TodoDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (TodoDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            TodoDatabase.class,
                            "todo_database"
                    ).allowMainThreadQueries().build(); // Note: In production, use background thread
                }
            }
        }
        return INSTANCE;
    }
}