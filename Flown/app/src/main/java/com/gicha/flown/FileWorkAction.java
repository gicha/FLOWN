package com.gicha.flown;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;

public class FileWorkAction extends Fragment implements View.OnClickListener {  //Фрагмент работы с файлами
    public void setMainActivity(MainActivity ma) {
        mainActivity = ma;
    }

    MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fileWorkActionFragmentView = inflater.inflate(R.layout.file_action, container, false);

        Button bSF = (Button) fileWorkActionFragmentView.findViewById(R.id.bSF);

        bSF.setOnClickListener(this);

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, 1);

        return fileWorkActionFragmentView;
    }

    @Override
    public void onClick(View v) { //обрабатываем нажатие кнопки
        switch (v.getId()) {
            case R.id.bSF:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, 1);
                break;


        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {  //обрабатываем полученный путь после выбора файла
        switch (requestCode) {                                                    //берем файл и отправляем сам файл
            case 1:
                if (resultCode == Core.getInstance().mainActivity.RESULT_OK) {
                    Uri uri = data.getData();
                    Log.d("PATH", "File Path: " + uri.getPath());
                    File file = new File(uri.getPath());
                    byte[] data1 = new byte[(int) file.length()];
                    try {
                        new FileInputStream(file).read(data1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String mess = null;
                    try {
                        mess = new String(data1, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Log.d("Str", mess);
                    try {
                        Core.getInstance().addMyMessage("###FILEWORKER***" + mess);
                        Core.getInstance().addMyMessage("Отправлен новый файл\nИщите в папке /Download");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
