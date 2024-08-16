package com.example.blackhole;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class RecipientAdapter extends RecyclerView.Adapter<RecipientAdapter.RecipientViewHolder> {

    private final List<Recipient> recipients;
    private final LayoutInflater inflater;
    private final List<Recipient> selectedRecipients = new ArrayList<>();
    private final Context context;

    public RecipientAdapter(Context context, List<Recipient> recipients) {
        this.context = context;
        this.recipients = recipients;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecipientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recipient_item, parent, false);
        return new RecipientViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipientViewHolder holder, int position) {
        Recipient recipient = recipients.get(position);
        holder.nameTextView.setText(recipient.getName());
        holder.messagePreviewTextView.setText(recipient.getMessagePreview());
        holder.avatarImageView.setImageResource(recipient.getAvatarResId());
        holder.selectCheckBox.setChecked(recipient.isSelected());
        holder.selectCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            recipient.setSelected(isChecked);
            if (isChecked) {
                selectedRecipients.add(recipient);
            } else {
                selectedRecipients.remove(recipient);
            }
        });

        // Optional: Start SelectedContactsActivity when an item is clicked
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SelectedContactsActivity.class);
            intent.putParcelableArrayListExtra("selected_recipients", (ArrayList<? extends Parcelable>) new ArrayList<>(selectedRecipients));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return recipients.size();
    }

    static class RecipientViewHolder extends RecyclerView.ViewHolder {
        final TextView nameTextView;
        final TextView messagePreviewTextView;
        final ImageView avatarImageView;
        final CheckBox selectCheckBox;

        public RecipientViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            messagePreviewTextView = itemView.findViewById(R.id.messagePreviewTextView);
            avatarImageView = itemView.findViewById(R.id.avatarImageView);
            selectCheckBox = itemView.findViewById(R.id.selectCheckBox);
        }
    }
}
