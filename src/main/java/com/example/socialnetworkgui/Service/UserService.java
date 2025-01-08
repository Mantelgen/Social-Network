package com.example.socialnetworkgui.Service;

import com.example.socialnetworkgui.Domain.Exceptions.ServiceException;
import com.example.socialnetworkgui.Domain.Friendship;
import com.example.socialnetworkgui.Domain.Tuple;
import com.example.socialnetworkgui.Domain.User;
import com.example.socialnetworkgui.Repository.Repository;
import com.example.socialnetworkgui.Utils.Events.ChangeEventType;
import com.example.socialnetworkgui.Utils.Events.UserEntityChangeEvent;
import com.example.socialnetworkgui.Utils.Observer.Observable;
import com.example.socialnetworkgui.Utils.Observer.Observer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserService implements Observable<UserEntityChangeEvent> {
    private final Repository<Long, User> userRepo;
    private final Repository<Tuple<Long,Long>, Friendship> friendshipRepo;
    private List<Observer<UserEntityChangeEvent>> observers = new ArrayList<>();

    public UserService(Repository<Long, User> userRepo, Repository<Tuple<Long, Long>, Friendship> friendshipRepo) {
        this.userRepo = userRepo;
        this.friendshipRepo = friendshipRepo;
    }
    public Iterable<User> getAllUsers() {
        return userRepo.findAll();
    }

    public User deleteUser(Long id){

        Optional<User>user = userRepo.findOne(id);
        if(user.isPresent()){
            List<Friendship> toRemove = StreamSupport.stream(friendshipRepo.findAll().spliterator(),false).
                    filter(f->f.getFirst().equals(id)||f.getSecond().equals(id)).collect(Collectors.toList());
            toRemove.forEach(f->friendshipRepo.delete(f.getId()));
            userRepo.delete(id);
            UserEntityChangeEvent event = new UserEntityChangeEvent(ChangeEventType.DELETE,user.get());
            notifyObservers(event);
            return user.get();
        }
        return null;
    }
    public User findUser(Long id){
        Optional<User>user = userRepo.findOne(id);
        return user.orElse(null);
    }


    public User addUser(Long id, String firstName, String lastName, LocalDate birthday, String email, String password) {
        User user = new User(firstName,lastName,birthday,email,password);
        user.setId(id);
        Optional<User> res=userRepo.save(user);
        if(res.isPresent()){
            throw new ServiceException("Error adding the user!");
        }
        UserEntityChangeEvent event = new UserEntityChangeEvent(ChangeEventType.ADD,user);
        notifyObservers(event);
        return user;
    }
    public User updateUser(Long id, String firstName, String lastName, LocalDate birthday, String email, String password) {
        return null;
    }

    public Optional<User> getUserByMail(String email) {
        Iterable<User> users = userRepo.findAll();
        ArrayList<User> usrs = new ArrayList<>();
        users.forEach(usrs::add);
        User result = usrs.stream().filter(u -> email.equals(u.getEmail())).findFirst().orElse(null);
        return Optional.ofNullable(result);
    }


    @Override
    public void addObserver(Observer<UserEntityChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<UserEntityChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(UserEntityChangeEvent t) {
        observers.forEach(e -> e.update(t));
    }
}
