package com.gicha.flown.connection;

import com.gicha.flown.Core;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;


public class TcpSender extends Thread { //отправка самого сообщения
    Socket s;
    Core core;

    public TcpSender(Socket i1) {
        s = i1;
        core = Core.getInstance();
        start();
    }

    public void run() {
        try {
            DataInputStream s1 = new DataInputStream(s.getInputStream());
            ByteArrayOutputStream y = new ByteArrayOutputStream();
            DataOutputStream x = new DataOutputStream(y);
            while (true) {
                int num = s1.readInt();
                if (num == 0)
                    break;
                byte mess[] = core.myMessage.get(num - 1).getBytes("UTF-8");
                x.writeInt(mess.length);
                x.writeLong(System.currentTimeMillis());
                x.write(mess);

                OutputStream w = s.getOutputStream();
                w.write(y.toByteArray());
                w.flush();
                y.reset();
            }
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
