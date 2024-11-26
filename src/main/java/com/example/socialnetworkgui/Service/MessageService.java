package com.example.socialnetworkgui.Service;

import com.example.socialnetworkgui.Domain.Exceptions.ServiceException;
import com.example.socialnetworkgui.Domain.Message;
import com.example.socialnetworkgui.Domain.User;
import com.example.socialnetworkgui.Repository.Repository;
import com.example.socialnetworkgui.Utils.Events.ChangeEventType;
import com.example.socialnetworkgui.Utils.Events.FriendshipEntityChangeEvent;
import com.example.socialnetworkgui.Utils.Events.MessageEntityChangeEvent;
import com.example.socialnetworkgui.Utils.Observer.Observable;
import com.example.socialnetworkgui.Utils.Observer.Observer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageService implements Observable<MessageEntityChangeEvent> {
    private final Repository<Long, User> userRepo;
    private final Repository<Long, Message> messageRepo;
    private List<Observer<MessageEntityChangeEvent>> observers = new ArrayList<>();

    public MessageService(Repository<Long, User> userRepo, Repository<Long, Message> messageRepo) {
        this.userRepo = userRepo;
        this.messageRepo = messageRepo;
    }

    public Iterable<Message> getAllMessages() {
        return messageRepo.findAll();
    }
    public Message deleteMessage(Long idMessage) {
        Optional<Message> mesg = messageRepo.delete(idMessage);
        MessageEntityChangeEvent event = new MessageEntityChangeEvent(ChangeEventType.DELETE,mesg.get());
        notifyObservers(event);
        return mesg.get();
    }
    public Message addMessage(Long id,User from, User to, String message, int replay) {
        DateTimeFormatter formatter =DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.parse(LocalDateTime.now().format(formatter), formatter);
        Message msg = new Message(from,to,message, now,replay);
        msg.setId(id);
        Optional<Message> m=messageRepo.save(msg);
        if(m.isPresent()) {
            throw new ServiceException("Error adding the message!");
        }
        MessageEntityChangeEvent event = new MessageEntityChangeEvent(ChangeEventType.ADD,msg);
        notifyObservers(event);
        return msg;
    }
    @Override
    public void addObserver(Observer<MessageEntityChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<MessageEntityChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(MessageEntityChangeEvent t) {
        observers.forEach(e -> e.update(t));
    }
}
