package bluebase.in.pioneermusicgym;

public class SongQueueItems {
    private int songId;
    private String songTitle;
    private String languageCode;
    private String fileLocation;
    private String lyricsLocation;
    private String englishLyricsLocation;
    private String duration;

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public String getLyricsLocation() {
        return lyricsLocation;
    }

    public void setLyricsLocation(String lyricsLocation) {
        this.lyricsLocation = lyricsLocation;
    }

    public String getEnglishLyricsLocation() {
        return englishLyricsLocation;
    }

    public void setEnglishLyricsLocation(String englishLyricsLocation) {
        this.englishLyricsLocation = englishLyricsLocation;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
