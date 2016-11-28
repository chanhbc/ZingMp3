package chanhbc.com.zingmp3.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import chanhbc.com.zingmp3.R;
import chanhbc.com.zingmp3.adapter.SongAdapter;
import chanhbc.com.zingmp3.custom.CustomListView;
import chanhbc.com.zingmp3.manager.SongManager;
import chanhbc.com.zingmp3.model.ItemSong;
import chanhbc.com.zingmp3.model.StaticFinal;

public class SongFragment extends Fragment implements AdapterView.OnItemClickListener {
    private String codeId;
    private SongManager songManager;
    private CustomListView lvSong;
    private SongAdapter songAdapter;
    private Handler handler;
    private ImageView ivCoverAlbum;
    private String urlCoverAlbum;

    public SongFragment(String codeId) {
        this.codeId = codeId;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("CODEID", "" + codeId);
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case StaticFinal.UPDATE_SONGLIST:
                        Glide.with(getContext()).load(urlCoverAlbum).into(ivCoverAlbum);
                        lvSong.setAdapter(songAdapter);
                        lvSong.setOnItemClickListener(SongFragment.this);
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_songs, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lvSong = (CustomListView) view.findViewById(R.id.lv_song);
        ivCoverAlbum = (ImageView) view.findViewById(R.id.iv_cover_album);
        lvSong.setExpanded(true);
        initComponents();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
