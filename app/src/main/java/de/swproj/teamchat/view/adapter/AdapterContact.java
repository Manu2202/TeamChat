package de.swproj.teamchat.view.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import de.swproj.teamchat.datamodell.chat.User;


/*
 * Created by Manuel Lanzinger on 03. November 2019.
 * For the project: TeamChat.
 */

public class AdapterContact extends BaseAdapter {

    private ArrayList<User> contacts;

    public AdapterContact(ArrayList<User> contacts) {
        this.contacts = contacts;
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
