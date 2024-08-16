package com.example.blackhole;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> implements Filterable {

    private List<LogEntry> logList;
    private List<LogEntry> logListFull; // Полный список для фильтрации

    public LogAdapter(List<LogEntry> logList) {
        this.logList = logList;
        this.logListFull = new ArrayList<>(logList); // Сохраняем полный список
    }

    // Создаем ViewHolder
    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Инфлейтим layout для элемента списка (log_item.xml)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_item, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        LogEntry logEntry = logList.get(position);
        holder.logMessage.setText(logEntry.message);
        holder.logLevel.setText(logEntry.level);
        holder.logTimestamp.setText(logEntry.timestamp);

        // Устанавливаем цвет текста на основе уровня лога
        if (logEntry.level.equals("ERROR")) {
            holder.logLevel.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.red));
        } else {
            holder.logLevel.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.green));
        }

        // Устанавливаем слушатель клика для отображения полного стектрейса
        holder.itemView.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
            builder.setTitle("Log Details");
            builder.setMessage(logEntry.stackTrace);
            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            builder.show();
        });
    }

    @Override
    public int getItemCount() {
        return logList.size();
    }

    @Override
    public Filter getFilter() {
        return logFilter;
    }

    private Filter logFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<LogEntry> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(logListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (LogEntry logEntry : logListFull) {
                    if (logEntry.message.toLowerCase().contains(filterPattern)) {
                        filteredList.add(logEntry);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            logList.clear();
            logList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    // Внутренний класс LogViewHolder
    public static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView logMessage, logLevel, logTimestamp;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            logMessage = itemView.findViewById(R.id.logMessage);
            logLevel = itemView.findViewById(R.id.logLevel);
            logTimestamp = itemView.findViewById(R.id.logTimestamp);
        }
    }
}
