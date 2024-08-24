package com.example.blackhole;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "log_entries")
public class LogEntry {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String timestamp;
    public String phoneNumber;
    public String recipient;
    public String message;
    public int status;
    public String level;
    public String stackTrace;

    // Конструктор, который инициализирует только некоторые поля
    public LogEntry(String timestamp, String phoneNumber, String recipient, String message) {
        this.timestamp = timestamp;
        this.phoneNumber = phoneNumber;
        this.recipient = recipient;
        this.message = message;
        // Установка значений по умолчанию для необязательных полей
        this.status = 0;  // Например, статус "не отправлено"
        this.level = "INFO";  // Уровень по умолчанию
        this.stackTrace = null;  // Без трассировки стека
    }

    @Ignore  // Добавляем аннотацию @Ignore к этому конструктору
    public LogEntry(String timestamp, String phoneNumber, String recipient, String message, int status, String level, String stackTrace) {
        this.timestamp = timestamp;
        this.phoneNumber = phoneNumber;
        this.recipient = recipient;
        this.message = message;
        this.status = status;
        this.level = level;
        this.stackTrace = stackTrace;
    }
}
