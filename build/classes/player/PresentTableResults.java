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
public class PresentTableResults 
{
    private  String trackName;
    private  String trackGenre;
    private  int tracklength;
    private  String trackartist;
    private int trackid;

 
     public PresentTableResults(int id,String trackName)
    {
        this.trackName = trackName; 
        this.trackid =id;
    }
     
    public PresentTableResults(String trackName, String trackGenre, int tracklength, String trackartist) 
    {
        this.trackName =trackName;
        this.trackGenre = trackGenre;
        this.tracklength = tracklength;
        this.trackartist = trackartist;
    }
    
   

    public String getTrackName() 
    {
       
        return trackName;
    }

    public String getTrackGenre() 
    {
        
        return trackGenre;
    }

    public int getTracklength() 
    {
     
        return tracklength;
    }

    public String getTrackartist() 
    {
        
        return trackartist;
    }

    public int getTrackid() 
    {
        return trackid;
    }
    
   
    
    
}
