package com.gicha.flown;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class RoomsAction extends Fragment {  //Фрагмент списка всех пользователей в онлайне
    Core core;
    SimpleAdapter adapter;
    EditText etxMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        core = Core.getInstance();
        View roomsActionFragmentView = inflater.inflate(R.layout.rooms_action, container, false);

        ListView lvMassages = (ListView) roomsActionFragmentView.findViewById(R.id.rooms);

        adapter = new SimpleAdapter(getActivity(), core.myUserList,
                R.layout.rooms_row,
                new String[]{"time", "login"},
                new int[]{R.id.colTime, R.id.colLogin});

        lvMassages.setAdapter(adapter);


        return roomsActionFragmentView;
    }


}