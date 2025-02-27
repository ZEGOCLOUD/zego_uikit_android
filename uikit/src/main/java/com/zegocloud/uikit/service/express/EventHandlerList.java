package com.zegocloud.uikit.service.express;

import androidx.core.util.Consumer;
import java.util.ArrayList;
import java.util.List;

public class EventHandlerList<T> {

    private List<T> handlerList = new ArrayList<>();
    private List<T> autoDeleteHandlerList = new ArrayList<>();


    public void addEventHandler(T eventHandler, boolean autoDelete) {
        if (autoDelete) {
            autoDeleteHandlerList.add(eventHandler);
        } else {
            handlerList.add(eventHandler);
        }
    }

    public void removeEventHandler(T eventHandler) {
        autoDeleteHandlerList.remove(eventHandler);
        handlerList.remove(eventHandler);
    }


    public void removeAutoDeleteRoomListeners() {
        autoDeleteHandlerList.clear();
    }

    public void clear(){
        removeAutoDeleteRoomListeners();
        handlerList.clear();
    }

    public List<T> getAutoDeleteHandlerList() {
        return autoDeleteHandlerList;
    }

    public List<T> getHandlerList() {
        return handlerList;
    }

    public void notifyAllListener(Consumer<T> notifier) {
        for (T t : autoDeleteHandlerList) {
            if (t != null) {
                notifier.accept(t);
            }
        }
        for (T t : handlerList) {
            notifier.accept(t);
        }
    }
}
