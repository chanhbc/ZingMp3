package chanhbc.com.zingmp3.model;

public class ItemAlbum {
    private String id;
    private String title;
    private String artist;
    private String cover;

    public ItemAlbum(String id, String title, String artist, String cover) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.cover = cover;
    }

    public String getId() {
        return id;
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
