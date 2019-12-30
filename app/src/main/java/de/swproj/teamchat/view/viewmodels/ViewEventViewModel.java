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

public class ViewEventViewModel extends Updateable {
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



    private void updateStatus(UserEventStatus status){
      if(myLiveState.getValue().getUserId().equals(status.getUserId())){
        myLiveState.postValue(status);
      }
        List<UserEventStatus> stats = liveStates.getValue();
        for (int i = 0; i < stats.size(); i++) {
          if (stats.get(i).getUserId().equals(status.getUserId())) {
            stats.set(i, status);
            liveStates.postValue(stats);
            break;
          }
      }
    }

    private void insertStatus(UserEventStatus status){
      liveStates.getValue().add(status);
      liveStates.postValue(liveStates.getValue());
    }


    //todo: implement update event
  //todo: implement insert UserEvent State

    @Override
    public void updateObject(UserEventStatus obj) {
      if(obj.getEventId().equals(liveEvent.getValue().getId())){
        updateStatus(obj);
      }

    }


}
