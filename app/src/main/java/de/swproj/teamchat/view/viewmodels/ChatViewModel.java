package de.swproj.teamchat.view.viewmodels;

import java.util.ArrayList;

import androidx.lifecycle.MutableLiveData;
import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.datamodell.chat.Message;

public class ChatViewModel extends Updateable {
    private MutableLiveData<Chat> liveChat = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Message>> liveMessages = new MutableLiveData<>();


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
    public void insertObject(Message message) {
        if (liveChat.getValue().getId().equals(message.getChatid()))
            insertLiveMessage(message);
    }


    @Override
    public void updateObject(Chat chat) {
        if (liveChat.getValue().getId().equals(chat.getId()))
            setLiveChat(chat);
    }


}
