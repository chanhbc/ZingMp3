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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import chanhbc.com.zingmp3.R;
import chanhbc.com.zingmp3.adapter.SongAdapter;
import chanhbc.com.zingmp3.custom.CustomListView;
import chanhbc.com.zingmp3.manager.SongManager;
import chanhbc.com.zingmp3.model.ItemSong;
import chanhbc.com.zingmp3.model.StaticFinal;
import de.hdodenhof.circleimageview.CircleImageView;

public class SongFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    private String codeId;
    private SongManager songManager;
    private CustomListView lvSong;
    private SongAdapter songAdapter;
    private Handler handler;
    private ImageView ivCoverAlbum;
    private CircleImageView civCoverSong;
    private TextView tvTitle;
    private TextView tvArtist;
    private LinearLayout llPlay;
    private String urlCoverAlbum;
    private ArrayList<ItemSong> itemSongs;

    public SongFragment(String codeId, String urlCoverAlbum) {
        this.codeId = codeId;
        this.urlCoverAlbum = urlCoverAlbum;
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
                SongFragment.this.itemSongs = itemSongs;
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
        llPlay = (LinearLayout) view.findViewById(R.id.ll_play_song);
        llPlay.setVisibility(View.GONE);
        llPlay.setOnClickListener(this);
        civCoverSong = (CircleImageView) view.findViewById(R.id.civ_cover_song);
        tvArtist = (TextView) view.findViewById(R.id.tv_artist);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvArtist.setSelected(true);
        tvTitle.setSelected(true);
        lvSong.setExpanded(true);
        initComponents();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.lv_song:
                ItemSong itemSong = itemSongs.get(position);
                llPlay.setVisibility(View.VISIBLE);
                tvTitle.setText(itemSong.getTitle());
                tvArtist.setText(itemSong.getArtist());
                Glide.with(getContext()).load(itemSong.getCover()).into(civCoverSong);
                break;

            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_play_song:

                break;

            default:
                break;
        }
    }
}
