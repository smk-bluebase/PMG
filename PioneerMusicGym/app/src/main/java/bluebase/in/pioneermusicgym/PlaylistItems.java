package bluebase.in.pioneermusicgym;

public class PlaylistItems {
    private int playlistId;
    private String playlistTitle;
    private String numberOfSongs;
    private String createdOn;

    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }

    public String getPlaylistTitle() {
        return playlistTitle;
    }

    public void setPlaylistTitle(String playlistTitle) {
        this.playlistTitle = playlistTitle;
    }

    public String getNumberOfSongs() {
        return numberOfSongs;
    }

    public void setNumberOfSongs(String numberOfSongs) {
        this.numberOfSongs = numberOfSongs;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }
}