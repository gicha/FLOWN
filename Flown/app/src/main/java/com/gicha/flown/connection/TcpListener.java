package com.gicha.flown.connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpListener extends Thread {    //ждет новых подключений через протокол TCP. Подключение происходит перед получением нового сообщения
    public TcpListener() {
        start();
    } //запускается в отдельном потоке (как и еще некоторые классы из нашего приложения)

    public void run() {
        try {
            ServerSocket ss = new ServerSocket(11112);
            while (true) {
                Socket s = ss.accept();
                new TcpSender(s);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
