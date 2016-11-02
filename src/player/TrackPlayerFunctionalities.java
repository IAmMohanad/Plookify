/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package player;

import java.io.File;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 *
 * @author Petra
 */
public class TrackPlayerFunctionalities 
{
    private String path; 
    private static Media media;
    private static MediaPlayer mediaPlayer; 
    private static boolean started = false; 
    private int endTimeSec;
    private static Duration endTime;
    
    
    private MediaPlayer createMediaPlayer(final String filename) throws MalformedURLException 
    {
        File file = new File(filename);
 
        final String mediaLocation = file.toURI().toURL().toString();
         media = new Media(mediaLocation);
         mediaPlayer = new MediaPlayer(media);
 
        return mediaPlayer;
    }
   
    public MediaPlayer getActiveMedia()
    {
        return mediaPlayer; 
    }
    
    public void intialiseMedia()
    {
        try 
        {
            started = true; 
            mediaPlayer = createMediaPlayer(path);
        } 
        catch (MalformedURLException ex) 
        {
            Logger.getLogger(TrackPlayerFunctionalities.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void play()
    {  
        started = true;
        mediaPlayer.play();   
    }
    
    public void pause() 
    {
        mediaPlayer.pause();
    }

    public void stop()
    {
        if(started){
        mediaPlayer.stop();
        }
    }
    
    public Duration getDuration()
    {
        endTime =mediaPlayer.getMedia().getDuration();
        return endTime; 
    }
    public String displayTrackLength()
    {
        getDuration();
        endTimeSec = (int)Math.ceil(endTime.toSeconds());
        int minutes = endTimeSec/60;
        int seconds = endTimeSec%60;
        String time = String.format("%02d:%02d",minutes, seconds);
        
        return time;     
    }
 
    public boolean isStarted()
    {
        return started; 
    }
     
    public void setPath(String s)
    {
        path=s; 
    }
    
    public void setStarted(boolean b)
    {
     started = b;   
    }
}
