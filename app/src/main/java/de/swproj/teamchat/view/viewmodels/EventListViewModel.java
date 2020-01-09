package de.swproj.teamchat.view.viewmodels;

/*
 * Created by Manuel Lanzinger on 08. Januar 2020.
 * For the project: TeamChat.
 */

import java.util.LinkedList;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import de.swproj.teamchat.datamodell.chat.Event;
import de.swproj.teamchat.datamodell.chat.Message;

public class EventListViewModel extends Updateable {

    // Initialize the Mutable Live Data as a List
    private MutableLiveData<LinkedList<Event>> liveEvents = new MutableLiveData<>();

    // Constructor
    public EventListViewModel(LinkedList<Event> events) {
        liveEvents.setValue(events);
    }

    // Getter for the LiveEvents
    public MutableLiveData<LinkedList<Event>> getLiveEvents() {
        return liveEvents;
    }

    private void insertLiveEvent(Event event) {
        for (int i = 0; i < liveEvents.getValue().size(); i++) {
            // Check if new event is the nearest
            if (liveEvents.getValue().get(i).getDate().compareTo(event.getDate()) <= 0 && i == 0) {
                liveEvents.getValue().add(i, event);
                break;
            }
            if (liveEvents.getValue().get(i).getDate().compareTo(event.getDate()) > 0) {
                liveEvents.getValue().add(i, event);
                break;
            }
        }
        liveEvents.postValue(liveEvents.getValue());
    }

    // Find an Event by ID
    private int findEvent(String eventID) {
        int pos = -1;
        List<Event> events = liveEvents.getValue();
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
            List<Event> events = liveEvents.getValue();

            try {
                events.remove(findEvent(obj.getId()));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            insertLiveEvent(obj);
        }
    }
}
