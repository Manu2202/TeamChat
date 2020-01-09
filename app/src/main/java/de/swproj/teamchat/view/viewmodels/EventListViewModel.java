package de.swproj.teamchat.view.viewmodels;

/*
 * Created by Manuel Lanzinger on 08. Januar 2020.
 * For the project: TeamChat.
 */

import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import de.swproj.teamchat.datamodell.chat.Event;
import de.swproj.teamchat.datamodell.chat.Message;
import de.swproj.teamchat.view.adapter.EventSeparator;

public class EventListViewModel extends Updateable {

    // Initialize the Mutable Live Data as a List
    private MutableLiveData<List<EventSeparator>> liveEvents = new MutableLiveData<>();
    private LinkedList <Event> events;

    // Constructor
    public EventListViewModel(List<EventSeparator> events) {
        liveEvents.setValue(events);
        setEvents(events);
    }

    // Getter for the LiveEvents
    public MutableLiveData<List<EventSeparator>> getLiveEvents() {
        return liveEvents;
    }

    private void insertLiveEvent(Event event) {


        // Check if new event is the nearest
        if (events.getFirst().getDate().compareTo(event.getDate()) <= 0) {
            events.add(0, event);

            for (int i = 1; i < liveEvents.getValue().size(); i++) {
                if (events.get(i).getDate().compareTo(event.getDate()) > 0) {
                    events.add(i, event);
                    break;
                }
            }
            liveEvents.postValue(prepareList(events));
        }
    }

    private void setEvents(List<EventSeparator> list){
        events = new LinkedList<>();
        for (EventSeparator es: list
             ) {
            events.add(es.getEv());
        }

    }

    private List<EventSeparator> prepareList(List<Event> events) {
        List<EventSeparator> eventsAndSeparators = liveEvents.getValue();
        eventsAndSeparators.clear();
        if (events.size() > 0) {
            Collections.sort(events);
            EventSeparator prevEvSep = null;

            for (Event ev : events) {
                if (prevEvSep != null) {
                    if ((ev.getDate().get(Calendar.YEAR) != prevEvSep.getEv().getDate().get(Calendar.YEAR)) ||
                            (ev.getDate().get(Calendar.MONTH) != prevEvSep.getEv().getDate().get(Calendar.MONTH))) {
                        prevEvSep = new EventSeparator(true, ev);
                    } else {
                        prevEvSep = new EventSeparator(false, ev);
                    }
                } else {
                    prevEvSep = new EventSeparator(true, ev);
                }

                eventsAndSeparators.add(prevEvSep);
            }
        }

        return  eventsAndSeparators;
    }

    // Find an Event by ID
    private int findEvent(String eventID) {
        int pos = -1;
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getId().equals(eventID))
                return i;
        }
        return pos;
    }

    @Override
    public void insertObject(Message obj) {
        if (obj != null) {
            if(obj.isEvent())
                insertLiveEvent((Event) obj);
        }
    }

    @Override
    public void updateObject(Event obj) {
        if (obj != null) {

            try {
                events.remove(findEvent(obj.getId()));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            insertLiveEvent(obj);
        }
    }
}
