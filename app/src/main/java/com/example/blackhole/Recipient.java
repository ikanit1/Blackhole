package com.example.blackhole;

import android.os.Parcel;
import android.os.Parcelable;

public class Recipient implements Parcelable {

    private String name;
    private String messagePreview;
    private int avatarResId;
    private boolean isSelected;

    // Constructor
    public Recipient(String name, String messagePreview, int avatarResId, boolean isSelected) {
        this.name = name;
        this.messagePreview = messagePreview;
        this.avatarResId = avatarResId;
        this.isSelected = isSelected;
    }

    // Parcelable Constructor
    protected Recipient(Parcel in) {
        name = in.readString();
        messagePreview = in.readString();
        avatarResId = in.readInt();
        isSelected = in.readByte() != 0;
    }

    public static final Creator<Recipient> CREATOR = new Creator<Recipient>() {
        @Override
        public Recipient createFromParcel(Parcel in) {
            return new Recipient(in);
        }

        @Override
        public Recipient[] newArray(int size) {
            return new Recipient[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(messagePreview);
        dest.writeInt(avatarResId);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessagePreview() {
        return messagePreview;
    }

    public void setMessagePreview(String messagePreview) {
        this.messagePreview = messagePreview;
    }

    public int getAvatarResId() {
        return avatarResId;
    }

    public void setAvatarResId(int avatarResId) {
        this.avatarResId = avatarResId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
