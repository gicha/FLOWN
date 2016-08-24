package com.gicha.flown;

import java.io.File;

public class Message {   //есть в планах развития передавать (вместо самого сообщения) класс со всеми данными о сообщении
    public String mess;
    public File file;
    public String login;
    public long time;
    public String ip;
    public boolean isFile;

    public Message() {
        mess = "";
        isFile = false;
        time = 0;
        ip = "";
        login = "NEWBIES";
    }

}
