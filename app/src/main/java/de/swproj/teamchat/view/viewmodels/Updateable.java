package de.swproj.teamchat.view.viewmodels;

import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.datamodell.chat.ChatMembers;
import de.swproj.teamchat.datamodell.chat.Message;
import de.swproj.teamchat.datamodell.chat.User;
import de.swproj.teamchat.datamodell.chat.UserEventStatus;

public interface Updateable {
 //   public void insertObject(Object obj);
    public void insertObject(Chat obj);
    public void insertObject(Message obj);
    public void insertObject(User obj);

    // public void updateObject(Object obj);
    public void updateObject(Chat obj);
    public void updateObject(User obj);
    public void updateObject(UserEventStatus obj);
    public void updateObject(ChatMembers chatMembers);

    //todo: implement update event



    //implemtation if there is time
   // public void removeObject(Object obj);
}
