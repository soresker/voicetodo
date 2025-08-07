package com.voicetodo.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.List;

public class DailyReminderReceiver extends BroadcastReceiver {
    
    @Override
    public void onReceive(Context context, Intent intent) {
        TodoDatabase database = TodoDatabase.getInstance(context);
        TodoDao todoDao = database.todoDao();
        NotificationHelper notificationHelper = new NotificationHelper(context);
        
        new Thread(() -> {
            // Bugünkü todo'ları al
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);
            
            List<Todo> todaysTodos = todoDao.getTodosByDate(today.getTimeInMillis());
            
            if (!todaysTodos.isEmpty()) {
                int pendingCount = 0;
                int urgentCount = 0;
                
                for (Todo todo : todaysTodos) {
                    if (!todo.isDone()) {
                        pendingCount++;
                        if (todo.getPriority() == Priority.URGENT) {
                            urgentCount++;
                        }
                    }
                }
                
                if (pendingCount > 0) {
                    String title = "🌅 Günaydın! Bugünkü Todo'larınız";
                    String message = pendingCount + " bekleyen göreviniz var";
                    
                    if (urgentCount > 0) {
                        message += " (" + urgentCount + " acil)";
                    }
                    
                    notificationHelper.showInstantNotification(title, message, Priority.NORMAL);
                }
            }
            
            // Yaklaşan hatırlatıcıları ayarla
            notificationHelper.scheduleUpcomingReminders();
        }).start();
    }
} 