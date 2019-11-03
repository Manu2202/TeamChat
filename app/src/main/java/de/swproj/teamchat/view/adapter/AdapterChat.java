package de.swproj.teamchat.view.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import de.swproj.teamchat.datamodell.chat.Chat;


/*
 * Created by Manuel Lanzinger on 03. November 2019.
 * For the project: TeamChat.
 */

public class AdapterChat extends BaseAdapter {

    private ArrayList<Chat> chats;

    public AdapterChat(ArrayList<Chat> chats) {
        this.chats = chats;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
