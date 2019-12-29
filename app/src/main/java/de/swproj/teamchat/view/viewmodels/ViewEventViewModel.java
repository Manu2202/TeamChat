package de.swproj.teamchat.view.viewmodels;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.datamodell.chat.ChatMembers;
import de.swproj.teamchat.datamodell.chat.Event;
import de.swproj.teamchat.datamodell.chat.Message;
import de.swproj.teamchat.datamodell.chat.User;
import de.swproj.teamchat.datamodell.chat.UserEventStatus;

public class ViewEventViewModel extends ViewModel implements Updateable {
  private MutableLiveData<UserEventStatus> myLiveState=new MutableLiveData<>();
  private MutableLiveData<List<UserEventStatus>> liveStates= new MutableLiveData<>();
  private MutableLiveData<Event> liveEvent = new MutableLiveData<>();

    public ViewEventViewModel(UserEventStatus myState, List<UserEventStatus> states, Event event) {
       myLiveState.setValue(myState);
       liveEvent.setValue(event);
       liveStates.setValue(states);
    }

    public MutableLiveData<UserEventStatus> getMyLiveState() {
        return myLiveState;
    }

    public MutableLiveData<List<UserEventStatus>> getLiveStates() {
        return liveStates;
    }

    public MutableLiveData<Event> getLiveEvent() {
        return liveEvent;
    }

    @Override
    public void insertObject(Chat obj) {

    }

    @Override
    public void insertObject(Message obj) {

    }

    @Override
    public void insertObject(User obj) {

    }

    @Override
    public void updateObject(Chat obj) {

    }

    @Override
    public void updateObject(User obj) {

    }

    @Override
    public void updateObject(UserEventStatus obj) {

    }

    @Override
    public void updateObject(ChatMembers chatMembers) {

    }
}
