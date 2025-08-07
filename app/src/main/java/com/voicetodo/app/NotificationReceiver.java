package com.voicetodo.app;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationReceiver extends BroadcastReceiver {
    
    private static final String CHANNEL_ID = "todo_reminders";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        int todoId = intent.getIntExtra("todo_id", -1);
        String todoText = intent.getStringExtra("todo_text");
        String todoCategory = intent.getStringExtra("todo_category");
        String todoPriority = intent.getStringExtra("todo_priority");
        
        if (todoId == -1 || todoText == null) {
            return;
        }
        
        // Ana uygulamaya yönlendirme intent'i
        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 
            todoId, 
            mainIntent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        // "Tamamla" action
        Intent completeIntent = new Intent(context, TodoActionReceiver.class);
        completeIntent.setAction("COMPLETE_TODO");
        completeIntent.putExtra("todo_id", todoId);
        PendingIntent completePendingIntent = PendingIntent.getBroadcast(
            context,
            todoId * 10 + 1,
            completeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        // "Ertele" action (5 dakika ertele)
        Intent postponeIntent = new Intent(context, TodoActionReceiver.class);
        postponeIntent.setAction("POSTPONE_TODO");
        postponeIntent.putExtra("todo_id", todoId);
        PendingIntent postponePendingIntent = PendingIntent.getBroadcast(
            context,
            todoId * 10 + 2,
            postponeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        // Bildirim oluştur
        String title = "🔔 Todo Hatırlatıcısı";
        String message = todoPriority + " " + todoText;
        if (todoCategory != null && !todoCategory.isEmpty()) {
            message = todoCategory + " • " + message;
        }
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{0, 500, 200, 500, 200, 500})
                .addAction(android.R.drawable.ic_menu_agenda, "Tamamla", completePendingIntent)
                .addAction(android.R.drawable.ic_menu_recent_history, "5dk Ertele", postponePendingIntent);
        
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(todoId, builder.build());
    }
} 