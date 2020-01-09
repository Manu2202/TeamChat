package de.swproj.teamchat.view.viewmodels;

import androidx.lifecycle.ViewModel;
import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.datamodell.chat.ChatMembers;
import de.swproj.teamchat.datamodell.chat.Event;
import de.swproj.teamchat.datamodell.chat.Message;
import de.swproj.teamchat.datamodell.chat.User;
import de.swproj.teamchat.datamodell.chat.UserEventStatus;

public abstract class Updateable extends ViewModel {
 //   public void insertObject(Object obj){}
    public void insertObject(Chat obj){}
    public void insertObject(Message obj){}
    public void insertObject(User obj){}
    public void insertObject(Event obj){}

    // public void updateObject(Object obj){}
    public void updateObject(Chat obj){}
    public void updateObject(User obj){}
    public void updateObject(UserEventStatus obj){}
    public void updateObject(ChatMembers chatMembers){}
    public void updateObject(Event obj){}


   //implemtation if there is time
   // public void removeObject(Object obj);
    public void removeObject(User obj){}
    public void removeObject(UserEventStatus obj){}
}
