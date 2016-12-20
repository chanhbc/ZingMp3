package chanhbc.com.zingmp3.manager;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import chanhbc.com.zingmp3.model.ItemSong;
import chanhbc.com.zingmp3.model.StaticFinal;

public class SongManager {
    private String songURL;

    public SongManager(String key) {
        songURL = StaticFinal.SONG_JSON_URL_PART_1 + key + StaticFinal.SONG_JSON_URL_PART_2;
    }

    public void getItemSongs(final OnGetSongOnlineListener listener) {
        final ArrayList<ItemSong> itemSongs = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document document = Jsoup.connect(songURL).ignoreContentType(true).get();
                    String text = document.text();
                    Gson gson = new Gson();
                    SongOnline songOnline = gson.fromJson(text, SongOnline.class);
                    for (int i = 0; i < songOnline.getSongLists().size(); i++) {
                        SongOnline.SongList songList = songOnline.getSongLists().get(i);
                        String title = songList.getTitle();
                        String artist = songList.getArtist();
                        String cover = StaticFinal.COVER_URL + songList.getCover();
                        SongOnline.SongList.SourceList sourceList = songList.getSource();
                        String source1 = sourceList.getS128();
                        String source2 = sourceList.getS320();
                        SongOnline.SongList.LinkList linkList = songList.getLinkDownload();
                        String link1 = linkList.getLink128();
                        String link2 = linkList.getLink320();
                        ItemSong song = new ItemSong(title, artist, cover, source1, source2, link1, link2);
                        itemSongs.add(song);
                    }
                    listener.completed(itemSongs);
                } catch (IOException e) {
                    listener.error(e);
                }
            }
        }).start();
    }

    private class SongOnline {
        @SerializedName("docs")
        private List<SongList> docs;

        private class SongList {
            @SerializedName("title")
            private String title;
            @SerializedName("artist")
            private String artist;
            @SerializedName("source")
            private SourceList source;

            private class SourceList {
                @SerializedName("128")
                private String s128;
                @SerializedName("320")
                private String s320;

                public String getS128() {
                    return s128;
                }

                public String getS320() {
                    return s320;
                }
            }

            @SerializedName("link_download")
            private LinkList linkDownload;

            private class LinkList {
                @SerializedName("128")
                private String link128;
                @SerializedName("320")
                private String link320;

                public String getLink128() {
                    return link128;
                }

                public String getLink320() {
                    return link320;
                }
            }

            @SerializedName("thumbnail")
            private String cover;

            public String getTitle() {
                return title;
            }

            public String getArtist() {
                return artist;
            }

            public String getCover() {
                return cover;
            }

            public SourceList getSource() {
                return source;
            }

            public LinkList getLinkDownload() {
                return linkDownload;
            }
        }

        public List<SongList> getSongLists() {
            return docs;
        }
    }

    public interface OnGetSongOnlineListener {
        void completed(ArrayList<ItemSong> itemSongs);

        void error(Exception e);
    }
}
