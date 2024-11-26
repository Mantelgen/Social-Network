package com.example.socialnetworkgui.Domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Message extends Entity<Long> {
    private User from;
    private List<User> to;
    private String message;
    private LocalDateTime date;
    private int replay;

    public Message(User from, List<User> to, String message, LocalDateTime date) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
        this.replay = 0;
    }

    public Message(User from, List<User> to, String message, LocalDateTime date, int replay) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
        this.replay = replay;
    }
    public Message(User from, User to, String message,LocalDateTime date, int replay) {
        this.from = from;
        this.to = new ArrayList<User>();
        this.to.add(to);
        this.message = message;
        this.date = date;
        this.replay = replay;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public List<User> getTo() {
        return to;
    }

    public void setTo(List<User> to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public int getReplay() {
        return replay;
    }

    public void setReplay(int replay) {
        this.replay = replay;
    }

    @Override
    public String toString() {
        return "Message{" +
                "from=" + from +
                ", to=" + to +
                ", message='" + message + '\'' +
                ", date=" + date +
                ", replay=" + replay +
                '}';
    }
}
