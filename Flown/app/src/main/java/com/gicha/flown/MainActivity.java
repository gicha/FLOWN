package com.gicha.flown;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.mxn.soul.flowingdrawer_core.FlowingView;
import com.mxn.soul.flowingdrawer_core.LeftDrawerLayout;

public class MainActivity extends AppCompatActivity {


    FragmentTransaction fragEditor;
    Core core;
    LeftDrawerLayout mLeftDrawerLayout;
    FileWorkAction fw;
    ChatAction ca;


    @Override
    protected void onCreate(Bundle savedInstanceState) { //поехааалиии!!!))
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mLeftDrawerLayout = (LeftDrawerLayout) findViewById(R.id.id_drawerlayout);
        FragmentManager fm = getSupportFragmentManager();
        MyMenuFragment mMenuFragment = (MyMenuFragment) fm.findFragmentById(R.id.id_container_menu);
        fw = new FileWorkAction();
        FlowingView mFlowingView = (FlowingView) findViewById(R.id.sv);
        if (mMenuFragment == null) {
            fm.beginTransaction().add(R.id.id_container_menu, mMenuFragment = new MyMenuFragment()).commit();
        }
        mMenuFragment.setMainActivity(this);
        fw.setMainActivity(this);
        mLeftDrawerLayout.setFluidView(mFlowingView);
        mLeftDrawerLayout.setMenuFragment(mMenuFragment);
        setupToolbar();
        core = Core.getInstance();
        core.setContext(this);
        core.start(); //после всех ритуалов с активити, фрагментами и бубном мы запускаем ядро приложения
        core.pushAnecdot(); //как же без анекдотов?)
        Log.e("Login", "<" + core.prefslog.getString("login", "NULL") + ">");
        if (core.prefslog.getString("login", "NULL").equals("NULL")) {  //если пользователь открывает приложение впервый раз, то приложение откроет красивую активити для выбора логина
            startActivity(new Intent(this, StartActivity.class));
        }
        ca = new ChatAction();
        fragEditor = getFragmentManager().beginTransaction();
        fragEditor.replace(R.id.main_frame, ca);

        fragEditor.commit();

    }

    public void setMenuClick(int id) {   //работаем с боковым меню (обработчик кнопок для бокового меню)
        if (id == R.id.nav_chat) { //если чат
            ChatAction chat_action_fragment = new ChatAction();

            fragEditor = getFragmentManager().beginTransaction();
            fragEditor.replace(R.id.main_frame, chat_action_fragment);
            fragEditor.commit();
            core.setAction(chat_action_fragment.toString());
        }
        if (id == R.id.nav_rooms) { //если "Пользователи"
            RoomsAction rooms_action_fragment = new RoomsAction();

            fragEditor = getFragmentManager().beginTransaction();
            fragEditor.replace(R.id.main_frame, rooms_action_fragment);
            fragEditor.commit();
            core.setAction(rooms_action_fragment.toString());

        }
        if (id == R.id.nav_filework) { //если "передача файлов"
            FileWorkAction file_work_action_fragment = new FileWorkAction();

            fragEditor = getFragmentManager().beginTransaction();
            fragEditor.replace(R.id.main_frame, file_work_action_fragment);
            fragEditor.commit();
            core.setAction(file_work_action_fragment.toString());
        }
        if (id == R.id.nav_about) {  //если О проекте
            About ab = new About();

            fragEditor = getFragmentManager().beginTransaction();
            fragEditor.replace(R.id.main_frame, ab);
            fragEditor.commit();
            core.setAction(ab.toString());
        }

        mLeftDrawerLayout.closeDrawer();
        hideKeyboardFrom();
    }

    public void hideKeyboardFrom() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    protected void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLeftDrawerLayout.toggle();
                hideKeyboardFrom();
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (mLeftDrawerLayout.isShownMenu()) {
            mLeftDrawerLayout.closeDrawer();
            hideKeyboardFrom();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Настроечки^_^
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        return id == R.id.action_settings || super.onOptionsItemSelected(item);

    }

    public void destroyFLOWN() { //вылет приложения. Для админов
        finish();
    }

}


