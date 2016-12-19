package chanhbc.com.zingmp3.fragment;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
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
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import chanhbc.com.zingmp3.R;
import chanhbc.com.zingmp3.adapter.ItemSongAdapter;
import chanhbc.com.zingmp3.custom.CustomListView;
import chanhbc.com.zingmp3.manager.SongManager;
import chanhbc.com.zingmp3.model.ItemSong;
import chanhbc.com.zingmp3.model.StaticFinal;
import chanhbc.com.zingmp3.service.PlaySong;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.BIND_AUTO_CREATE;

public class SongFragmentManager extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener, PlaySong.OnPrepareListener {
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
    private ImageView ivPlay;
    private ImageView ivNext;
    private ImageView ivPrivous;
    private ImageView ivPlay1;
    private ImageView ivNext1;
    private ImageView ivPrivous1;
    private ImageView ivShuffle;
    private ImageView ivRepeat;
    private boolean isPlay = true;
    private PlaySong playSong;
    private TextView tvTimeCurrent;
    private TextView tvTimeTotal;
    private SeekBar sbTime;

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

                    case StaticFinal.UPDATE_TIME_TEXTVIEW:
                        tvTimeCurrent.setText(getTimeFormat(playSong.getTimeCurrent()));
                        sbTime.setProgress(playSong.getTimeCurrent());
                        break;
                }
                return false;
            }
        });
        Intent intent = new Intent();
        intent.setClass(getActivity(), PlaySong.class);
        getActivity().startService(intent);
        getActivity().bindService(intent, connection, BIND_AUTO_CREATE);
        Log.d("START SERVICE", "");
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d("onServiceConnected", "a");
            PlaySong.MyBinderMedia media = (PlaySong.MyBinderMedia) iBinder;
            playSong = media.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("onServiceDisconnected", "a");
        }
    };

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
        ivPlay = (ImageView) view.findViewById(R.id.iv_play);
        ivNext = (ImageView) view.findViewById(R.id.iv_next);
        ivPrivous = (ImageView) view.findViewById(R.id.iv_privious);
        ivPlay1 = (ImageView) view.findViewById(R.id.iv_play_1);
        ivNext1 = (ImageView) view.findViewById(R.id.iv_next_1);
        ivPrivous1 = (ImageView) view.findViewById(R.id.iv_privious_1);
        ivShuffle = (ImageView) view.findViewById(R.id.iv_shuffle);
        ivRepeat = (ImageView) view.findViewById(R.id.iv_repeat);
        tvTimeCurrent = (TextView) view.findViewById(R.id.tv_time_current);
        tvTimeTotal = (TextView) view.findViewById(R.id.tv_time_total);
        sbTime = (SeekBar) view.findViewById(R.id.sb_time_process);
        ivClose.setOnClickListener(this);
        ivPlay.setOnClickListener(this);
        ivPlay1.setOnClickListener(this);
        ivNext.setOnClickListener(this);
        ivNext1.setOnClickListener(this);
        ivPrivous.setOnClickListener(this);
        ivPrivous1.setOnClickListener(this);
        ivShuffle.setOnClickListener(this);
        ivRepeat.setOnClickListener(this);
        tvArtist1.setOnClickListener(this);
        tvTitle1.setOnClickListener(this);
        civCoverSong1.setOnClickListener(this);
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
                civCoverSong.setImageResource(R.drawable.cover_music);
                civCoverSong.setAnimation(anim_cover_song);
                Glide.with(getContext()).load(itemSong.getCover()).into(civCoverSong);
                llPlayFull.startAnimation(anim_ll_play_song);
                civCoverSong.startAnimation(anim_cover_song);
                ivPlay.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_24dp);
                play();
                break;

            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_artist_1:
            case R.id.tv_title_1:
            case R.id.civ_cover_song_1:
            case R.id.ll_play_song_lite:
                llListSong.setVisibility(View.GONE);
                llPlayLite.setVisibility(View.GONE);
                llPlayFull.setVisibility(View.VISIBLE);
                llPlayFull.startAnimation(anim_ll_play_song);
                tvTitle.setText(itemSong.getTitle());
                tvArtist.setText(itemSong.getArtist());
                if (civCoverSong == null) {
                    Glide.with(getContext()).load(itemSong.getCover()).into(civCoverSong);
                }
                if (isPlay) {
                    ivPlay.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_24dp);
                } else {
                    ivPlay.setBackgroundResource(R.drawable.ic_play_circle_outline_black_24dp);
                }
                break;

            case R.id.iv_close_activity_play:
                Log.d("CLOSE", "");
                llPlayFull.startAnimation(anim_ll_play_song_down);
                llPlayLite.setVisibility(View.VISIBLE);
                if (isPlay) {
                    ivPlay1.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_24dp);
                } else {
                    ivPlay1.setBackgroundResource(R.drawable.ic_play_circle_outline_black_24dp);
                }
                llPlayLite.setOnClickListener(SongFragmentManager.this);
                tvTitle1.setText(itemSong.getTitle());
                tvArtist1.setText(itemSong.getArtist());
                Glide.with(getContext()).load(itemSong.getCover()).into(civCoverSong1);
                civCoverSong1.startAnimation(anim_cover_song);
                llListSong.setVisibility(View.VISIBLE);
                break;

            case R.id.iv_play_1:

            case R.id.iv_play:
                if (isPlay) {
                    isPlay = false;
                    ivPlay.setBackgroundResource(R.drawable.ic_play_circle_outline_black_24dp);
                    pause();
                } else {
                    isPlay = true;
                    ivPlay.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_24dp);
                    playSong.start();
                    startThread();
                }
                break;

            default:
                break;
        }
    }

    private void pause() {
        if (playSong != null) {
            playSong.pause();
        }
    }

    private void play() {
        if (playSong != null) {
            if (isPlay) {
                playSong.play(itemSong.getLink1());
                playSong.setOnPrepareListener(SongFragmentManager.this);
            } else {
                playSong.start();
            }
        }
    }

    private String getTimeFormat(long time) {
        String tm = "";
        int s;
        int m;
        int h;
        s = (int) (time % 60);
        m = (int) ((time - s) / 60);
        if (m >= 60) {
            h = m / 60;
            m = m % 60;
            if (h > 0) {
                if (h < 10)
                    tm += "0" + h + ":";
                else
                    tm += h + ":";
            }
        }
        if (m < 10)
            tm += "0" + m + ":";
        else
            tm += m + ":";
        if (s < 10)
            tm += "0" + s;
        else
            tm += s + "";
        return tm;
    }

    @Override
    public void OnPrepare() {
        tvTimeTotal.setText(getTimeFormat(playSong.getTimeTotal()));
        sbTime.setMax(playSong.getTimeTotal());
        startThread();
    }

    private void startThread() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isPlay) {
                    Message message = new Message();
                    message.what = StaticFinal.UPDATE_TIME_TEXTVIEW;
                    handler.sendMessage(message);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }
}
