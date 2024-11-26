package com.example.socialnetworkgui.Utils.Observer;

import com.example.socialnetworkgui.Utils.Events.Event;

public interface Observer<E extends Event> {
    void update(E event);
}
