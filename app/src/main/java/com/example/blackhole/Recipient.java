package com.example.blackhole;

public class Recipient {

    private String name;
    private String messagePreview;
    private int avatarResId;
    private boolean isSelected;

    public Recipient(String name, String messagePreview, int avatarResId, boolean isSelected) {
        this.name = name;
        this.messagePreview = messagePreview;
        this.avatarResId = avatarResId;
        this.isSelected = isSelected;
    }

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
