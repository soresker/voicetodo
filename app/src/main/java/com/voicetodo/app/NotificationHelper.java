package com.voicetodo.app;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;
import java.util.List;

public class NotificationHelper {
    
    private static final String CHANNEL_ID = "todo_reminders";
    private static final String CHANNEL_NAME = "Todo Hatırlatıcıları";
    private static final String CHANNEL_DESCRIPTION = "Zamanlanmış todo'lar için hatırlatıcılar";
    
    private Context context;
    private NotificationManager notificationManager;
    private AlarmManager alarmManager;
    
    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        createNotificationChannel();
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESCRIPTION);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(channel);
        }
    }
    
    public void scheduleNotification(Todo todo) {
        if (!todo.hasScheduledDateTime() || todo.getScheduledDate() == null) {
            return;
        }
        
        long currentTime = System.currentTimeMillis();
        long scheduledTime = todo.getScheduledDate().getTime();
        
        // Geçmiş tarihler için bildirim ayarlama
        if (scheduledTime <= currentTime) {
            return;
        }
        
        Intent notificationIntent = new Intent(context, NotificationReceiver.class);
        notificationIntent.putExtra("todo_id", todo.getId());
        notificationIntent.putExtra("todo_text", todo.getText());
        notificationIntent.putExtra("todo_category", todo.getCategory().getDisplayWithEmoji());
        notificationIntent.putExtra("todo_priority", todo.getPriority().getDisplayWithEmoji());
        
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            context,
            todo.getId(),
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        // AlarmManager ile zamanlanmış bildirim ayarla
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, scheduledTime, pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, scheduledTime, pendingIntent);
            }
        }
    }
    
    public void cancelNotification(int todoId) {
        Intent notificationIntent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            context,
            todoId,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
    
    public void showInstantNotification(String title, String message, Priority priority) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_menu_recent_history)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(getNotificationPriority(priority))
                .setAutoCancel(true)
                .setVibrate(new long[]{100, 200, 100, 200});
        
        // Ana uygulamaya yönlendirme
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        builder.setContentIntent(pendingIntent);
        
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify((int) System.currentTimeMillis(), builder.build());
    }
    
    private int getNotificationPriority(Priority priority) {
        switch (priority) {
            case URGENT:
                return NotificationCompat.PRIORITY_MAX;
            case HIGH:
                return NotificationCompat.PRIORITY_HIGH;
            case NORMAL:
                return NotificationCompat.PRIORITY_DEFAULT;
            case LOW:
                return NotificationCompat.PRIORITY_LOW;
            default:
                return NotificationCompat.PRIORITY_DEFAULT;
        }
    }
    
    public void scheduleUpcomingReminders() {
        // Yaklaşan (1 saat içinde) todo'lar için hatırlatıcı ayarla
        Calendar oneHourLater = Calendar.getInstance();
        oneHourLater.add(Calendar.HOUR, 1);
        
        new Thread(() -> {
            TodoDatabase database = TodoDatabase.getInstance(context);
            TodoDao todoDao = database.todoDao();
            
            // 1 saat içindeki scheduled todo'ları al
            long endTime = oneHourLater.getTimeInMillis();
            List<Todo> upcomingTodos = todoDao.getScheduledTodos(endTime);
            
            for (Todo todo : upcomingTodos) {
                if (!todo.isDone()) {
                    scheduleNotification(todo);
                }
            }
        }).start();
    }
    
    public void scheduleRecurringReminder() {
        // Günlük hatırlatıcı: Sabah 9'da bugünkü todo'ları göster
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        
        // Eğer zaman geçmişse, yarın için ayarla
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        
        Intent intent = new Intent(context, DailyReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            context,
            9999, // Unique ID for daily reminder
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        if (alarmManager != null) {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            );
        }
    }
} 