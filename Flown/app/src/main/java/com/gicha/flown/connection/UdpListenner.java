package com.gicha.flown.connection;

import android.util.Log;

import com.gicha.flown.Core;
import com.gicha.flown.Utils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;

/**
 * Created by MSI1 on 30.03.2016.
 */
public class UdpListenner extends Thread {
    Core core;

    public UdpListenner() {
        core = Core.getInstance();
        start();
    }

    public void run() {
        Log.e("UDP", "Started");
        try {

            String adrr = "235.0.0.0";
            MulticastSocket mess = new MulticastSocket(11111);   //в нашем децентрализованном чатике для отправки сообщений мы будем использовать сокеты
            mess.joinGroup(InetAddress.getByName(adrr)); //итак. Идея чата такая:
            byte[] buft = new byte[1000]; //все пользователи в сети через какое-то определенное время выбрасывают в сеть броадкаст с количеством своих сообщений.
            while (true) {              //возьмем каких-то конкретных юзеров. Например, Машу и еще каких-то неизвестных чуваков в сети. Как только они зашли в приложение, они начинают отправлять в сеть кол-во сообщений, которые они отправили.
                DatagramPacket time = new DatagramPacket(buft, buft.length); //Маше нравился Петя. Поэтому она решила ему в этом признаться в нашем чатике(она была странной девочкой). У нее(!) в память это сообщение записывается.
                mess.receive(time);     //Программа с ее устройства теперь отправляет, что количство отправленных сообщений теперь не 0, а 1. Все остальные устройства в сети немного офигевают от происходящего
                ByteArrayInputStream y = new ByteArrayInputStream(time.getData()); //и начинают судорожно отправлять запросы "Отправь мне сообщение под номером 1".
                DataInputStream x = new DataInputStream(y); //Маша всем отправляет это сообщение. Его читает и Петя. Теперь все счастливы (в особенности Маша и Петя) и ждут дальнейших действий. Так наше приложение помогло еще двум людям.
                String ip = time.getAddress().getHostAddress(); //Это вкратце^_^
                int loginBCount = x.readInt();
                byte temp[] = new byte[loginBCount];
                x.readFully(temp, 0, loginBCount);
                String login = new String(temp);
                long t1 = x.readLong();
                int c1 = x.readInt();
                Log.w("UDP", ip + " - " + login + " - " + c1);
                //////////////////////////////////
                if (c1 == 0) {
                    continue;
                }
                ////////////////////////////////////
                if (!core.netUsers.containsKey(ip)) {
                    new MessageReceive(ip, 0, c1, login);
                    core.netUsers.put(ip, c1);
                    HashMap<String, String> map;
                    map = new HashMap<>();
                    map.put("time", Utils.convertTime(System.currentTimeMillis()) + "");
                    map.put("login", login);
                    core.myUserList.add(map);
                } else if (c1 != core.netUsers.get(ip)) {
                    new MessageReceive(ip, core.netUsers.get(ip), c1, login);
                    core.netUsers.put(ip, c1);
                }
            }

        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
