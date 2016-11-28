package chanhbc.com.zingmp3.manager;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import chanhbc.com.zingmp3.model.ItemAlbum;
import chanhbc.com.zingmp3.model.StaticFinal;

public class AlbumManager {
    private String[] type;
    private ArrayList<ItemAlbum> itemAlbums;

    public AlbumManager() {
        type = new String[]{"hot", "release_date", "total_play"};
    }

    public void getItemAlbums(int length, int start, int t, final OnGetAlbumOnlineListener listener) {
        final String albumURL = StaticFinal.ALBUM_JSON_URL_PART_1 + length + StaticFinal.ALBUM_JSON_URL_PART_2 + start + StaticFinal.ALBUM_JSON_URL_PART_3 + type[t] + StaticFinal.ALBUM_JSON_URL_PART_4;
        final ArrayList<ItemAlbum> albums = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document document = Jsoup.connect(albumURL).ignoreContentType(true).get();
                    String text = document.text();
                    Gson gson = new Gson();
                    AlbumOnline albumOnline = gson.fromJson(text, AlbumOnline.class);
                    for (int i = 0; i < albumOnline.getAlbumLists().size(); i++) {
                        AlbumOnline.AlbumList albumList = albumOnline.getAlbumLists().get(i);
                        String id = albumList.getPlayListId();
                        String title = albumList.getTitle();
                        String artist = albumList.getArtist();
                        String cover = StaticFinal.COVER_URL + albumList.getCover();
                        ItemAlbum itemAlbum = new ItemAlbum(id, title, artist, cover);
                        albums.add(itemAlbum);
                    }
                    listener.completed(albums);
                } catch (IOException e) {
                    listener.error(e);
                }
            }
        }).start();

    }

    private class AlbumOnline {
        @SerializedName("docs")
        private List<AlbumList> docs;

        private class AlbumList {
            @SerializedName("playlist_id")
            private String playListId;
            @SerializedName("title")
            private String title;
            @SerializedName("artist")
            private String artist;
            @SerializedName("cover")
            private String cover;

            public String getPlayListId() {
                return playListId;
            }

            public String getTitle() {
                return title;
            }

            public String getArtist() {
                return artist;
            }

            public String getCover() {
                return cover;
            }
        }

        public List<AlbumList> getAlbumLists() {
            return docs;
        }
    }

    public interface OnGetAlbumOnlineListener {
        void completed(ArrayList<ItemAlbum> itemAlbums);

        void error(Exception e);
    }
}
