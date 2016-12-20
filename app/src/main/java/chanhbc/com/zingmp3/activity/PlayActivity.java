package chanhbc.com.zingmp3.activity;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import chanhbc.com.zingmp3.App;
import chanhbc.com.zingmp3.R;
import chanhbc.com.zingmp3.adapter.ItemSongAdapter;
import chanhbc.com.zingmp3.custom.CustomListView;
import chanhbc.com.zingmp3.manager.SongManager;
import chanhbc.com.zingmp3.model.ItemSong;
import chanhbc.com.zingmp3.model.StaticFinal;
import chanhbc.com.zingmp3.service.PlaySong;
import de.hdodenhof.circleimageview.CircleImageView;

public class PlayActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener, PlaySong.OnPrepareListener {
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
    private Animation animCoverSong;
    private ImageView ivClose;
    private ImageView ivPlay;
    private ImageView ivNext;
    private ImageView ivPrivous;
    private ImageView ivPlay1;
    private ImageView ivNext1;
    private ImageView ivPrivous1;
    private ImageView ivShuffle;
    private ImageView ivRepeat;
    private ImageView ivDownload;
    private boolean isPlay = true;
    private PlaySong playSong;
    private TextView tvTimeCurrent;
    private TextView tvTimeTotal;
    private SeekBar sbTime;
    private int repeat = 0;
    private int shuffle = 0;
    private Intent service;
    private boolean bound;
    private boolean isServiceRunning;
    private SharedPreferences sp;
    private int index;
    private LinearLayout llSong;
    private ImageView ivActivityPlay;
    private boolean played = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);
        Intent intent = getIntent();
        codeId = intent.getStringExtra("SongId");
        urlCoverAlbum = intent.getStringExtra("SongCover");
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case StaticFinal.UPDATE_SONGLIST:
                        Glide.with(PlayActivity.this).load(urlCoverAlbum).into(ivCoverAlbum);
                        lvSong.setAdapter(itemSongAdapter);
                        lvSong.setOnItemClickListener(PlayActivity.this);
                        break;

                    case StaticFinal.UPDATE_TIME_TEXTVIEW:
                        tvTimeCurrent.setText(getTimeFormat(playSong.getTimeCurrent()));
                        sbTime.setProgress(playSong.getTimeCurrent());
                        break;
                }
                return false;
            }
        });
        if (isMyServiceRunning(PlaySong.class)) {
            isServiceRunning = true;
        } else {
            isServiceRunning = false;
        }
        service = new Intent();
        service.setClass(PlayActivity.this, PlaySong.class);
        startService(service);
        bindService(service, connection, BIND_AUTO_CREATE);
        initViews();
        initComponents();
    }

    private void initViews() {
        lvSong = (CustomListView) findViewById(R.id.lv_song);
        ivCoverAlbum = (ImageView) findViewById(R.id.iv_cover_album);
        llPlayLite = (LinearLayout) findViewById(R.id.ll_play_song_lite);
        llPlayLite.setVisibility(View.GONE);
        llPlayLite.setOnClickListener(this);
        llPlayFull = (LinearLayout) findViewById(R.id.ll_play_song_full);
        llPlayFull.setVisibility(View.GONE);
        llListSong = (LinearLayout) findViewById(R.id.ll_list_song);
        civCoverSong1 = (CircleImageView) findViewById(R.id.civ_cover_song_1);
        civCoverSong = (CircleImageView) findViewById(R.id.civ_cover_song);
        tvArtist1 = (TextView) findViewById(R.id.tv_artist_1);
        tvTitle1 = (TextView) findViewById(R.id.tv_title_1);
        tvArtist = (TextView) findViewById(R.id.tv_artist);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        ivClose = (ImageView) findViewById(R.id.iv_close_activity_play);
        ivPlay = (ImageView) findViewById(R.id.iv_play);
        ivNext = (ImageView) findViewById(R.id.iv_next);
        ivPrivous = (ImageView) findViewById(R.id.iv_privious);
        ivPlay1 = (ImageView) findViewById(R.id.iv_play_1);
        ivNext1 = (ImageView) findViewById(R.id.iv_next_1);
        ivPrivous1 = (ImageView) findViewById(R.id.iv_privious_1);
        ivShuffle = (ImageView) findViewById(R.id.iv_shuffle);
        ivRepeat = (ImageView) findViewById(R.id.iv_repeat);
        tvTimeCurrent = (TextView) findViewById(R.id.tv_time_current);
        tvTimeTotal = (TextView) findViewById(R.id.tv_time_total);
        sbTime = (SeekBar) findViewById(R.id.sb_time_process);
        ivDownload = (ImageView) findViewById(R.id.iv_download);
        llSong = (LinearLayout) findViewById(R.id.ll_song);
        ivActivityPlay = (ImageView) findViewById(R.id.iv_activity_play);
        ivDownload.setOnClickListener(this);
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
    }

    private void initComponents() {
        anim_ll_play_song = AnimationUtils.loadAnimation(PlayActivity.this, R.anim.anim_translate_ll_play_song_full);
//        anim_ll_play_song_down = AnimationUtils.loadAnimation(getContext(), R.anim.anim_translate_ll_play_song_full_down);
        animCoverSong = AnimationUtils.loadAnimation(PlayActivity.this, R.anim.anim_rotate_image_cover_song);
        songManager = new SongManager(codeId);
        songManager.getItemSongs(new SongManager.OnGetSongOnlineListener() {
            @Override
            public void completed(ArrayList<ItemSong> itemSongs) {
                PlayActivity.this.itemSongs = itemSongs;
                itemSongAdapter = new ItemSongAdapter(itemSongs);
                Message message = new Message();
                message.what = StaticFinal.UPDATE_SONGLIST;
                handler.sendMessage(message);
            }

            @Override
            public void error(Exception e) {

            }
        });
        if (isServiceRunning) {
            llPlayLite.setVisibility(View.VISIBLE);
            getInfo();
            if (isPlay) {
                ivPlay1.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_24dp);
            } else {
                ivPlay1.setBackgroundResource(R.drawable.ic_play_circle_outline_black_24dp);
            }
            startThread();
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            PlaySong.MyBinderMedia media = (PlaySong.MyBinderMedia) iBinder;
            playSong = media.getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            playSong = null;
            bound = false;
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.lv_song:
                index = position;
                llPlayFull.setVisibility(View.VISIBLE);
                llSong.setVisibility(View.GONE);
                itemSong = itemSongs.get(position);
                isPlay = true;
                if (shuffle == 1) {
                    ivShuffle.setImageResource(R.drawable.ic_shuffle_black_24dp);
                }
                if (repeat == 1) {
                    ivRepeat.setImageResource(R.drawable.ic_repeat_one_black_24dp);
                } else if (repeat == 2) {
                    ivRepeat.setImageResource(R.drawable.ic_repeat_black_24dp);
                }
                play();
                break;

            default:
                break;
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_artist_1:
            case R.id.tv_title_1:
            case R.id.civ_cover_song_1:
            case R.id.ll_play_song_lite:
                llSong.setVisibility(View.GONE);
                llPlayFull.setVisibility(View.VISIBLE);
                tvTitle.setText(itemSong.getTitle());
                tvArtist.setText(itemSong.getArtist());
                Glide.with(PlayActivity.this).load(itemSong.getCover()).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        civCoverSong.setImageBitmap(resource);
                        civCoverSong.startAnimation(animCoverSong);
                    }
                });
                if (isPlay) {
                    ivPlay.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_24dp);
                } else {
                    ivPlay.setBackgroundResource(R.drawable.ic_play_circle_outline_black_24dp);
                }
                if (shuffle == 1) {
                    ivShuffle.setImageResource(R.drawable.ic_shuffle_black_24dp);
                }
                if (repeat == 1) {
                    ivRepeat.setImageResource(R.drawable.ic_repeat_one_black_24dp);
                } else if (repeat == 2) {
                    ivRepeat.setImageResource(R.drawable.ic_repeat_black_24dp);
                }
                break;

            case R.id.iv_close_activity_play:
                civCoverSong.clearAnimation();
                llSong.setVisibility(View.VISIBLE);
                llPlayFull.setVisibility(View.GONE);
                llPlayLite.setVisibility(View.VISIBLE);
                if (isPlay) {
                    ivPlay1.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_24dp);
                } else {
                    ivPlay1.setBackgroundResource(R.drawable.ic_play_circle_outline_black_24dp);
                }
                llPlayLite.setOnClickListener(PlayActivity.this);
                tvTitle1.setText(itemSong.getTitle());
                tvArtist1.setText(itemSong.getArtist());
                ivPlay1.setOnClickListener(PlayActivity.this);
                Glide.with(PlayActivity.this).load(itemSong.getCover()).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        civCoverSong1.setImageBitmap(resource);
                        civCoverSong1.setAnimation(animCoverSong);
                    }
                });
                break;

            case R.id.iv_play_1:
                if (isPlay) {
                    isPlay = false;
                    ivPlay1.setBackgroundResource(R.drawable.ic_play_circle_outline_black_24dp);
                    pause();
                } else {
                    isPlay = true;
                    ivPlay1.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_24dp);
                    playSong.start();
                    startThread();
                }
                break;

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

            case R.id.iv_repeat:
                repeat = (++repeat) % 3;
                switch (repeat) {
                    case 0:
                        ivRepeat.setImageResource(R.drawable.ic_repeat_white_24dp);
                        break;

                    case 1:
                        ivRepeat.setImageResource(R.drawable.ic_repeat_one_black_24dp);
                        break;

                    case 2:
                        ivRepeat.setImageResource(R.drawable.ic_repeat_black_24dp);
                        break;

                    default:
                        break;
                }
                break;

            case R.id.iv_shuffle:
                shuffle = (++shuffle) % 2;
                switch (shuffle) {
                    case 0:
                        ivShuffle.setImageResource(R.drawable.ic_shuffle_white_24dp);
                        break;

                    case 1:
                        ivShuffle.setImageResource(R.drawable.ic_shuffle_black_24dp);
                        break;

                    default:
                        break;
                }
                break;

            case R.id.iv_next_1:
                nextSong();
                break;

            case R.id.iv_next:
                nextSong();
                break;

            case R.id.iv_privious_1:
                privousSong();
                break;

            case R.id.iv_privious:
                privousSong();
                break;

            case R.id.iv_download:
                AlertDialog alertDialog = new AlertDialog.Builder(PlayActivity.this)
                        .setTitle("Download")
                        .setMessage("Choose quaity download?")
                        .setPositiveButton("128 kbps", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("Link", "" + itemSong.getLink1());
                                DownloadMusic downloadMusic = new DownloadMusic(itemSong.getTitle().replace(" ", "_") + "_128kbps");
                                downloadMusic.execute(itemSong.getLink1());
                            }
                        })
                        .setNegativeButton("320 kbps", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DownloadMusic downloadMusic = new DownloadMusic(itemSong.getTitle().replace(" ", "_") + "_320kbps");
                                downloadMusic.execute(itemSong.getLink2());
                            }
                        })
                        .create();
                alertDialog.show();
                break;

            default:
                break;
        }
    }

    private void nextSong() {
        if (repeat != 1) {
            if (shuffle == 0) {
                if (index < itemSongs.size() - 1) {
                    index += 1;
                } else if (index == itemSongs.size() - 1) {
                    index = 0;
                }
            } else {
                index = (new Random()).nextInt(itemSongs.size());
            }
            itemSong = itemSongs.get(index);
        }
        play();
    }

    private void privousSong() {
        if (playSong.getTimeCurrent() < 5) {
            if (shuffle == 0) {
                if (index == 0) {
                    index = itemSongs.size() - 1;
                } else if (index > 0) {
                    index -= 1;
                }
            } else {
                index = (new Random()).nextInt(itemSongs.size());
            }
            itemSong = itemSongs.get(index);
        }
        play();
    }

    private class DownloadMusic extends AsyncTask<String, Void, Void> {
        private HttpURLConnection connection;
        private String name;

        public DownloadMusic(String name) {
            this.name = name;
        }


        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                Log.d("URL", params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/" + Environment.DIRECTORY_DOWNLOADS + "/" + name + ".mp3");
                if (file.exists()) {
                    file.delete();
                }
                FileOutputStream fos = new FileOutputStream(file);
                InputStream is = connection.getInputStream();
                int length;
                int total = 0;
                byte[] bytes = new byte[1024];
                while ((length = is.read(bytes)) != -1) {
                    fos.write(bytes, 0, length);
                    total += length;
                    Log.d("Percent", length / total + "");
                }
                is.close();
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(PlayActivity.this, "Tải thành công", Toast.LENGTH_SHORT).show();
            super.onPostExecute(aVoid);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        addInfo();
        if (bound) {
            unbindService(connection);
            bound = false;
        }
    }

    @Override
    public void onBackPressed() {
        if (llPlayFull.getVisibility() == View.VISIBLE) {
            llSong.setVisibility(View.VISIBLE);
            llPlayFull.setVisibility(View.GONE);
            llPlayLite.setVisibility(View.VISIBLE);
            if (isPlay) {
                ivPlay1.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_24dp);
            } else {
                ivPlay1.setBackgroundResource(R.drawable.ic_play_circle_outline_black_24dp);
            }
            llPlayLite.setOnClickListener(PlayActivity.this);
            tvTitle1.setText(itemSong.getTitle());
            tvArtist1.setText(itemSong.getArtist());
            ivPlay1.setOnClickListener(PlayActivity.this);
            Glide.with(PlayActivity.this).load(itemSong.getCover()).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    civCoverSong1.setImageBitmap(resource);
                    civCoverSong1.setAnimation(animCoverSong);
                }
            });
            return;
        }
        super.onBackPressed();
    }

    private void getInfo() {
        sp = (App.getContext()).getSharedPreferences("Rank", Context.MODE_PRIVATE);
        tvArtist1.setText(sp.getString("artist", ""));
        tvTitle1.setText(sp.getString("title", ""));
        tvTimeTotal.setText(getTimeFormat(sp.getInt("timetotal", 0)));
        tvTimeCurrent.setText(getTimeFormat(sp.getInt("timecurrent", 0)));
        sbTime.setProgress(sp.getInt("timecurrent", 0));
        sbTime.setMax(sp.getInt("timetotal", 0));
        isPlay = sp.getBoolean("isplay", false);
        index = sp.getInt("index", 0);
        shuffle = sp.getInt("shuffle", 0);
        repeat = sp.getInt("repeat", 0);
        Glide.with(PlayActivity.this).load(sp.getString("cover", "")).into(civCoverSong1);
        itemSong = new ItemSong(sp.getString("title", ""), sp.getString("artist", ""), sp.getString("cover", ""), sp.getString("source1", ""),
                sp.getString("source2", ""), sp.getString("link1", ""), sp.getString("link2", ""));
    }

    private void addInfo() {
        sp = (App.getContext()).getSharedPreferences("Rank", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (played) {
            editor.putString("title", itemSong.getTitle());
            editor.putString("artist", itemSong.getArtist());
            editor.putString("cover", itemSong.getCover());
            editor.putString("link1", itemSong.getLink1());
            editor.putString("link2", itemSong.getLink2());
            editor.putString("source1", itemSong.getSource1());
            editor.putString("source2", itemSong.getSource2());
            editor.putInt("timetotal", playSong.getTimeTotal());
            editor.putInt("timecurrent", playSong.getTimeCurrent());
            editor.putBoolean("isplay", isPlay);
            editor.putInt("index", index);
            editor.putInt("repeat", repeat);
            editor.putInt("shuffle", shuffle);
        }
        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void pause() {
        if (playSong != null) {
            playSong.pause();
        }
    }

    private void play() {
        tvTitle.setText(itemSong.getTitle());
        tvArtist.setText(itemSong.getArtist());
        tvTitle1.setText(itemSong.getTitle());
        tvArtist1.setText(itemSong.getArtist());
        Glide.with(PlayActivity.this).load(itemSong.getCover()).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                civCoverSong.setImageBitmap(resource);
                civCoverSong.startAnimation(animCoverSong);
                ivActivityPlay.setImageBitmap(resource);
                civCoverSong1.setImageBitmap(resource);
                civCoverSong1.startAnimation(animCoverSong);
                ivActivityPlay.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ivActivityPlay.setAlpha((float) 0.1);
            }
        });
        played = true;
        ivPlay.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_24dp);
        if (playSong != null) {
            if (isPlay) {
                playSong.play(itemSong.getSource1());
                playSong.setOnPrepareListener(PlayActivity.this);
            } else {
                playSong.start();
            }
        }
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

    @Override
    public void OnPrepare() {
        tvTimeTotal.setText(getTimeFormat(playSong.getTimeTotal()));
        sbTime.setMax(playSong.getTimeTotal());
        startThread();
    }

    @Override
    public void OnCompletion() {
        Log.d("Completion", "hihi");
        nextSong();
    }
}
