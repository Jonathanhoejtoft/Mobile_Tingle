package com.itu.jonathan.tingle;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;


public class TingleActivity extends FragmentActivity {

    private Fragment fragment;
    private Fragment fragmentList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tingle);
        FragmentManager fm = getSupportFragmentManager();
        fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = new TingleFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }

        View fragmentListExists = (View)findViewById(R.id.fragment_container_list);

        if (fragmentListExists != null)
        {
            fragmentList = fm.findFragmentById(R.id.fragment_container_list);
            if (fragmentList == null) {
                fragmentList = new ListFragment();
                fm.beginTransaction()
                        .add(R.id.fragment_container_list, fragmentList)
                        .commit();
            }
        }
    }


    void UpdateList()
    {
        if (fragmentList != null)
            ((ListFragment)fragmentList).UpdateList();
    }


}
