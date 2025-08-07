package com.voicetodo.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

public class TodoActionReceiver extends BroadcastReceiver {
    
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        int todoId = intent.getIntExtra("todo_id", -1);
        
        if (todoId == -1) {
            return;
        }
        
        TodoDatabase database = TodoDatabase.getInstance(context);
        TodoDao todoDao = database.todoDao();
        NotificationHelper notificationHelper = new NotificationHelper(context);
        
        new Thread(() -> {
            // Todo'yu database'den al
            Todo todo = findTodoById(todoDao, todoId);
            if (todo == null) {
                return;
            }
            
            if ("COMPLETE_TODO".equals(action)) {
                // Todo'yu tamamla
                todo.setDone(true);
                todoDao.updateTodo(todo);
                
                // Bildirimi kapat
                NotificationManagerCompat.from(context).cancel(todoId);
                
                // Başarı bildirimi göster
                notificationHelper.showInstantNotification(
                    "✅ Tamamlandı",
                    "\"" + todo.getText() + "\" tamamlandı",
                    todo.getPriority()
                );
                
            } else if ("POSTPONE_TODO".equals(action)) {
                // Todo'yu 5 dakika ertele
                Calendar newTime = Calendar.getInstance();
                newTime.add(Calendar.MINUTE, 5);
                
                todo.setScheduledDate(newTime.getTime());
                todo.setHasScheduledTime(true);
                todoDao.updateTodo(todo);
                
                // Yeni hatırlatıcı ayarla
                notificationHelper.scheduleNotification(todo);
                
                // Mevcut bildirimi kapat
                NotificationManagerCompat.from(context).cancel(todoId);
                
                // Bilgi bildirimi göster
                notificationHelper.showInstantNotification(
                    "⏰ Ertelendi",
                    "\"" + todo.getText() + "\" 5 dakika ertelendi",
                    todo.getPriority()
                );
            }
        }).start();
    }
    
    private Todo findTodoById(TodoDao todoDao, int todoId) {
        return todoDao.getTodoById(todoId);
    }
} 