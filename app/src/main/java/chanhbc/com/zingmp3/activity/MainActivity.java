package chanhbc.com.zingmp3.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

import chanhbc.com.zingmp3.R;
import chanhbc.com.zingmp3.adapter.MenuAdapter;
import chanhbc.com.zingmp3.adapter.PagerAdapter;
import chanhbc.com.zingmp3.fragment.AlbumFragmentManager;
import chanhbc.com.zingmp3.model.ItemAlbum;
import chanhbc.com.zingmp3.model.ItemListMusic;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener, PagerAdapter.OnLoadItemSongListener {
    private ArrayList<ItemListMusic> listMusics;
    private ListView lvMusic;
    private MenuAdapter menuAdapter;
    private DrawerLayout dlMenu;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private PagerAdapter pagerAdapter;
    private ImageView ivNavigation;
    private FragmentManager fragmentManager;
    private AlbumFragmentManager albumFragmentManager;
    private LinearLayout llPlaySong;
    private LinearLayout llToolBar;
    private boolean isFirst = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initializeComponents();
        llToolBar.setVisibility(View.GONE);
        tabLayout.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.VISIBLE);
        pagerAdapter = new PagerAdapter(fragmentManager);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setCurrentItem(1);
        pagerAdapter.setOnLoadItemSongListener(MainActivity.this);
        tabLayout.setTabsFromPagerAdapter(pagerAdapter);
    }


    private void initViews() {
        lvMusic = (ListView) findViewById(R.id.lv_music);
        dlMenu = (DrawerLayout) findViewById(R.id.dl_menu);
        viewPager = (ViewPager) findViewById(R.id.vp);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        ivNavigation = (ImageView) findViewById(R.id.iv_navigation);
        viewPager.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
        llToolBar = (LinearLayout) findViewById(R.id.ll_toolbar);
        llPlaySong = (LinearLayout) findViewById(R.id.ll_play_song_full);
    }

    private void initializeComponents() {
        listMusics = new ArrayList<>();
        listMusics.add(new ItemListMusic(R.drawable.ic_music_offline, "Music offline"));
        listMusics.add(new ItemListMusic(R.drawable.ic_music_offline, "Ablum Dance"));
        menuAdapter = new MenuAdapter(listMusics);
        lvMusic.setAdapter(menuAdapter);
        lvMusic.setOnItemClickListener(this);
        ivNavigation.setOnClickListener(this);
        if (albumFragmentManager == null) {
            albumFragmentManager = new AlbumFragmentManager();
        }
        replaceFragment(albumFragmentManager);
        fragmentManager = getSupportFragmentManager();
    }

    private void replaceFragment(Fragment fragment) {
        Fragment f = getSupportFragmentManager().findFragmentByTag(fragment.getClass().getName());
        if (fragment != null) {
            if (fragment.isVisible()) {
                return;
            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .show(fragment)
                    .commit();
            return;
        }
        if (isFirst == false) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.layout_content, fragment, fragment.getClass().getName())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.layout_content, fragment, fragment.getClass().getName())
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        }
        isFirst = true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.lv_music:
                dlMenu.closeDrawers();
                llToolBar.setVisibility(View.GONE);
                if (i == 1) {
                    tabLayout.setVisibility(View.VISIBLE);
                    viewPager.setVisibility(View.VISIBLE);
                    pagerAdapter = new PagerAdapter(fragmentManager);
                    viewPager.setAdapter(pagerAdapter);
                    if (tabLayout == null)
                        tabLayout.setupWithViewPager(viewPager);
                    viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                    viewPager.setCurrentItem(1);
                    pagerAdapter.setOnLoadItemSongListener(MainActivity.this);
                    if (tabLayout == null)
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
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {

            super.onBackPressed();
        }
    }

    @Override
    public void onSongLoad(ItemAlbum itemAlbum) {
        Intent intent = new Intent(this, PlayActivity.class);
        intent.putExtra("SongId", itemAlbum.getId());
        intent.putExtra("SongCover", itemAlbum.getCover());
        startActivity(intent);
    }
}
