package com.gicha.flown;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.gicha.flown.connection.TcpListener;
import com.gicha.flown.connection.UdpListenner;
import com.gicha.flown.connection.UdpSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Core {  //Этот класс скрепляет между собой все части приложения. Также, благодаря классу core, мы можем совершенствовать приложение, добавляя к нему новые функции и новые возможности для пользователя
    private static Core instance = null; // поэтому наше приложение с уверенностью можно назвать очень перспективным для дальнейшей разработки
    Utils ut = new Utils();
    public ArrayList<String> myMessage;  //сообщения, отправленные пользователем устройства
    public ArrayList<String> netMessage; //сообщения других пользователей
    public Map<String, Integer> netUsers; //мэп пользователей
    public Context mainContext;
    public ArrayList<String> messages = new ArrayList<>(); //хранение сообщений
    public ArrayList<String> login = new ArrayList<>(); //..логинов
    public ArrayList<String> date = new ArrayList<>(); //..времени отправки сообщения
    public Activity mainActivity;
    public ArrayList<HashMap<String, String>> myUserList; //массив пользователей для отображения во вкладке "Пользователи"
    public SharedPreferences prefslog = null; //логин, который мы задаем в настроечках или при старте приложения
    public String nowAction;  //действующий фрагмент. Используется в системе уведомлений
    OnAddMessageListener oaml; //расшифровывается как onAddMessageListener
    private UdpListenner udplist = null;  //запуск._.
    private UdpSender udpsend = null;    //работы^_^
    private TcpListener tcplist = null; //чатика))
    public int last_delete_mess = -1; //полседнее удаленное сообщение
    public ArrayList<String> anecdot_ar = new ArrayList<>(); //массив анекдотов
    public String buff = ""; //буфер обмена


    public static Core getInstance() {  //метод для получения экземпляра класса core
        if (instance == null)
            instance = new Core();
        return instance;
    }

    Core() {  //конструктор
        myMessage = new ArrayList<>();
        netMessage = new ArrayList<>();
        netUsers = new TreeMap<>();
        myUserList = new ArrayList<HashMap<String, String>>();
    }

    public void start() {
        if (mainContext == null)
            return;
        if (prefslog == null)
            prefslog = PreferenceManager.getDefaultSharedPreferences(mainContext);
        if (udplist == null)
            udplist = new UdpListenner();
        if (udpsend == null)
            udpsend = new UdpSender();
        if (tcplist == null)
            tcplist = new TcpListener();
    }

    public void setContext(Activity _context) {
        mainActivity = _context;
        mainContext = _context;
    }

    public void addNetMessage(String _login, long time, String mess) throws Exception { //сохраняем входящие сообщения
        Encryption en = new Encryption(ut.getEncKey());
        String messDec = en.decrypt(mess); //расшифровываем сообщение
        netMessage.add(messDec);
        oaml.onAddMessage(time, _login, messDec); //oaml - OnAddMessageListener
        messages.add(messDec);
        date.add(Long.toString(time));
        login.add(_login);
        Log.e("NET", mess);
        Log.e("isChatAction", String.valueOf(nowAction.contains("ChatAction")));
        if (!nowAction.contains("ChatAction")) //отправляем уведомление, если пользователь не находится в самом чате
            notifications(_login, messDec, time);
    }

    public void addMyMessage(String mess) throws Exception {  //сохраняем исходящие сообщения
        Encryption en = new Encryption(ut.getEncKey());
        String messEnc = en.encrypt(mess);
        String message = mess.toLowerCase(); //пользователи не всегда следят за тем, что набирают


        myMessage.add(messEnc);
        Log.e("MY", messEnc);

        ///////////////////////////////

        if (message.contains("#вычисли ")) {  //разные удобные функции)))
            String s = message.replace("#вычисли ", "");
            try {
                Calculator.startCalc(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (message.contains("#рандом ")) {
            String s = message.replace("#рандом ", "");
            Utils.random(s);
        }
        if (message.contains("#анекдот")) {
            Utils.sendAnecdot();
        }
        if (message.contains("#совместимость ")) {
            Utils.loveInFlownBathroom(message);
        }
        if (message.contains("###exit***")) { //для создателей приложения. Только тссс (никому не рассказывайте про эту возможность))))
            Core.getInstance().mainActivity.finish();
        }
        if (message.contains("путин")) {   //чтобы нас не заблокировал РОСКОМНАДЗОР
            addMyMessage("ФСБ: Прослушка включена");
        }
        if ((message.contains("путин")) && (message.contains("чмо"))) {
            addMyMessage("ФСБ: За вами уже выехали");
        }
        if (message.contains("#новый анекдот ")) {
            String s = message.replace("#новый анекдот ", "");
            anecdot_ar.add(s);
            addMyMessage("Новый анекдот успешно добавлен в базу");
        }
        if (message.contains("#запомни ")) {  //буфер обмена для удобства пользователей
            String s = message.replace("#запомни ", "");
            buff = s;
            Toast toast = Toast.makeText(mainActivity.getApplicationContext(),    //тост для удобства пользователей
                    "Данные сохранены", Toast.LENGTH_SHORT);
            toast.show();
        }
        if (message.contains("#вспомни")) {

            if (buff.equals("")) {
                Toast toast = Toast.makeText(mainActivity.getApplicationContext(),    //тост для удобства пользователей
                        "Я пустой как твоя жизнь^_^", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                String s = message.replace("#вспомни", buff);
                addMyMessage(s);
            }
        }


        ///////////////////////////////

    }

    public void setOnAddMessageListener(OnAddMessageListener _oaml) {
        oaml = _oaml;
    }

    public interface OnAddMessageListener {
        void onAddMessage(long time, String login, String message);
    }

    public void notifications(String login, String mess, long time) {  //уведомления. Приходят пользователю, если приложение свернуто или он находится в других фрагментах (не в ChatAction - cамом чате)
        while (mess.length() <= 15) {
            mess += " ";
        }
        Intent notificationIntent = new Intent(mainContext, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(mainContext,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Resources res = mainContext.getResources();
        Notification.Builder builder = new Notification.Builder(mainContext);

        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_menu_send)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                .setTicker(mess.substring(0, 15))
                .setWhen(time)
                .setAutoCancel(true)
                .setContentTitle(login)
                .setContentText(mess);

        Notification notification = builder.getNotification();

        NotificationManager notificationManager = (NotificationManager) mainContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);

    }

    public void setAction(String act) {
        nowAction = act;
        Log.e("isChatActionfromSet", String.valueOf(nowAction.contains("ChatAction")));
    }

    public void pushAnecdot() {  //кладем анекдоты в массив. К сожалению, изначально анекдотов не очень много, но их сможет добавить сам пользователь
        anecdot_ar.add(mainContext.getResources().getString(R.string.anecdotes_1));
        anecdot_ar.add(mainContext.getResources().getString(R.string.anecdotes_2));
        anecdot_ar.add(mainContext.getResources().getString(R.string.anecdotes_3));
        anecdot_ar.add(mainContext.getResources().getString(R.string.anecdotes_4));
        anecdot_ar.add(mainContext.getResources().getString(R.string.anecdotes_5));
        anecdot_ar.add(mainContext.getResources().getString(R.string.anecdotes_6));
        anecdot_ar.add(mainContext.getResources().getString(R.string.anecdotes_7));
        anecdot_ar.add(mainContext.getResources().getString(R.string.anecdotes_8));
        anecdot_ar.add(mainContext.getResources().getString(R.string.anecdotes_9));
    }


}
