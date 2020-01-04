package com.example.currencygroups;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabAccessAdapter extends FragmentPagerAdapter {
    public TabAccessAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                CurrencyGroupsFragment currencyGroupsFragment = new CurrencyGroupsFragment();
                return currencyGroupsFragment;
            case 1:
                CurrencyListsFragment currencyListsFragment = new CurrencyListsFragment();
                return currencyListsFragment;
            case 2:
                SyncFragment syncFragment = new SyncFragment();
                return syncFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    //İSTERSEK YAZIDA EKLEYEBİLİRİZ.

    /*
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Gruplar";
            case 1:
                return "Menüler";
            default:
                return null;
        }
    }
    */

}
