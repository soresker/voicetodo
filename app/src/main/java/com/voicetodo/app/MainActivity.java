package com.voicetodo.app;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TodoAdapter.OnTodoListener {
    
    private static final int REQUEST_SPEECH_INPUT = 1000;
    private static final int SPEECH_REQUEST_CODE = 1000;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    
    private ExtendedFloatingActionButton fabVoice;
    private TextView tvStatus;
    private TextView tvEmpty;
    private RecyclerView rvTodos;
    private TodoAdapter todoAdapter;
    private TabLayout tabLayout;
    
    private TodoDatabase database;
    private TodoDao todoDao;
    private Vibrator vibrator;
    private Animation pulseAnimation;
    private NotificationHelper notificationHelper;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initViews();
        initDatabase();
        setupRecyclerView();
        initAnimations();
        initNotifications();
        checkPermissions();
        loadTodos();
    }
    
    private void initViews() {
        fabVoice = findViewById(R.id.fab_voice);
        tvStatus = findViewById(R.id.tv_status);
        tvEmpty = findViewById(R.id.tv_empty);
        rvTodos = findViewById(R.id.rv_todos);
        tabLayout = findViewById(R.id.tab_layout);
        
        fabVoice.setOnClickListener(v -> startVoiceInput());
        setupTabs();
    }
    
    private void initDatabase() {
        database = TodoDatabase.getInstance(this);
        todoDao = database.todoDao();
    }
    
    private void setupRecyclerView() {
        todoAdapter = new TodoAdapter(this);
        rvTodos.setLayoutManager(new LinearLayoutManager(this));
        rvTodos.setAdapter(todoAdapter);
        
        // Setup swipe actions and drag & drop
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToActionCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    handleSwipeAction(position, direction);
                }
            }
            
            @Override
            public void onItemMove(int fromPosition, int toPosition) {
                handleDragAndDrop(fromPosition, toPosition);
            }
        });
        itemTouchHelper.attachToRecyclerView(rvTodos);
    }
    
    private void handleDragAndDrop(int fromPosition, int toPosition) {
        // Update adapter immediately for smooth UI
        todoAdapter.moveItem(fromPosition, toPosition);
        
        // Update display order in database
        new Thread(() -> {
            List<Todo> currentTodos = todoAdapter.getTodos();
            for (int i = 0; i < currentTodos.size(); i++) {
                Todo todo = currentTodos.get(i);
                todo.setDisplayOrder(i);
                todoDao.updateTodo(todo);
            }
        }).start();
    }
    
    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("📋 Tümü"));
        tabLayout.addTab(tabLayout.newTab().setText("📅 Bugün"));
        tabLayout.addTab(tabLayout.newTab().setText("🌅 Yarın"));
        tabLayout.addTab(tabLayout.newTab().setText("📆 Bu Hafta"));
        tabLayout.addTab(tabLayout.newTab().setText("📊 Kategoriler"));
        
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterTodosByTab(tab.getPosition());
            }
            
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }
    
    private void filterTodosByTab(int position) {
        new Thread(() -> {
            List<Todo> filteredTodos;
            Calendar today = Calendar.getInstance();
            Calendar tomorrow = Calendar.getInstance();
            tomorrow.add(Calendar.DAY_OF_MONTH, 1);
            Calendar weekEnd = Calendar.getInstance();
            weekEnd.add(Calendar.DAY_OF_MONTH, 7);
            
            switch (position) {
                case 0: // Tümü
                    filteredTodos = todoDao.getAllTodos();
                    break;
                case 1: // Bugün
                    Date todayStart = getTodayStart();
                    Date todayEnd = getTodayEnd();
                    filteredTodos = todoDao.getTodosByDateRange(todayStart.getTime(), todayEnd.getTime());
                    break;
                case 2: // Yarın
                    Date tomorrowStart = getTomorrowStart();
                    Date tomorrowEnd = getTomorrowEnd();
                    filteredTodos = todoDao.getTodosByDateRange(tomorrowStart.getTime(), tomorrowEnd.getTime());
                    break;
                case 3: // Bu Hafta
                    Date weekStart = getTodayStart();
                    Date weekEndDate = getWeekEnd();
                    filteredTodos = todoDao.getTodosByDateRange(weekStart.getTime(), weekEndDate.getTime());
                    break;
                case 4: // Kategoriler
                    // Show category-wise grouping (for now show all)
                    filteredTodos = todoDao.getAllTodos();
                    break;
                default:
                    filteredTodos = todoDao.getAllTodos();
                    break;
            }
            
            runOnUiThread(() -> {
                todoAdapter.updateTodos(filteredTodos);
                updateEmptyView(filteredTodos.isEmpty());
            });
        }).start();
    }
    
    private Date getTodayStart() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    private Date getTodayEnd() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }
    
    private Date getTomorrowStart() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    private Date getTomorrowEnd() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }
    
    private Date getWeekEnd() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 7);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }
    
    private void initAnimations() {
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse_animation);
    }
    
    private void initNotifications() {
        notificationHelper = new NotificationHelper(this);
        // Günlük hatırlatıcıyı başlat
        notificationHelper.scheduleRecurringReminder();
        // Yaklaşan hatırlatıcıları ayarla
        notificationHelper.scheduleUpcomingReminders();
    }
    
    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) 
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, 
                    new String[]{Manifest.permission.RECORD_AUDIO}, 
                    REQUEST_RECORD_AUDIO_PERMISSION);
        }
    }
    
    private void startVoiceInput() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) 
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
            return;
        }
        
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "tr-TR");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Ne yapmak istiyorsunuz?");
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 8000);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 6000);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 30000);
        
        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, getString(R.string.speech_not_available), Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String spokenText = result.get(0);
                if (!spokenText.trim().isEmpty()) {
                    processVoiceCommand(spokenText.trim());
                }
            }
        }
    }
    
    private void processVoiceCommand(String spokenText) {
        String command = spokenText.toLowerCase().trim();
        
        // Voice commands for todo operations
        if (command.contains("sil") || command.contains("silme")) {
            deleteLastTodo();
        } else if (command.contains("tamamla") || command.contains("bitir") || command.contains("tamam")) {
            completeLastTodo();
        } else if (command.contains("temizle") || command.contains("temizlik")) {
            clearCompletedTodos();
        } else if (command.contains("say") || command.contains("kaç tane") || command.contains("adet")) {
            announceStats();
        } else if (command.contains("oku") || command.contains("listele")) {
            readTodos();
        } else if (command.contains("kategorileri göster") || command.contains("kategori listesi")) {
            showCategories();
        } else if (command.contains("kategori") && (command.contains("oku") || command.contains("listele"))) {
            readTodosByCategory(command);
        } else if (command.contains("bugün") && (command.contains("oku") || command.contains("listele"))) {
            readTodaysSchedule();
        } else if (command.contains("yarın") && (command.contains("oku") || command.contains("listele"))) {
            readTomorrowsSchedule();
        } else if (command.contains("zamanlanmış") && (command.contains("oku") || command.contains("listele"))) {
            readScheduledTodos();
        } else if (command.contains("öncelikli") && (command.contains("oku") || command.contains("listele"))) {
            readPriorityTodos();
        } else if (command.contains("acil") && (command.contains("oku") || command.contains("listele"))) {
            readTodosByPriority("URGENT");
        } else if (command.contains("önemli") && (command.contains("oku") || command.contains("listele"))) {
            readTodosByPriority("HIGH");
        } else if (command.contains("hatırlatıcı") && command.contains("test")) {
            testNotification();
        } else if (command.contains("hatırlatıcıları") && (command.contains("yenile") || command.contains("güncelle"))) {
            refreshNotifications();
        } else {
            // Normal todo ekleme - akıllı kategori, öncelik ve tarih/saat tespiti ile
            addTodoWithDateTime(spokenText);
        }
    }
    
    private void addTodoWithDateTime(String text) {
        Todo todo = new Todo(text);
        
        // Save to database in background thread
        new Thread(() -> {
            todoDao.insertTodo(todo);
            
            // Eğer zamanlanmış todo ise hatırlatıcı ayarla
            if (todo.hasScheduledDateTime()) {
                notificationHelper.scheduleNotification(todo);
            }
            
            runOnUiThread(this::loadTodos);
        }).start();
        
        String message = "Todo eklendi: " + todo.getText();
        if (todo.hasScheduledDateTime()) {
            message += " (" + todo.getFormattedScheduledDateTime() + ")";
            message += " 🔔 Hatırlatıcı ayarlandı";
        }
        
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        triggerHapticFeedback();
    }
    
    private void deleteLastTodo() {
        new Thread(() -> {
            List<Todo> todos = todoDao.getAllTodos();
            if (!todos.isEmpty()) {
                Todo lastTodo = todos.get(todos.size() - 1);
                todoDao.deleteTodo(lastTodo);
                runOnUiThread(() -> {
                    loadTodos();
                    Toast.makeText(this, "Son todo silindi: " + lastTodo.getText(), Toast.LENGTH_SHORT).show();
                    triggerHapticFeedback();
                });
            } else {
                runOnUiThread(() -> 
                    Toast.makeText(this, "Silinecek todo bulunamadı", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
    
    private void completeLastTodo() {
        new Thread(() -> {
            List<Todo> todos = todoDao.getAllTodos();
            if (!todos.isEmpty()) {
                Todo lastTodo = todos.get(todos.size() - 1);
                if (!lastTodo.isDone()) {
                    lastTodo.setDone(true);
                    todoDao.updateTodo(lastTodo);
                    runOnUiThread(() -> {
                        loadTodos();
                        Toast.makeText(this, "Todo tamamlandı: " + lastTodo.getText(), Toast.LENGTH_SHORT).show();
                        triggerHapticFeedback();
                    });
                } else {
                    runOnUiThread(() -> 
                        Toast.makeText(this, "Son todo zaten tamamlanmış", Toast.LENGTH_SHORT).show()
                    );
                }
            } else {
                runOnUiThread(() -> 
                    Toast.makeText(this, "Tamamlanacak todo bulunamadı", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
    
    private void clearCompletedTodos() {
        new Thread(() -> {
            List<Todo> todos = todoDao.getAllTodos();
            int deletedCount = 0;
            for (Todo todo : todos) {
                if (todo.isDone()) {
                    todoDao.deleteTodo(todo);
                    deletedCount++;
                }
            }
            final int finalCount = deletedCount;
            runOnUiThread(() -> {
                loadTodos();
                Toast.makeText(this, finalCount + " tamamlanan todo temizlendi", Toast.LENGTH_SHORT).show();
                triggerHapticFeedback();
            });
        }).start();
    }
    
    private void announceStats() {
        new Thread(() -> {
            List<Todo> todos = todoDao.getAllTodos();
            int totalCount = todos.size();
            int completedCount = 0;
            for (Todo todo : todos) {
                if (todo.isDone()) {
                    completedCount++;
                }
            }
            final int finalCompletedCount = completedCount;
            final int pendingCount = totalCount - completedCount;
            
            // Kategori bazlı istatistikler
            StringBuilder categoryStats = new StringBuilder();
            for (Category category : Category.values()) {
                int count = todoDao.getTodoCountByCategory(category);
                if (count > 0) {
                    categoryStats.append(category.getEmoji()).append(" ").append(count).append(", ");
                }
            }
            
            runOnUiThread(() -> {
                String message = "Toplam " + totalCount + " todo var. " + 
                               finalCompletedCount + " tamamlandı, " + 
                               pendingCount + " bekliyor.\n";
                
                if (categoryStats.length() > 0) {
                    message += "Kategoriler: " + categoryStats.substring(0, categoryStats.length() - 2);
                }
                
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            });
        }).start();
    }
    
    private void readTodos() {
        new Thread(() -> {
            List<Todo> todos = todoDao.getAllTodos();
            if (todos.isEmpty()) {
                runOnUiThread(() -> 
                    Toast.makeText(this, "Todo listeniz boş", Toast.LENGTH_SHORT).show()
                );
            } else {
                StringBuilder todoList = new StringBuilder("Todo'larınız:\n");
                for (int i = 0; i < Math.min(todos.size(), 5); i++) {
                    Todo todo = todos.get(i);
                    todoList.append((i + 1)).append(". ").append(todo.getText());
                    if (todo.isDone()) {
                        todoList.append(" (Tamamlandı)");
                    }
                    todoList.append("\n");
                }
                if (todos.size() > 5) {
                    todoList.append("... ve ").append(todos.size() - 5).append(" tane daha");
                }
                
                runOnUiThread(() -> 
                    Toast.makeText(this, todoList.toString(), Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }
    
    private void showCategories() {
        new Thread(() -> {
            List<Category> categories = todoDao.getAllCategories();
            StringBuilder categoryList = new StringBuilder("Mevcut kategoriler:\n");
            
            for (Category category : Category.values()) {
                int count = todoDao.getTodoCountByCategory(category);
                if (count > 0) {
                    categoryList.append(category.getDisplayWithEmoji())
                               .append(" (").append(count).append(" todo)\n");
                }
            }
            
            runOnUiThread(() -> 
                Toast.makeText(this, categoryList.toString(), Toast.LENGTH_LONG).show()
            );
        }).start();
    }
    
    private void readTodosByCategory(String command) {
        Category targetCategory = null;
        
        // Komuttan kategori tespit et
        String lowerCommand = command.toLowerCase();
        for (Category category : Category.values()) {
            if (lowerCommand.contains(category.getDisplayName().toLowerCase())) {
                targetCategory = category;
                break;
            }
        }
        
        if (targetCategory == null) {
            Toast.makeText(this, "Kategori bulunamadı. 'Kategorileri göster' diyerek mevcut kategorileri görebilirsiniz.", Toast.LENGTH_SHORT).show();
            return;
        }
        
        final Category finalCategory = targetCategory;
        new Thread(() -> {
            List<Todo> todos = todoDao.getTodosByCategory(finalCategory);
            if (todos.isEmpty()) {
                runOnUiThread(() -> 
                    Toast.makeText(this, finalCategory.getDisplayWithEmoji() + " kategorisinde todo bulunamadı", Toast.LENGTH_SHORT).show()
                );
            } else {
                StringBuilder todoList = new StringBuilder(finalCategory.getDisplayWithEmoji() + " kategorisi:\n");
                for (int i = 0; i < Math.min(todos.size(), 5); i++) {
                    Todo todo = todos.get(i);
                    todoList.append((i + 1)).append(". ").append(todo.getText());
                    if (todo.isDone()) {
                        todoList.append(" (Tamamlandı)");
                    }
                    todoList.append("\n");
                }
                if (todos.size() > 5) {
                    todoList.append("... ve ").append(todos.size() - 5).append(" tane daha");
                }
                
                runOnUiThread(() -> 
                    Toast.makeText(this, todoList.toString(), Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }
    
    private void readTodaysSchedule() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        
        new Thread(() -> {
            List<Todo> todos = todoDao.getTodosByDate(today.getTimeInMillis());
            if (todos.isEmpty()) {
                runOnUiThread(() -> 
                    Toast.makeText(this, "Bugün için zamanlanmış todo bulunamadı", Toast.LENGTH_SHORT).show()
                );
            } else {
                StringBuilder schedule = new StringBuilder("Bugünkü program:\n");
                for (Todo todo : todos) {
                    schedule.append("• ").append(todo.getText());
                    if (todo.hasScheduledTime()) {
                        schedule.append(" (").append(todo.getFormattedScheduledDateTime()).append(")");
                    }
                    schedule.append("\n");
                }
                
                runOnUiThread(() -> 
                    Toast.makeText(this, schedule.toString(), Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }
    
    private void readTomorrowsSchedule() {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        tomorrow.set(Calendar.HOUR_OF_DAY, 0);
        tomorrow.set(Calendar.MINUTE, 0);
        tomorrow.set(Calendar.SECOND, 0);
        tomorrow.set(Calendar.MILLISECOND, 0);
        
        new Thread(() -> {
            List<Todo> todos = todoDao.getTodosByDate(tomorrow.getTimeInMillis());
            if (todos.isEmpty()) {
                runOnUiThread(() -> 
                    Toast.makeText(this, "Yarın için zamanlanmış todo bulunamadı", Toast.LENGTH_SHORT).show()
                );
            } else {
                StringBuilder schedule = new StringBuilder("Yarınki program:\n");
                for (Todo todo : todos) {
                    schedule.append("• ").append(todo.getText());
                    if (todo.hasScheduledTime()) {
                        schedule.append(" (").append(todo.getFormattedScheduledDateTime()).append(")");
                    }
                    schedule.append("\n");
                }
                
                runOnUiThread(() -> 
                    Toast.makeText(this, schedule.toString(), Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }
    
    private void readScheduledTodos() {
        new Thread(() -> {
            List<Todo> todos = todoDao.getAllScheduledTodos();
            if (todos.isEmpty()) {
                runOnUiThread(() -> 
                    Toast.makeText(this, "Zamanlanmış todo bulunamadı", Toast.LENGTH_SHORT).show()
                );
            } else {
                StringBuilder schedule = new StringBuilder("Zamanlanmış todo'lar:\n");
                for (int i = 0; i < Math.min(todos.size(), 10); i++) {
                    Todo todo = todos.get(i);
                    schedule.append("• ").append(todo.getText());
                    schedule.append(" (").append(todo.getFormattedScheduledDateTime()).append(")");
                    schedule.append("\n");
                }
                if (todos.size() > 10) {
                    schedule.append("... ve ").append(todos.size() - 10).append(" tane daha");
                }
                
                runOnUiThread(() -> 
                    Toast.makeText(this, schedule.toString(), Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }
    
    private void readPriorityTodos() {
        new Thread(() -> {
            List<Todo> todos = todoDao.getAllTodosByPriority();
            if (todos.isEmpty()) {
                runOnUiThread(() -> 
                    Toast.makeText(this, "Todo bulunamadı", Toast.LENGTH_SHORT).show()
                );
            } else {
                StringBuilder priorityList = new StringBuilder("Öncelik sırasına göre todo'lar:\n");
                for (int i = 0; i < Math.min(todos.size(), 8); i++) {
                    Todo todo = todos.get(i);
                    priorityList.append(todo.getPriority().getEmoji()).append(" ")
                               .append(todo.getText());
                    if (todo.hasScheduledDateTime()) {
                        priorityList.append(" (").append(todo.getFormattedScheduledDateTime()).append(")");
                    }
                    priorityList.append("\n");
                }
                if (todos.size() > 8) {
                    priorityList.append("... ve ").append(todos.size() - 8).append(" tane daha");
                }
                
                runOnUiThread(() -> 
                    Toast.makeText(this, priorityList.toString(), Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }
    
    private void readTodosByPriority(String priorityName) {
        Priority priority;
        try {
            priority = Priority.valueOf(priorityName);
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, "Geçersiz öncelik seviyesi", Toast.LENGTH_SHORT).show();
            return;
        }
        
        new Thread(() -> {
            List<Todo> todos = todoDao.getTodosByPriority(priority);
            if (todos.isEmpty()) {
                runOnUiThread(() -> 
                    Toast.makeText(this, priority.getDisplayWithEmoji() + " öncelikli todo bulunamadı", Toast.LENGTH_SHORT).show()
                );
            } else {
                StringBuilder priorityList = new StringBuilder(priority.getDisplayWithEmoji() + " öncelikli todo'lar:\n");
                for (int i = 0; i < Math.min(todos.size(), 5); i++) {
                    Todo todo = todos.get(i);
                    priorityList.append("• ").append(todo.getText());
                    if (todo.hasScheduledDateTime()) {
                        priorityList.append(" (").append(todo.getFormattedScheduledDateTime()).append(")");
                    }
                    priorityList.append("\n");
                }
                if (todos.size() > 5) {
                    priorityList.append("... ve ").append(todos.size() - 5).append(" tane daha");
                }
                
                runOnUiThread(() -> 
                    Toast.makeText(this, priorityList.toString(), Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }
    
    private void testNotification() {
        notificationHelper.showInstantNotification(
            "🔔 Test Hatırlatıcısı",
            "Hatırlatıcı sistemi çalışıyor!",
            Priority.NORMAL
        );
        Toast.makeText(this, "Test hatırlatıcısı gönderildi", Toast.LENGTH_SHORT).show();
    }
    
    private void refreshNotifications() {
        new Thread(() -> {
            // Tüm zamanlanmış todo'lar için hatırlatıcıları yeniden ayarla
            List<Todo> scheduledTodos = todoDao.getAllScheduledTodos();
            int count = 0;
            
            for (Todo todo : scheduledTodos) {
                if (!todo.isDone() && todo.hasScheduledDateTime()) {
                    notificationHelper.scheduleNotification(todo);
                    count++;
                }
            }
            
            final int finalCount = count;
            runOnUiThread(() -> 
                Toast.makeText(this, finalCount + " hatırlatıcı yenilendi", Toast.LENGTH_SHORT).show()
            );
        }).start();
    }
    
    private void triggerHapticFeedback() {
        if (vibrator != null && vibrator.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(50);
            }
        }
    }
    
    private void handleSwipeAction(int position, int direction) {
        new Thread(() -> {
            List<Todo> todos = todoDao.getAllTodos();
            if (position < todos.size()) {
                Todo todo = todos.get(position);
                
                if (direction == ItemTouchHelper.RIGHT) {
                    // Swipe right - Complete/Uncomplete
                    todo.setDone(!todo.isDone());
                    todoDao.updateTodo(todo);
                    runOnUiThread(() -> {
                        loadTodos();
                        String message = todo.isDone() ? "Tamamlandı: " : "Tamamlanmadı: ";
                        Toast.makeText(this, message + todo.getText(), Toast.LENGTH_SHORT).show();
                        triggerHapticFeedback();
                    });
                } else if (direction == ItemTouchHelper.LEFT) {
                    // Swipe left - Delete
                    notificationHelper.cancelNotification(todo.getId());
                    todoDao.deleteTodo(todo);
                    runOnUiThread(() -> {
                        loadTodos();
                        Toast.makeText(this, "Silindi: " + todo.getText(), Toast.LENGTH_SHORT).show();
                        triggerHapticFeedback();
                    });
                }
            }
        }).start();
    }
    
    private void loadTodos() {
        new Thread(() -> {
            List<Todo> todos = todoDao.getAllTodos();
            runOnUiThread(() -> {
                todoAdapter.setTodos(todos);
                updateEmptyView(todos.isEmpty());
            });
        }).start();
    }
    
    private void updateEmptyView(boolean isEmpty) {
        if (isEmpty) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvTodos.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvTodos.setVisibility(View.VISIBLE);
        }
    }
    
    @Override
    public void onTodoChecked(Todo todo, boolean isChecked) {
        todo.setDone(isChecked);
        new Thread(() -> {
            todoDao.updateTodo(todo);
            runOnUiThread(this::loadTodos);
        }).start();
    }
    
    @Override
    public void onTodoDeleted(Todo todo) {
        new Thread(() -> {
            // Hatırlatıcıyı iptal et
            notificationHelper.cancelNotification(todo.getId());
            todoDao.deleteTodo(todo);
            runOnUiThread(this::loadTodos);
        }).start();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Mikrofon izni verildi", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_LONG).show();
            }
        }
    }
}