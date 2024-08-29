package com.example.blackhole;

public class MessageData {
    private String datetime;
    private String phone_number;
    private String recipient;
    private String text;
    private String status;

    public MessageData(String datetime, String phone_number, String recipient, String text, String status) {
        this.datetime = datetime;
        this.phone_number = phone_number;
        this.recipient = recipient;
        this.text = text;
        this.status = status;
    }

    // Геттеры и сеттеры
}
