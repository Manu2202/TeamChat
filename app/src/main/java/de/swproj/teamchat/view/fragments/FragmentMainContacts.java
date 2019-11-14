package de.swproj.teamchat.view.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;
import de.swproj.teamchat.R;


/*
 * Created by Manuel Lanzinger on 14. November 2019.
 * For the project: TeamChat.
 */

public class FragmentMainContacts extends ListFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //TODO: Den Adapter erzeugen und mit Werten füllen und auf ListFragment setzen
        Log.d("Fragments:", "In Contact Fragment");

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListView list = getListView();
        String[] s = new String[]{"Hier", "Gibt", "Es", "Stabile", "Kontakte", "In", "Deiner", "Nähe"};
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.dummy_list_item, R.id.textView, s);
        setListAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //TODO: Konfiguration des Click Listener auf der ListView
            }
        });
    }
}
