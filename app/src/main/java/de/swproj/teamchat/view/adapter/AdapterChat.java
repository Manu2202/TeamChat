package de.swproj.teamchat.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.R;
import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.datamodell.chat.Message;


/*
 * Created by Manuel Lanzinger on 03. November 2019.
 * For the project: TeamChat.
 */

public class AdapterChat extends BaseAdapter {

    private ArrayList<Chat> chats;
    private DBStatements db;

    public AdapterChat(ArrayList<Chat> chats, DBStatements dbStatements) {
        this.chats = chats;
        db = dbStatements;
    }

    @Override
    public int getCount() {
        return chats.size();
    }

    @Override
    public Chat getItem(int position) {
        return chats.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        Chat chat = chats.get(position);


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.listitem_chat, null, false);

        // Color
        View colorBox = convertView.findViewById(R.id.list_color_box);
        colorBox.setBackgroundColor(chat.getColor());

        // Name
        TextView chatName = (TextView) convertView.findViewById(R.id.chatListChatName);
        chatName.setText(chat.getName());

        // Last message
        TextView lastMessage = (TextView) convertView.findViewById(R.id.chatListLastMessage);
        final Message lastMsg = db.getLastMessage(chat.getId());

        //TODO LAST MESSAGE NOT WORKING

        // Avoid NullpointerException if Chat is empty
        if (lastMsg == null) {
        } else {
            lastMessage.setText(lastMsg.getMessage());

            // Date
            TextView messageDate = (TextView) convertView.findViewById(R.id.chatListLastMessageDate);
            messageDate.setText(lastMsg.getTimeStamp().toString());
        }


        return convertView;
    }
}
