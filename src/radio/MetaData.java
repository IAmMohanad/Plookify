package radio;

/**     ****MetaData****
 * Author: Marcel Riederer Monclou 
 * Stores track data.
 * Gets and sets.
 * 
 */


public class MetaData {

    private int trackId,artistId;
    private String trackName,artistName,genreName;

    public MetaData(int trackId, int artistId, String trackName, String artistName, String genreName){
        this.trackId = trackId;
        this.artistId = artistId;
        this.trackName = trackName;
        this.artistName = artistName;
        this.genreName = genreName; 
    }

    /**
     * @return the trackId
     */
    public int getTrackId() {
        return trackId;
    }

    /**
     * @param trackId the trackId to set
     */
    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    /**
     * @return the artistId
     */
    public int getArtistId() {
        return artistId;
    }

    /**
     * @param artistId the artistId to set
     */
    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    /**
     * @return the trackName
     */
    public String getTrackName() {
        return trackName;
    }

    /**
     * @param trackName the trackName to set
     */
    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    /**
     * @return the artistName
     */
    public String getArtistName() {
        return artistName;
    }

    /**
     * @param artistName the artistName to set
     */
    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    /**
     * @return the genreName
     */
    public String getGenreName() {
        return genreName;
    }

    /**
     * @param genreName the genreName to set
     */
    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }
}
