package chanhbc.com.zingmp3.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.IOException;

import chanhbc.com.zingmp3.R;
import chanhbc.com.zingmp3.activity.MainActivity;
import chanhbc.com.zingmp3.activity.PlayActivity;
import chanhbc.com.zingmp3.model.ItemSong;
import chanhbc.com.zingmp3.model.StaticFinal;

public class PlaySong extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener {
    private MediaPlayer mediaPlayer;
    private OnListener onListener;
    private NotificationManagerCompat notification;
    private NotificationCompat.Builder builder;
    private Notification mNotification;
    private RemoteViews views;
    private RemoteViews smallViews;
    private boolean isPlay = false;
    private String songId;
    private String urlCoverAlbum;

    @Override
    public void onCreate() {
        super.onCreate();
        notification = NotificationManagerCompat.from(this);
    }

    private void customNotification(ItemSong itemSong) {
        views = new RemoteViews(getPackageName(), R.layout.custom_nitification);
        smallViews = new RemoteViews(getPackageName(), R.layout.custom_noti_small);

        Intent intentNoti = new Intent();
        intentNoti.setAction("OLD_MUSIC_1");
        intentNoti.setClass(this, PlayActivity.class);
        intentNoti.putExtra("SongId", songId);
        intentNoti.putExtra("SongCover", urlCoverAlbum);
        intentNoti.putExtra("service", true);
        intentNoti.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentNoti, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentPrev = new Intent();
        intentPrev.setClass(this, PlaySong.class);
        intentPrev.setAction(StaticFinal.PREV_ACTION_NO);
        PendingIntent pendingPrev = PendingIntent.getService(this, 0, intentPrev, 0);

        Intent intentPlay = new Intent();
        intentPlay.setClass(this, PlaySong.class);
        intentPlay.setAction(StaticFinal.PLAY_ACTION_NO);
        PendingIntent pendingPlay = PendingIntent.getService(this, 0, intentPlay, 0);

        Intent intentNext = new Intent();
        intentNext.setClass(this, PlaySong.class);
        intentNext.setAction(StaticFinal.NEXT_ACTION_NO);
        PendingIntent pendingNext = PendingIntent.getService(this, 0, intentNext, 0);

        Intent intentClose = new Intent();
        intentClose.setClass(this, PlaySong.class);
        intentClose.setAction(StaticFinal.STOP_FOREGROUND_ACTION);
        PendingIntent pendingClose = PendingIntent.getService(this, 0, intentClose, 0);

        changeDisplayNo(itemSong);

        views.setOnClickPendingIntent(R.id.iv_privious, pendingPrev);
        views.setOnClickPendingIntent(R.id.iv_play, pendingPlay);
        views.setOnClickPendingIntent(R.id.iv_next, pendingNext);
        views.setOnClickPendingIntent(R.id.iv_close, pendingClose);

        smallViews.setOnClickPendingIntent(R.id.iv_play, pendingPlay);
        smallViews.setOnClickPendingIntent(R.id.iv_next, pendingNext);
        smallViews.setOnClickPendingIntent(R.id.iv_close, pendingClose);

        builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_mp3);
        builder.setOngoing(true);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);
        mNotification = builder.build();
        mNotification.contentView = smallViews;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mNotification.bigContentView = views;
        }
        startForeground(StaticFinal.ID_NO_MEDIA, mNotification);
    }

    private void changeDisplayNo(ItemSong song) {
        views.setTextViewText(R.id.tv_song_title, song.getTitle());
        views.setTextViewText(R.id.tv_song_artist, song.getArtist());
        smallViews.setTextViewText(R.id.tv_song_title, song.getTitle());
        smallViews.setTextViewText(R.id.tv_song_artist, song.getArtist());
        Glide.with(this).load(song.getCover()).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                views.setImageViewBitmap(R.id.iv_cover, resource);
                smallViews.setImageViewBitmap(R.id.iv_cover, resource);
                startForeground(StaticFinal.ID_NO_MEDIA, mNotification);
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinderMedia();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        songId = intent.getStringExtra("SongId");
        urlCoverAlbum = intent.getStringExtra("SongCover");
        if (StaticFinal.PLAY_ACTION_NO.equals(intent.getAction())) {
            handlerPlayAndPause();
        } else if (StaticFinal.STOP_FOREGROUND_ACTION.equals(intent.getAction())) {
            handlerStop(intent);
        } else if (StaticFinal.NEXT_ACTION_NO.equals(intent.getAction())) {
            handlerNext();
        } else if (StaticFinal.PREV_ACTION_NO.equals(intent.getAction())) {
            handlerPrev();
        }
        return START_NOT_STICKY;
    }

    private void handlerStop(Intent intent) {
        stopForeground(false);
        onListener.OnPauseSong();
        stopService(intent);
        notification.cancel(StaticFinal.ID_NO_MEDIA);
    }

    private void handlerNext() {
        onListener.OnNextSong();
    }

    private void handlerPrev() {
        onListener.OnPrevSong();
    }

    private void handlerPlayAndPause() {
        changeIcon();
        onListener.OnPlaySong();
    }

    private void changeIcon() {
        if (isPlay) {
            views.setImageViewResource(R.id.iv_play, R.drawable.ic_pause_circle_outline_black_24dp);
            smallViews.setImageViewResource(R.id.iv_play, R.drawable.ic_pause_circle_outline_black_24dp);
        } else {
            views.setImageViewResource(R.id.iv_play, R.drawable.ic_play_circle_outline_black_24dp);
            smallViews.setImageViewResource(R.id.iv_play, R.drawable.ic_play_circle_outline_black_24dp);
        }
        startForeground(StaticFinal.ID_NO_MEDIA, mNotification);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        this.mediaPlayer = mp;
        onListener.OnPrepare();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        onListener.OnCompletion();
    }

    public class MyBinderMedia extends Binder {
        public PlaySong getService() {
            return PlaySong.this;
        }
    }

    public void play(String link, ItemSong song) {
        customNotification(song);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(this, Uri.parse(link));
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnCompletionListener(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        isPlay = true;
        changeIcon();
    }

    public int getTimeTotal() {
        return mediaPlayer.getDuration() / 1000;
    }

    public void seek(int time) {
        mediaPlayer.seekTo(time);
    }

    public int getTimeCurrent() {
        return mediaPlayer.getCurrentPosition() / 1000;
    }

    public void start(ItemSong song) {
        customNotification(song);
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
        isPlay = true;
        changeIcon();
    }

    public void stop() {
        mediaPlayer.pause();
    }

    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
        isPlay = false;
        changeIcon();
    }

    public void setOnListener(OnListener listener) {
        this.onListener = listener;
    }

    public interface OnListener {
        void OnPrepare();

        void OnCompletion();

        void OnPlaySong();

        void OnNextSong();

        void OnPrevSong();

        void OnPauseSong();
    }
}
