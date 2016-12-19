package chanhbc.com.zingmp3.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class SongAdapter extends FragmentStatePagerAdapter {
    private Fragment fragment;

    public SongAdapter(FragmentManager fragmentManager, Fragment fragment){
        super(fragmentManager);
        this.fragment = fragment;
    }

    @Override
    public Fragment getItem(int position) {
        return fragment;
    }

    @Override
    public int getCount() {
        return 1;
    }

}
