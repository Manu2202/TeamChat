package de.swproj.teamchat.view.viewmodels;

import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.datamodell.chat.Message;
import de.swproj.teamchat.datamodell.chat.User;

public interface Updateable {
    public void insertObject(Object obj);
    public void insertObject(Chat obj);
    public void insertObject(Message obj);
    public void insertObject(User obj);

    public void updateObject(Object obj);
    //implemtation if there is time
   // public void removeObject(Object obj);
}
