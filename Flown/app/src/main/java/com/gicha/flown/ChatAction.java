package com.gicha.flown;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

//Класс фрагмента чата
public class ChatAction extends Fragment implements Core.OnAddMessageListener, View.OnClickListener, AdapterView.OnItemLongClickListener {
    public Core core;
    ArrayList<HashMap<String, String>> myArrList; //массив сообщений, которыедолжны будут вывестись на экран
    SimpleAdapter adapter; //для отображения сообщений мы используем simpleAdapter
    EditText etxMessage;
    ListView lvMassages;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myArrList = new ArrayList<>();
        core = Core.getInstance();
        View chatActionFragmentView = inflater.inflate(R.layout.chat_action, container, false);
        ImageButton sendButton = (ImageButton) chatActionFragmentView.findViewById(R.id.but_send);
        sendButton.setOnClickListener(this);
        etxMessage = (EditText) chatActionFragmentView.findViewById(R.id.etx_message);
        lvMassages = (ListView) chatActionFragmentView.findViewById(R.id.chat_messages);

        adapter = new SimpleAdapter(getActivity(), myArrList,
                R.layout.message_row,
                new String[]{"time", "login", "message"},
                new int[]{R.id.colTime, R.id.colLogin, R.id.colMessage});

        lvMassages.setAdapter(adapter);
        lvMassages.setOnItemLongClickListener(this);
        core.setOnAddMessageListener(this);
        core.start();
        for (int i = 0; i < core.messages.size(); i++) {    //добавляем все сообщения, если мы выходили из этого активити
            this.onAddMessage(Long.parseLong(core.date.get(i)), core.login.get(i), core.messages.get(i));
        }
        return chatActionFragmentView;
    }

    @Override
    public void onAddMessage(final long time, final String login, final String message) { //метод добавления сообщения на экран
        Core.getInstance().mainActivity.runOnUiThread(new Runnable() {
            public void run() {
                synchronized (this) {
                    Log.e("oam", message);
                    ////////////////////////////
                    if (message.contains("###FILEWORKER***")) {  //если пришел файл
                        Utils.writeFileSD(Utils.convertTime(time) + ".txt", message.replace("###FILEWORKER***", ""));
                        return;
                    }
                    ///////////////////////////////
                    Log.e("onAdd", message);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("time", Utils.convertTime(time) + "");
                    map.put("login", login);
                    map.put("message", message);
                    myArrList.add(map);
                    adapter.notifyDataSetChanged();
                    lvMassages.setSelection(adapter.getCount() - 1);

                }
            }
        });
    }

    @Override
    public void onClick(View v) { //обработчик событий кнопки
        try {

            if (!etxMessage.getText().toString().equals("")) {
                core.addMyMessage(etxMessage.getText().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        etxMessage.setText("");
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) { //при долгом тапе на экран выбранное сообщение удаляется
        Toast toast = Toast.makeText(core.mainActivity.getApplicationContext(),    //тост для удобства пользователей
                "Сообщение удалено", Toast.LENGTH_SHORT);
        toast.show();
        if (core.login.get(position).equals(core.prefslog.getString("login", "NULL"))) {
            for (int i = 0; i < core.myMessage.size(); i++) {
                if (core.myMessage.get(i).equals(core.messages.get(position)))
                    core.myMessage.remove(i);
            }
        }
        core.last_delete_mess = position;
        core.messages.remove(core.last_delete_mess);
        core.login.remove(core.last_delete_mess);
        core.date.remove(core.last_delete_mess);  //удаляем сообщение из памяти
        core.mainActivity.getFragmentManager().beginTransaction().detach(this).attach(this).commit(); //обновляем экран
        return true;
    }
}