/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package playlist;

/**
 *
 * @author Work
 */
public class PlaylistTracks {
    
    private String name;
    private int time;
    private String artist;
    private String genre;
    
    
    public PlaylistTracks(String name, int time, String artist, String genre){
        this.name = name;
        this.time = time;
        this.artist = artist;
        this.genre = genre;
        
    }

    public String getName() {
        return name;
    }

    public int getTime() {
        return time;
    }

    public String getArtist() {
        return artist;
    }

    public String getGenre() {
        return genre;
    }
    
    
}
//update