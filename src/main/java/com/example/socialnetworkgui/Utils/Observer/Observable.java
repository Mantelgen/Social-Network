package com.example.socialnetworkgui.Utils.Observer;

import com.example.socialnetworkgui.Utils.Events.Event;

public interface Observable<E extends Event>{
    void addObserver(Observer<E> e);
    void removeObserver(Observer<E> e);
    void notifyObservers(E t);
}
