/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package player;

/**
 *
 * @author Petra
 */
public class NowPlayingList 
{
    private String trackName; 
    private String trackPath;
    
    NowPlayingList(String trackName,String trackPath)
    {
        this.trackName = trackName; 
        this.trackPath=trackPath; 
    }

    public String getTrackName() 
    {
        return trackName;
    }

    public String getTrackPath() 
    {
        return trackPath;
    }
   
}
