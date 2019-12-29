package de.swproj.teamchat.view.viewmodels;

import java.util.ArrayList;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.datamodell.chat.Message;
import de.swproj.teamchat.datamodell.chat.User;

public class ChatViewModel extends ViewModel implements Updateable {
    MutableLiveData<Chat> liveChat = new MutableLiveData<>();
    MutableLiveData<ArrayList<Message>> liveMessages = new MutableLiveData<>();


    public ChatViewModel(Chat chat, ArrayList<Message> messages) {
        liveChat.setValue(chat);
        liveMessages.setValue(messages);
    }

    public MutableLiveData<Chat> getLiveChat() {
        return liveChat;
    }

    public void setLiveChat(Chat chat) {
        liveChat.postValue(chat);
    }

    public MutableLiveData<ArrayList<Message>> getLiveMessages() {
        return liveMessages;
    }

    public void insertLiveMessage(Message message) {

            liveMessages.getValue().add(message);
            liveMessages.postValue(liveMessages.getValue());




    }

    @Override
    public void insertObject(Object obj) {

    }

    @Override
    public void insertObject(Chat obj) {

    }

    @Override
    public void insertObject(User obj) {

    }

    @Override
    public void insertObject(Message message){
        if(liveChat.getValue().getId().equals(message.getChatid()))
        insertLiveMessage(message);
    }



    @Override
    public void updateObject(Object obj) {

    }
    public void updateObject(Chat chat){
        if(liveChat.getValue().getId().equals(chat.getId()))
        setLiveChat(chat);
    }


}
