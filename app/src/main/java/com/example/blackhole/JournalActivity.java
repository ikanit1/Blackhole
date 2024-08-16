package com.example.blackhole;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JournalActivity extends AppCompatActivity {

    private RecyclerView logRecyclerView;
    private LogAdapter logAdapter;
    private SearchView searchView;
    private Spinner logLevelSpinner;
    private BottomNavigationView bottomNavigationView;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);

        logRecyclerView = findViewById(R.id.logRecyclerView);
        searchView = findViewById(R.id.searchView);
        logLevelSpinner = findViewById(R.id.logLevelSpinner);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        logRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        executorService = Executors.newSingleThreadExecutor();  // Инициализация Executor для выполнения задач в фоновом потоке

        // Загружаем все логи асинхронно
        loadLogsAsync("ALL");

        // Добавляем слушатели для поиска и фильтрации
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterLogs(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterLogs(newText);
                return false;
            }
        });

        // Фильтрация логов по уровню, когда пользователь выбирает другой элемент Spinner
        logLevelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLevel = parent.getItemAtPosition(position).toString();
                loadLogsAsync(selectedLevel);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Обработка нажатий на элементы нижней панели навигации
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_push) {
                startActivity(new Intent(JournalActivity.this, AppSelectionActivity.class));
                return true;
            } else if (itemId == R.id.navigation_sms) {
                startActivity(new Intent(JournalActivity.this, RecipientSms.class));
                return true;
            } else if (itemId == R.id.navigation_dlq) {
                startActivity(new Intent(JournalActivity.this, DLQActivity.class));
                return true;
            } else if (itemId == R.id.navigation_journal) {
                return true; // Остаемся в текущей активности
            } else if (itemId == R.id.navigation_options) {
                startActivity(new Intent(JournalActivity.this, OptionsActivity.class));
                return true;
            } else {
                return false;
            }
        });
    }

    private void loadLogsAsync(String level) {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            List<LogEntry> logs;

            if (level.equals("ALL")) {
                logs = db.logEntryDao().getAllLogs();
            } else {
                logs = db.logEntryDao().getLogsByLevel(level);
            }

            // Обновление UI должно происходить на главном потоке
            runOnUiThread(() -> {
                logAdapter = new LogAdapter(logs);
                logRecyclerView.setAdapter(logAdapter);
            });
        });
    }

    private void filterLogs(String query) {
        if (logAdapter != null) {
            logAdapter.getFilter().filter(query);
        }
    }
}
