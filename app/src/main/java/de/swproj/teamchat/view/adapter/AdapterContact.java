package de.swproj.teamchat.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import de.swproj.teamchat.R;
import de.swproj.teamchat.datamodell.chat.User;


/*
 * Created by Manuel Lanzinger on 03. November 2019.
 * For the project: TeamChat.
 */

public class AdapterContact extends BaseAdapter {

    private List<User> contacts;

    public AdapterContact(List<User> contacts) {
        this.contacts = contacts;
    }

    public void add(User user) {
        contacts.add(user);
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Object getItem(int position) {
        return contacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the specific User
        User contact = contacts.get(position);

        Context context = parent.getContext();

        // Set the Layout Document
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listitem_user, null, false);
        }

        // Get all Views out of the listitem
        TextView icon = (TextView)convertView.findViewById(R.id.li_user_icon);
        TextView accName = (TextView)convertView.findViewById(R.id.li_user_accname);
        TextView forename = (TextView)convertView.findViewById(R.id.li_user_fname);
        TextView lastname = (TextView)convertView.findViewById(R.id.li_user_lname);

        // Get information of the user Object
        String userForename = contact.getFirstName();
        String userLastname = contact.getName();

        icon.setText(userForename.toUpperCase().charAt(0) + "" + userLastname.toUpperCase().charAt(0));
        accName.setText(contact.getAccountName());
        forename.setText(userForename);
        lastname.setText(userLastname);

        return convertView;
    }
}
