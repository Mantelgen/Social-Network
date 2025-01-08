package com.example.socialnetworkgui.Service;

import com.example.socialnetworkgui.Domain.Friendship;
import com.example.socialnetworkgui.Domain.FriendshipStatus;
import com.example.socialnetworkgui.Domain.Tuple;
import com.example.socialnetworkgui.Domain.User;
import com.example.socialnetworkgui.Repository.Database.Paging.FriendshipDBPagingRepository;
import com.example.socialnetworkgui.Repository.Database.Paging.FriendshipDTO;
import com.example.socialnetworkgui.Repository.Repository;
import com.example.socialnetworkgui.Utils.Events.ChangeEventType;
import com.example.socialnetworkgui.Utils.Events.FriendshipEntityChangeEvent;
import com.example.socialnetworkgui.Utils.Observer.Observable;
import com.example.socialnetworkgui.Utils.Observer.Observer;
import com.example.socialnetworkgui.Utils.paging.Page;
import com.example.socialnetworkgui.Utils.paging.Pageable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendshipService implements Observable<FriendshipEntityChangeEvent> {

    private final Repository<Long, User> userRepo;
    private final FriendshipDBPagingRepository friendshipRepo;
    private List<Observer<FriendshipEntityChangeEvent>> observers = new ArrayList<>();

    public FriendshipService(Repository<Long, User> userRepo, FriendshipDBPagingRepository friendshipRepo) {
        this.userRepo = userRepo;
        this.friendshipRepo = friendshipRepo;
    }
    public Iterable<Friendship> getAllFriendships() {
        return friendshipRepo.findAll();
    }

    public Friendship deleteFriendship( Tuple<Long,Long> friendshipId) {
        Optional<Friendship> friendship = friendshipRepo.delete(friendshipId);
        FriendshipEntityChangeEvent event = new FriendshipEntityChangeEvent(ChangeEventType.DELETE,friendship.get());
        notifyObservers(event);
        return friendship.orElse(null);
    }
    public Friendship addFriendship(Tuple<Long,Long> friendshipId) {
            if(friendshipRepo.findOne(friendshipId).isPresent() || friendshipRepo.findOne(new Tuple<>(friendshipId.getRight(), friendshipId.getLeft())).isPresent())
                return null;
            Optional<User> o1 = userRepo.findOne(friendshipId.getLeft());
            Optional<User> o2 = userRepo.findOne(friendshipId.getRight());
            if(o1.isPresent() && o2.isPresent()) {

                DateTimeFormatter formatter
                        = DateTimeFormatter.ofPattern(
                        "yyyy-MM-dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                String dateTimeString = now.format(formatter);
                LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);
                Friendship friendship = new Friendship(friendshipId,dateTime);
                friendshipRepo.save(friendship);
                FriendshipEntityChangeEvent event = new FriendshipEntityChangeEvent(ChangeEventType.ADD,friendship);
                notifyObservers(event);
                return friendship;
            }
            return null;
    }
    public Friendship setStatus(Tuple<Long,Long> friendshipId,FriendshipStatus status) {
        Optional<Friendship> fr = friendshipRepo.findOne(friendshipId);
        if(fr.isPresent()) {
            Friendship friendship = fr.get();
            friendship.setStatus(status);
            friendshipRepo.update(friendship);
            FriendshipEntityChangeEvent event = new FriendshipEntityChangeEvent(ChangeEventType.UPDATE,friendship);
            notifyObservers(event);
            return friendship;
        }
        return null;
    }
    @Override
    public void addObserver(Observer<FriendshipEntityChangeEvent> e) {
            observers.add(e);
    }

    @Override
    public void removeObserver(Observer<FriendshipEntityChangeEvent> e) {
            observers.remove(e);
    }

    @Override
    public void notifyObservers(FriendshipEntityChangeEvent t) {
        observers.forEach(e -> e.update(t));
    }
    public Page<Friendship> findAllOnPage(Pageable pageable, FriendshipDTO filter) {
        return friendshipRepo.findAllOnPage(pageable,filter);
    }

    public long numberOfPending(User user) {
        return StreamSupport.stream(friendshipRepo.findAll().spliterator(), false).
                filter(fr->fr.getFirst().equals(user.getId()) && fr.getStatus().equals(FriendshipStatus.PENDING))
                .count();
    }
    public List<Friendship> findActualFriendships(User user, Page<Friendship> page, FriendshipStatus status) {
        return StreamSupport.stream(page.getElementsOnPage().spliterator(), false).
                filter(fr->fr.getStatus().equals(status))
                .collect(Collectors.toList());
    }
}
