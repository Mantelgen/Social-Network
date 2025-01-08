package com.example.socialnetworkgui.Repository.Database.Paging;

import com.example.socialnetworkgui.Domain.FriendshipStatus;
import com.example.socialnetworkgui.Domain.Tuple;
import com.example.socialnetworkgui.Domain.User;

import java.time.LocalDateTime;
import java.util.Optional;

public class FriendshipDTO {
    private Optional<Long> user = Optional.empty();
    private Optional<Long> user2 = Optional.empty();
    private Optional<LocalDateTime> date  = Optional.empty();
    private Optional<FriendshipStatus> status = Optional.empty();

    public Optional<Long> getUser() {
        return user;
    }

    public void setUser(Optional<Long> user) {
        this.user = user;
    }

    public Optional<Long> getUser2() {
        return user2;
    }

    public void setUser2(Optional<Long> user2) {
        this.user2 = user2;
    }

    public Optional<LocalDateTime> getDate() {
        return date;
    }

    public void setDate(Optional<LocalDateTime> date) {
        this.date = date;
    }

    public Optional<FriendshipStatus> getStatus() {
        return status;
    }

    public void setStatus(Optional<FriendshipStatus> status) {
        this.status = status;
    }
}
