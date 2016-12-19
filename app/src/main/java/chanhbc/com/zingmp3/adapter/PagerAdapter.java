package chanhbc.com.zingmp3.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.ArrayList;

import chanhbc.com.zingmp3.fragment.ListAlbumFragment;
import chanhbc.com.zingmp3.model.ItemAlbum;

public class PagerAdapter extends FragmentStatePagerAdapter implements ListAlbumFragment.OnLoadItemSongListener {
    private OnLoadItemSongListener onLoadItemSongListener;

    public PagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        ListAlbumFragment listAlbumFragment = new ListAlbumFragment(position);
        listAlbumFragment.setOnLoadItemSongListener(PagerAdapter.this);
        return listAlbumFragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        switch (position) {
            case 0:
                title = "Nổi bật";
                break;

            case 1:
                title = "Mới nhất";
                break;

            case 2:
                title = "Nghe nhiều";
                break;

            default:
                break;
        }
        return title;
    }

    @Override
    public void onSongLoad(ItemAlbum itemAlbum) {
        onLoadItemSongListener.onSongLoad(itemAlbum);
    }

    public void setOnLoadItemSongListener(OnLoadItemSongListener listener) {
        this.onLoadItemSongListener = listener;
    }

    public interface OnLoadItemSongListener {
        void onSongLoad(ItemAlbum itemAlbum);
    }
}
