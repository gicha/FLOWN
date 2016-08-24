package com.gicha.flown.connection;

import android.util.Log;

import com.gicha.flown.Core;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class MessageReceive extends Thread {

    String ip;
    Socket s;
    int lastId, newId;
    Core core;  //объект класса core
    String login; //логин

    public MessageReceive(String _ip, int _lastId, int _newId, String _login) { //конструктор класса
        ip = _ip;
        lastId = _lastId;
        newId = _newId;
        login = _login;
        try {
            s = new Socket(ip, 11112);
        } catch (IOException e) {
            e.printStackTrace();
        }
        core = Core.getInstance(); //получаем экземпляр
        start();
    }

    public void run() {
        ByteArrayOutputStream y = new ByteArrayOutputStream();
        DataOutputStream x = new DataOutputStream(y);

        OutputStream w = null;
        DataInputStream s1 = null;
        try {
            w = s.getOutputStream();
            s1 = new DataInputStream(s.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("MessRec", "FAIL 1");
            return;
        }
        for (int num = lastId + 1; num <= newId; num++) {
            try {
                x.writeInt(num);
                w.write(y.toByteArray());
                w.flush();
                y.reset();

                int count;
                long time;
                count = s1.readInt();
                time = s1.readLong();
                byte[] mess = new byte[count];
                s1.read(mess);
                String message = new String(mess);
                core.addNetMessage(login, time, message);  //получаем новое сообщение и добавляем на экран


            } catch (Exception e) {

                Log.e("MessRec", "FAIL 2");
                Log.e("MessRec", e.toString());
            }
        }
        try {
            x.writeInt(0);
            w.write(y.toByteArray());
            w.flush();
            y.reset();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }
}