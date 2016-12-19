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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import chanhbc.com.zingmp3.R;
import chanhbc.com.zingmp3.adapter.ItemSongAdapter;
import chanhbc.com.zingmp3.custom.CustomListView;
import chanhbc.com.zingmp3.manager.SongManager;
import chanhbc.com.zingmp3.model.ItemSong;
import chanhbc.com.zingmp3.model.StaticFinal;
import de.hdodenhof.circleimageview.CircleImageView;

public class SongFragmentManager extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    private String codeId;
    private SongManager songManager;
    private CustomListView lvSong;
    private ItemSongAdapter itemSongAdapter;
    private Handler handler;
    private ImageView ivCoverAlbum;
    private CircleImageView civCoverSong1;
    private CircleImageView civCoverSong;
    private TextView tvTitle1;
    private TextView tvArtist1;
    private TextView tvTitle;
    private TextView tvArtist;
    private LinearLayout llPlayLite;
    private LinearLayout llPlayFull;
    private LinearLayout llListSong;
    private String urlCoverAlbum;
    private ItemSong itemSong;
    private ArrayList<ItemSong> itemSongs;
    private Animation anim_ll_play_song;
    private Animation anim_ll_play_song_down;
    private Animation anim_cover_song;
    private ImageView ivClose;

    public SongFragmentManager(String codeId, String urlCoverAlbum) {
        this.codeId = codeId;
        this.urlCoverAlbum = urlCoverAlbum;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case StaticFinal.UPDATE_SONGLIST:
                        Glide.with(getContext()).load(urlCoverAlbum).into(ivCoverAlbum);
                        lvSong.setAdapter(itemSongAdapter);
                        lvSong.setOnItemClickListener(SongFragmentManager.this);
                        break;
                }
                return false;
            }
        });
    }

    private void initComponents() {
        anim_ll_play_song = AnimationUtils.loadAnimation(getContext(), R.anim.anim_translate_ll_play_song_full);
        anim_ll_play_song_down = AnimationUtils.loadAnimation(getContext(), R.anim.anim_translate_ll_play_song_full_down);
        anim_cover_song = AnimationUtils.loadAnimation(getContext(), R.anim.anim_rotate_image_cover_song);
        songManager = new SongManager(codeId);
        songManager.getItemSongs(new SongManager.OnGetSongOnlineListener() {
            @Override
            public void completed(ArrayList<ItemSong> itemSongs) {
                SongFragmentManager.this.itemSongs = itemSongs;
                itemSongAdapter = new ItemSongAdapter(itemSongs);
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
        llPlayLite = (LinearLayout) view.findViewById(R.id.ll_play_song_lite);
        llPlayLite.setVisibility(View.GONE);
        llPlayLite.setOnClickListener(this);
        llPlayFull = (LinearLayout) view.findViewById(R.id.ll_play_song_full);
        llPlayFull.setVisibility(View.GONE);
        llListSong = (LinearLayout) view.findViewById(R.id.ll_list_song);
        civCoverSong1 = (CircleImageView) view.findViewById(R.id.civ_cover_song_1);
        civCoverSong = (CircleImageView) view.findViewById(R.id.civ_cover_song);
        tvArtist1 = (TextView) view.findViewById(R.id.tv_artist_1);
        tvTitle1 = (TextView) view.findViewById(R.id.tv_title_1);
        tvArtist = (TextView) view.findViewById(R.id.tv_artist);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        ivClose = (ImageView) view.findViewById(R.id.iv_close_activity_play);
        ivClose.setOnClickListener(this);
        tvArtist1.setSelected(true);
        tvTitle1.setSelected(true);
        tvArtist.setSelected(true);
        tvTitle.setSelected(true);
        lvSong.setExpanded(true);
        initComponents();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.lv_song:
                llListSong.setVisibility(View.GONE);
                llPlayFull.setVisibility(View.VISIBLE);
                itemSong = itemSongs.get(position);
                tvTitle.setText(itemSong.getTitle());
                tvArtist.setText(itemSong.getArtist());
                Glide.with(getContext()).load(itemSong.getCover()).into(civCoverSong);
                llPlayFull.startAnimation(anim_ll_play_song);
                civCoverSong.startAnimation(anim_cover_song);
                break;

            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_play_song_lite:
                llListSong.setVisibility(View.GONE);
                llPlayLite.setVisibility(View.GONE);
                llPlayFull.setVisibility(View.VISIBLE);
                llPlayFull.startAnimation(anim_ll_play_song);
                tvTitle.setText(itemSong.getTitle());
                tvArtist.setText(itemSong.getArtist());
                Glide.with(getContext()).load(itemSong.getCover()).into(civCoverSong);
                break;

            case R.id.iv_close_activity_play:
                Log.d("CLOSE","");
                llPlayFull.startAnimation(anim_ll_play_song_down);
                llPlayLite.setVisibility(View.VISIBLE);
                llPlayLite.setOnClickListener(SongFragmentManager.this);
                tvTitle1.setText(itemSong.getTitle());
                tvArtist1.setText(itemSong.getArtist());
                Glide.with(getContext()).load(itemSong.getCover()).into(civCoverSong1);
                llListSong.setVisibility(View.VISIBLE);
                break;

            default:
                break;
        }
    }
}
