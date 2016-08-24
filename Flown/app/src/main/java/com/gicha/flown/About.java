package com.gicha.flown;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by MSI1 on 16.05.2016.
 */
public class About extends Fragment {   //важные сведения о приложении

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View aboutFragmentView = inflater.inflate(R.layout.about_action, container, false);

        return aboutFragmentView;
    }
}
//Фрагмент "О проекте"