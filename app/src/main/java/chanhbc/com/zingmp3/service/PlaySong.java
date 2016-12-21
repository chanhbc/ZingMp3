package chanhbc.com.zingmp3.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

public class PlaySong extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener {
    private MediaPlayer mediaPlayer;
    private OnPrepareListener onPrepareListener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinderMedia();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        this.mediaPlayer = mp;
        onPrepareListener.OnPrepare();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        onPrepareListener.OnCompletion();
    }

    public class MyBinderMedia extends Binder {
        public PlaySong getService() {
            return PlaySong.this;
        }
    }

    public void play(String link) {
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

    public void start() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public void setOnPrepareListener(OnPrepareListener listener) {
        this.onPrepareListener = listener;
    }

    public interface OnPrepareListener {
        void OnPrepare();

        void OnCompletion();
    }
}
