package com.caloriesdiary.caloriesdiary;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.caloriesdiary.caloriesdiary.tab_fragments.GraphFragment;


public class StatPagerFragmentAdapter extends FragmentPagerAdapter {

    String title [];
    public StatPagerFragmentAdapter (FragmentManager fm){
        super(fm);
        title = new String[]{"Графики", "Таблица"};
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0:
                return GraphFragment.getInstance();

            case 1:
                return GraphFragment.getInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return title.length;
    }
}
