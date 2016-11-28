package chanhbc.com.zingmp3.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

import chanhbc.com.zingmp3.R;
import chanhbc.com.zingmp3.adapter.MenuAdapter;
import chanhbc.com.zingmp3.adapter.PagerAdapter;
import chanhbc.com.zingmp3.fragment.AlbumHotFragment;
import chanhbc.com.zingmp3.fragment.AlbumReleaseDateFragment;
import chanhbc.com.zingmp3.fragment.AlbumTotalPlayFragment;
import chanhbc.com.zingmp3.fragment.SongFragment;
import chanhbc.com.zingmp3.model.ItemAlbum;
import chanhbc.com.zingmp3.model.ItemListMusic;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener, AlbumHotFragment.OnLoadItemSongListener, AlbumReleaseDateFragment.OnLoadItemSongListener, AlbumTotalPlayFragment.OnLoadItemSongListener {
    private ArrayList<ItemListMusic> listMusics;
    private ListView lvMusic;
    private MenuAdapter menuAdapter;
    private DrawerLayout dlMenu;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private PagerAdapter pagerAdapter;
    private ArrayList<Fragment> fragments;
    private ArrayList<Fragment> songFragments;
    private ImageView ivNavigation;
    private AlbumHotFragment albumHotFragment;
    private AlbumReleaseDateFragment albumReleaseDateFragment;
    private AlbumTotalPlayFragment albumTotalPlayFragment;
    private SongFragment songFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initializeComponents();
    }

    private void initViews() {
        lvMusic = (ListView) findViewById(R.id.lv_music);
        dlMenu = (DrawerLayout) findViewById(R.id.dl_menu);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        ivNavigation = (ImageView) findViewById(R.id.iv_navigation);
        viewPager.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
    }

    private void initializeComponents() {
        listMusics = new ArrayList<>();
        listMusics.add(new ItemListMusic(R.drawable.ic_music_offline, "Music offline"));
        listMusics.add(new ItemListMusic(R.drawable.ic_music_offline, "Ablum Dance"));
        menuAdapter = new MenuAdapter(listMusics);
        lvMusic.setAdapter(menuAdapter);
        lvMusic.setOnItemClickListener(this);
        ivNavigation.setOnClickListener(this);
        albumHotFragment = new AlbumHotFragment();
        albumReleaseDateFragment = new AlbumReleaseDateFragment();
        albumTotalPlayFragment = new AlbumTotalPlayFragment();
        fragments = new ArrayList<>();
        fragments.add(albumHotFragment);
        fragments.add(albumReleaseDateFragment);
        fragments.add(albumTotalPlayFragment);
        fragmentManager = getSupportFragmentManager();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.lv_music:
                dlMenu.closeDrawers();
                if (i == 1) {
                    tabLayout.setVisibility(View.VISIBLE);
                    viewPager.setVisibility(View.VISIBLE);
                    pagerAdapter = new PagerAdapter(fragmentManager, fragments);
                    albumHotFragment.setOnLoadItemSongListener(MainActivity.this);
                    albumReleaseDateFragment.setOnLoadItemSongListener(MainActivity.this);
                    albumTotalPlayFragment.setOnLoadItemSongListener(MainActivity.this);
                    viewPager.setAdapter(pagerAdapter);
                    tabLayout.setupWithViewPager(viewPager);
                    viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                    viewPager.setCurrentItem(1);
                    tabLayout.setTabsFromPagerAdapter(pagerAdapter);
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_navigation:
                dlMenu.openDrawer(GravityCompat.START);
                break;

            default:
                break;
        }
    }

    @Override
    public void onHotSongLoad(ItemAlbum itemAlbum) {
        tabLayout.setVisibility(View.GONE);
        songFragment = new SongFragment(itemAlbum.getId(),itemAlbum.getCover());
        songFragments = new ArrayList<>();
        songFragments.add(songFragment);
        pagerAdapter = new PagerAdapter(fragmentManager, songFragments);
        viewPager.setAdapter(pagerAdapter);
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 ){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onReleaseDateSongLoad(ItemAlbum itemAlbum) {
        tabLayout.setVisibility(View.GONE);
        songFragment = new SongFragment(itemAlbum.getId(),itemAlbum.getCover());
        songFragments = new ArrayList<>();
        songFragments.add(songFragment);
        pagerAdapter = new PagerAdapter(fragmentManager, songFragments);
        viewPager.setAdapter(pagerAdapter);
    }

    @Override
    public void onTotalPlayDateSongLoad(ItemAlbum itemAlbum) {
        tabLayout.setVisibility(View.GONE);
        songFragment = new SongFragment(itemAlbum.getId(),itemAlbum.getCover());
        songFragments = new ArrayList<>();
        songFragments.add(songFragment);
        pagerAdapter = new PagerAdapter(fragmentManager, songFragments);
        viewPager.setAdapter(pagerAdapter);
    }
}
