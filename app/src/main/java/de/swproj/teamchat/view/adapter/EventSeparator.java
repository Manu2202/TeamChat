package de.swproj.teamchat.view.adapter;

import de.swproj.teamchat.datamodell.chat.Event;

public class EventSeparator {
    private boolean needsSeparator;
    private Event ev;

    public EventSeparator(boolean needsSeparator, Event ev) {
        this.needsSeparator = needsSeparator;
        this.ev = ev;
    }

    public boolean needsSeparator() {
        return needsSeparator;
    }

    public void setSeparator(boolean needsSeparator) {
        this.needsSeparator = needsSeparator;
    }

    public Event getEv() {
        return ev;
    }

    public void setEv(Event ev) {
        this.ev = ev;
    }
}
