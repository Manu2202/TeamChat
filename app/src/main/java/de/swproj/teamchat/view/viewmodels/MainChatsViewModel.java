package de.swproj.teamchat.view.viewmodels;

import java.util.LinkedList;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.datamodell.chat.Message;

public class MainChatsViewModel extends Updateable {
    private MutableLiveData<LinkedList<Chat>> liveChats = new MutableLiveData<>();

    public MainChatsViewModel(LinkedList<Chat> chats) {
        liveChats.setValue(chats);
    }

    public MutableLiveData<LinkedList<Chat>> getLiveChats() {
        return liveChats;
    }

    private void insertLiveChat(Chat chat) {
        liveChats.getValue().add(0, chat);
        liveChats.postValue(liveChats.getValue());

    }

    private int findChat(String chatID) {
        int res = -1;
        List<Chat> chats = liveChats.getValue();
        for (int i = 0; i < chats.size(); i++) {
            if (chats.get(i).getId().equals(chatID)) {
                return i;
            }
        }


        return res;
    }

    @Override
    public void updateObject(Chat obj) {
        if (obj != null) {
            List<Chat> chats = liveChats.getValue();
            Chat chat = obj;
            try {
                chats.remove(findChat(obj.getId()));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            insertLiveChat(chat);

        }
    }


    @Override
    public void insertObject(Chat obj) {
        if (obj != null)
            insertLiveChat(obj);
    }

    @Override
    public void insertObject(Message obj) {
        if (obj != null) {
            List<Chat> chats = liveChats.getValue();
            Chat chat = null;
            try {
                chat = chats.remove(findChat(obj.getChatid()));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            insertLiveChat(chat);

        }
    }

    @Override
    public void removeObject(Chat obj) {
        if (obj != null) {
            LinkedList<Chat> chats = liveChats.getValue();
            Chat chat = obj;
            try {
                chats.remove(findChat(obj.getId()));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

             liveChats.postValue(chats);
        }
    }
}
