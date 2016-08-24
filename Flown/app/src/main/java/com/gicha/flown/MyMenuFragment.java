package com.gicha.flown;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mxn.soul.flowingdrawer_core.MenuFragment;


public class MyMenuFragment extends MenuFragment implements NavigationView.OnNavigationItemSelectedListener {
    FragmentTransaction fragEditor;
    MainActivity mainActivity;
    Core core;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setMainActivity(MainActivity ma) {
        mainActivity = ma;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container,
                false);
        NavigationView lv = (NavigationView) view.findViewById(R.id.vNavigation);
        lv.setNavigationItemSelectedListener(this);
        return setupReveal(view);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (mainActivity != null)
            mainActivity.setMenuClick(id);
        return true;
    }
}