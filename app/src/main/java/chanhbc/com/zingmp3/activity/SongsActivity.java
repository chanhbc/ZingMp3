package chanhbc.com.zingmp3.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import chanhbc.com.zingmp3.R;
import chanhbc.com.zingmp3.adapter.SongAdapter;
import chanhbc.com.zingmp3.custom.CustomListView;
import chanhbc.com.zingmp3.manager.SongManager;
import chanhbc.com.zingmp3.model.ItemSong;
import chanhbc.com.zingmp3.model.StaticFinal;

public class SongsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private String codeId;
    private SongManager songManager;
    private CustomListView lvSong;
    private SongAdapter songAdapter;
    private Handler handler;
    private ImageView ivCoverAlbum;
    private String urlCoverAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);
        Intent intent = getIntent();
        codeId = intent.getStringExtra(StaticFinal.KEY);
        initView();
        initComponents();
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case StaticFinal.UPDATE_SONGLIST:
                        Glide.with(getApplicationContext()).load(urlCoverAlbum).into(ivCoverAlbum);
                        lvSong.setAdapter(songAdapter);
                        lvSong.setOnItemClickListener(SongsActivity.this);
                        break;
                }
                return false;
            }
        });
    }

    private void initComponents() {
        songManager = new SongManager(codeId);
        songManager.getItemSongs(new SongManager.OnGetSongOnlineListener() {
            @Override
            public void completed(ArrayList<ItemSong> itemSongs) {
                urlCoverAlbum = itemSongs.get(0).getCover();
                songAdapter = new SongAdapter(itemSongs);
                Message message = new Message();
                message.what = StaticFinal.UPDATE_SONGLIST;
                handler.sendMessage(message);
            }

            @Override
            public void error(Exception e) {

            }
        });

    }

    private void initView() {
        lvSong = (CustomListView) findViewById(R.id.lv_song);
        ivCoverAlbum = (ImageView) findViewById(R.id.iv_cover_album);
        lvSong.setExpanded(true);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
