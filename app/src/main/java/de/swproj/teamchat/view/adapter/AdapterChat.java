package de.swproj.teamchat.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import de.swproj.teamchat.Connection.database.DBStatements;
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
    private boolean darkColor = false;

    // Set to false if you want Chat List to have white background + black text
    // Set to true if you want colored background (with white text, if chatColor is too dark)
    private boolean coloredBackground = false;


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
        final Chat chat = chats.get(position);

    //    if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listitem_chat, null, false);

            // Color
            View colorBox = convertView.findViewById(R.id.list_color_box);
            colorBox.setBackgroundColor(chat.getColor());

            // Colored Background
        View colorBackground = convertView.findViewById(R.id.list_color_background);
        if (coloredBackground) {
            int red = Color.red(chat.getColor());
            int green = Color.green(chat.getColor());
            int blue = Color.blue(chat.getColor());
            float[] hsv = new float[3];
            Color.RGBToHSV(red, green, blue, hsv);
            // reduce saturation to 20%
            hsv[1] = hsv[1] * 0.20f;
            if (hsv[2] < 0.66f) {
                darkColor = true;
                hsv[2] = 0.2f;
            } else
                darkColor = false;
            colorBackground.setBackgroundColor(Color.HSVToColor(hsv));
            // Check if the color is too dark for black text
        }
        else {
            colorBackground.setBackgroundColor(0xFFFFFFFF);
            darkColor = false;
        }


            // Name
            TextView chatName = (TextView) convertView.findViewById(R.id.chatListChatName);
            chatName.setText(chat.getName());
            // Set textcolor to white if the color of the background is too dark
            if (darkColor)
                chatName.setTextColor(0xFFCAC8CA);

            // Last message
            TextView lastMessage = (TextView) convertView.findViewById(R.id.chatListLastMessage);
            final Message lastMsg = db.getLastMessage(chat.getId());
            if (darkColor)
                lastMessage.setTextColor(0xFFFFFFFF);


            // Avoid NullpointerException if Chat is empty
            if (lastMsg == null) {
                TextView messageDate = (TextView) convertView.findViewById(R.id.chatListLastMessageDate);
                messageDate.setText("10.10.2025");
                if (darkColor)
                    messageDate.setTextColor(0xFFCAC8CA);
            } else {
                lastMessage.setText(lastMsg.getMessage());

                // Date
                TextView messageDate = (TextView) convertView.findViewById(R.id.chatListLastMessageDate);
                messageDate.setText(lastMsg.getTimeStamp().toString());
                if (darkColor)
                    messageDate.setTextColor(0xFFCAC8CA);
            }


      //  }

        return convertView;
    }
}
