package de.swproj.teamchat.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Collections;
import java.util.List;

import de.swproj.teamchat.R;
import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.datamodell.chat.Chat;
import de.swproj.teamchat.datamodell.chat.Message;
import de.swproj.teamchat.datamodell.chat.User;
import de.swproj.teamchat.helper.FormatHelper;


/*
 * Created by Manuel Lanzinger on 03. November 2019.
 * For the project: TeamChat.
 */

public class AdapterChat extends BaseAdapter {

    private List<Chat> chats;


    public AdapterChat(List<Chat> chats) {
        this.chats = chats;
        Collections.sort(this.chats, Collections.reverseOrder());
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
        try {
            colorBox.setBackgroundColor(chat.getColor());
        } catch (Exception e) {
            e.printStackTrace();
            colorBox.setBackgroundColor(parent.getResources().getColor(R.color.black));
        }

        // Name
        TextView chatName = (TextView) convertView.findViewById(R.id.chatListChatName);
        chatName.setText(chat.getName());

        // Last message
        TextView lastMessage = (TextView) convertView.findViewById(R.id.chatListLastMessage);
        final Message lastMsg = DBStatements.getLastMessage(chat.getId());

        // Avoid NullpointerException if Chat is empty
        if (lastMsg != null) {
            if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(lastMsg.getCreator())) {
                lastMessage.setText("Me: " + lastMsg.getMessage());
            } else {
                User msgSender = DBStatements.getUser(lastMsg.getCreator());
                lastMessage.setText(msgSender.getFirstName() + ": " + lastMsg.getMessage());
            }

            // Date
            TextView messageDate = (TextView) convertView.findViewById(R.id.chatListLastMessageDate);
            messageDate.setText(FormatHelper.formatHoursMinutesFromDate(lastMsg.getTimeStampDate()));


        }


        return convertView;
    }
}
