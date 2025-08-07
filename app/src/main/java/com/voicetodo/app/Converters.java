package com.voicetodo.app;

import androidx.room.TypeConverter;
import java.util.Date;

public class Converters {
    
    @TypeConverter
    public static Category fromString(String value) {
        if (value == null) {
            return Category.PERSONAL;
        }
        try {
            return Category.valueOf(value);
        } catch (IllegalArgumentException e) {
            return Category.PERSONAL;
        }
    }
    
    @TypeConverter
    public static String categoryToString(Category category) {
        return category == null ? Category.PERSONAL.name() : category.name();
    }
    
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }
    
    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
    
    @TypeConverter
    public static Priority fromPriorityString(String value) {
        if (value == null) {
            return Priority.NORMAL;
        }
        try {
            return Priority.valueOf(value);
        } catch (IllegalArgumentException e) {
            return Priority.NORMAL;
        }
    }
    
    @TypeConverter
    public static String priorityToString(Priority priority) {
        return priority == null ? Priority.NORMAL.name() : priority.name();
    }
} 