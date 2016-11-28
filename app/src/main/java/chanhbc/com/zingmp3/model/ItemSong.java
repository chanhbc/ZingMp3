package chanhbc.com.zingmp3.model;

public class ItemSong {
    private String title;
    private String artist;
    private String cover;
    private String source1;
    private String source2;
    private String link1;
    private String link2;

    public ItemSong(String title, String artist, String cover, String source1,String source2, String link1,String link2) {
        this.title = title;
        this.artist = artist;
        this.cover = cover;
        this.source1 = source1;
        this.source2 = source2;
        this.link1 = link1;
        this.link1 = link2;
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

    public String getSource1() {
        return source1;
    }

    public String getSource2() {
        return source2;
    }

    public String getLink1() {
        return link1;
    }

    public String getLink2() {
        return link2;
    }
}
