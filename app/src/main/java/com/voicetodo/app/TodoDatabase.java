package com.voicetodo.app;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import android.content.Context;

@Database(entities = {Todo.class}, version = 5, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class TodoDatabase extends RoomDatabase {
    
    private static volatile TodoDatabase INSTANCE;
    
    public abstract TodoDao todoDao();
    
    // Migration from version 1 to 2 (adding category column)
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE todos ADD COLUMN category TEXT NOT NULL DEFAULT 'PERSONAL'");
        }
    };
    
    // Migration from version 2 to 3 (adding scheduled date/time columns)
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE todos ADD COLUMN scheduledDate INTEGER");
            database.execSQL("ALTER TABLE todos ADD COLUMN hasScheduledTime INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE todos ADD COLUMN hasScheduledDate INTEGER NOT NULL DEFAULT 0");
        }
    };
    
    // Migration from version 3 to 4 (adding priority column)
    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE todos ADD COLUMN priority TEXT NOT NULL DEFAULT 'NORMAL'");
        }
    };
    
    // Migration from version 4 to 5 (adding displayOrder column)
    static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE todos ADD COLUMN displayOrder INTEGER NOT NULL DEFAULT 0");
            // Set initial order based on timestamp
            database.execSQL("UPDATE todos SET displayOrder = timestamp / 1000");
        }
    };
    
    public static TodoDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (TodoDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            TodoDatabase.class,
                            "todo_database"
                    ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                     .fallbackToDestructiveMigration() // Geliştirme aşamasında güvenli
                     .allowMainThreadQueries().build(); // Note: In production, use background thread
                }
            }
        }
        return INSTANCE;
    }
}