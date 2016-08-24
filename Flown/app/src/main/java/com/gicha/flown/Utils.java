package com.gicha.flown;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class Utils {    //разные полезные методы
    private String EncKey = "770A8A65DA156D24EE2A093277530142";
    final static String LOG_TAG = "myLogs";

    public String getEncKey() {
        return EncKey;
    }

    public static String convertTime(long time) {   //конвертируем время unix в адекватное для простых смертных
        Date date = new Date(time);
        Format format = new SimpleDateFormat("HH:mm:ss");
        return format.format(date);
    }

    public static boolean checkPermission(String perm) {
        return (Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(Core.getInstance().mainContext, perm) == PackageManager.PERMISSION_GRANTED);
    }

    public static void requestPermission(String perm) {
        ActivityCompat.requestPermissions(Core.getInstance().mainActivity, new String[]{perm}, 0);
    }

    public static String readFileSD(String dir, String file) {    //читаем файл из памяти устройства
        Log.e("FileReader", "Start " + dir + " " + file);
        Utils.requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        String str = "";
        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d(LOG_TAG, "SD-карта не доступна(как и наш мозг): " + Environment.getExternalStorageState());
            return "Ошибочка(9((99( ОБИДНО КРЧ";
        }
        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + dir);
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, file);
        try {
            // открываем поток для чтения
            BufferedReader br = new BufferedReader(new FileReader(sdFile));

            // читаем содержимое
            String tmp;
            while ((tmp = br.readLine()) != null) {
                Log.d(LOG_TAG, str);
                str += tmp;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static void writeFileSD(String name, String str) { //запись плученного файла на устройство
        // проверяем доступность SD
        Log.e("writeFile", str);
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
            return;
        }
        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + "Download");
        // создаем каталог
        sdPath.mkdirs();
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, name);
        try {
            // открываем поток для записи
            BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
            // пишем данные
            bw.write(str);
            // закрываем поток
            bw.close();
            Log.d(LOG_TAG, "Файл записан на SD: " + sdFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loveInFlownBathroom(String s) { //пасхалка для пользователей. Пользователь пишет два имени, потом приложение выдает совместимость по шкале от одного до десяти
        s.replace("#совместимость ", "");
        s.replace(" ", "");
        s = s.toLowerCase();
        ArrayList<String> arr = new ArrayList<String>();
        ArrayList<Integer> num = new ArrayList<Integer>();
        for (int i = 0; i < s.length(); i++) {
            boolean f = false;
            String per = s.substring(i, i + 1);
            if (!per.equals(" ")) {
                for (int j = 0; j < arr.size(); j++) {
                    if (per.equals(arr.get(j))) {
                        f = true;
                        int y = num.remove(j) + 1;
                        num.add(j, y);
                    }
                }
                if (!f) {
                    num.add(1);
                    arr.add(per);
                }
            }
        }
        for (int i = 0; i < num.size(); i++) {
            num.set(i, num.get(i) % 2);
        }

        for (int i = 0; i < num.size(); i = i + 2) {
            if (i != num.size() - 1) {
                int y = num.remove(i + 1) + num.remove(i);
                num.add(i, y);
                i--;
            } else {
                int y = num.remove(i);
                num.add(i, y);
                i--;
            }
        }

        while (num.size() > 1) {
            for (int i = 0; i < num.size(); i++) {
                if (i != num.size() - 1) {
                    int y = num.remove(i + 1) + num.remove(i);
                    if (y > 9) {
                        y = y / 10 + y % 10;
                    }
                    num.add(i, y);
                }
            }
        }

        try {
            Core.getInstance().addMyMessage("На, держи!\n" + num.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void random(String in) { //выдает рандомное число в определенном диапазоне
        Log.e("Random", "Я тута " + in);
        String[] str1 = in.split(" ");
        if (str1.length == 2) {
            try {
                long x = Long.parseLong(str1[0]) + (int) (Math.random() * ((Long.parseLong(str1[1]) - Long.parseLong(str1[0])) + 1)); //делаем рандомное число
                Core.getInstance().addMyMessage("На, держи!\n" + String.valueOf(x));
            } catch (Exception e) {
                e.printStackTrace();
                Toast toast = Toast.makeText(Core.getInstance().mainActivity.getApplicationContext(),
                        "А не большие ли у вас запросы, сударь?!?!?!?!?!?!?)))))0)))))", Toast.LENGTH_SHORT); //если пользователь ввел что-то неправильно
                toast.show();
            }
        }
    }

    public static void sendAnecdot() { //отправляет рандомный анекдот из массива анекдотов
        Log.e("Anecdotes", "go");
        try {
            Core.getInstance().addMyMessage("На, держи!\n" + Core.getInstance().anecdot_ar.get((int) (Math.random() * (Core.getInstance().anecdot_ar.size() + 1))));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}

