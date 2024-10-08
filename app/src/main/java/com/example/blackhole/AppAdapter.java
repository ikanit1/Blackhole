package com.example.blackhole;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppViewHolder> {

    private List<AppInfo> appList;
    private AppSelectionViewModel viewModel;

    public AppAdapter(List<AppInfo> appList, AppSelectionViewModel viewModel) {
        this.appList = appList;
        this.viewModel = viewModel;
    }

    public void updateData(List<AppInfo> newAppList) {
        this.appList = newAppList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app, parent, false);
        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        AppInfo appInfo = appList.get(position);
        holder.bind(appInfo, viewModel);
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    class AppViewHolder extends RecyclerView.ViewHolder {

        private ImageView appIcon;
        private ImageView checkIcon;

        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.app_icon);
            checkIcon = itemView.findViewById(R.id.check_icon);
        }

        public void bind(AppInfo appInfo, AppSelectionViewModel viewModel) {
            appIcon.setImageDrawable(appInfo.getAppIcon());

            // Set initial state
            updateSelectionState(appInfo, viewModel);

            itemView.setOnClickListener(v -> {
                viewModel.toggleSelection(appInfo);
                updateSelectionState(appInfo, viewModel);
            });
        }

        private void updateSelectionState(AppInfo appInfo, AppSelectionViewModel viewModel) {
            if (viewModel.isSelected(appInfo)) {
                checkIcon.setVisibility(View.VISIBLE);
                appIcon.setColorFilter(Color.parseColor("#80000000")); // semi-transparent black
            } else {
                checkIcon.setVisibility(View.GONE);
                appIcon.setColorFilter(null);
            }
        }
    }
}
