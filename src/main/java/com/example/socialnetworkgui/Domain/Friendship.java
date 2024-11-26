package com.example.socialnetworkgui.Domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Friendship extends Entity<Tuple<Long,Long>> {

    LocalDateTime date;
    FriendshipStatus status;

    public Friendship(Tuple<Long,Long> id) {

        super.setId(id);
        date = LocalDateTime.now();
        this.status= FriendshipStatus.PENDING;
    }

    public Friendship(Tuple<Long,Long> id, LocalDateTime date) {
        super.setId(id);
        this.date = date;
        this.status = FriendshipStatus.PENDING;
    }

    public Friendship(Tuple<Long,Long> id,LocalDateTime date,FriendshipStatus status) {
        super.setId(id);
        this.status = status;
        this.date = date;
    }

    public FriendshipStatus getStatus() {
        return status;
    }

    public void setStatus(FriendshipStatus status) {
        this.status = status;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    public String getStringDate(){
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    public LocalDateTime getDate() {
        return date;
    }

    public Long getFirst(){
        return super.getId().getLeft();
    }
    public Long getSecond(){
        return super.getId().getRight();
    }

    public void setFirst(Long first) {
        super.getId().setLeft(first);
    }
    public void setSecond(Long second) {
        super.getId().setRight(second);
    }

    @Override
    public String toString() {
        return "Friendship{" +
                "user1=" + getFirst() +
                ", user2=" + getSecond() +
                ", date=" + getDate() +
                ", status=" + getStatus() +
                '}';
    }
}
