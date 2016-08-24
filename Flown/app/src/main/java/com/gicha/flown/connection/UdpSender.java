package com.gicha.flown.connection;

import com.gicha.flown.Core;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


public class UdpSender extends Thread {  //отсылает в сеть данные о состоянии устройства. С помощью этой отправленной информации другие устройства узнают о количестве отправленных сообщений другими пользователями
    Core core;   //если число сообщений с предыдущей проверки не совпадает с числом последней проверки, значит пришло новое сообщение и пора бить тревогу другим классам, типа "принимайте"

    public UdpSender() {
        core = Core.getInstance();
        start();
    }

    public void run() {

        try {
            String adrr = "235.0.0.0";
            DatagramSocket mess = new DatagramSocket();
            mess.connect(InetAddress.getByName(adrr), 11111);
            long timestamp;
            int coinsmess;
            byte[] b;
            while (true) {
                byte loginB[] = core.prefslog.getString("login", "NULL").getBytes();
                timestamp = System.currentTimeMillis();
                coinsmess = core.myMessage.size();
                ByteArrayOutputStream y = new ByteArrayOutputStream();
                DataOutputStream x = new DataOutputStream(y);
                x.writeInt(loginB.length);
                x.write(loginB);
                x.writeLong(timestamp);
                x.writeInt(coinsmess);
                b = y.toByteArray();

                DatagramPacket t = new DatagramPacket(b, b.length,
                        mess.getInetAddress(), 11111);

                mess.setBroadcast(true);
                mess.send(t);
                Thread.sleep(1000);
            }

        } catch (InterruptedException e) {
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}