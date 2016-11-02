package player;

import GUI_Template.DBConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Petra
 * This class displays method to retrieve any data required for the track player 
 * from the database 
 */
public class DatabaseQueries
{
    private ResultSet queryResult;
    private int npid;
    private final Connection c = DBConnection.getConnection();
    
    private ObservableList<PresentTableResults> DataTable = FXCollections.observableArrayList();
    private  ArrayList<NowPlayingList>nowPlayingList = new ArrayList <>();
    private int userID; 
    
    public ArrayList<NowPlayingList> getNowPlayingList() throws SQLException 
    {
        userID = new TrackPlayerController().getUserId();
           
            PreparedStatement stmt = c.prepareStatement("SELECT idNP FROM NowPlayingUser WHERE idUser ="+userID);
            ResultSet result = stmt.executeQuery();
            String npid = result.getString("idNP");
            stmt.close();
            
            PreparedStatement stmt2=c.prepareStatement("SELECT TrackPath ,TrackName  FROM NowPlayingList WHERE idNowPlaying="+npid);
            ResultSet result1 = stmt2.executeQuery();
            while(result1.next())
            {
                nowPlayingList.add(new NowPlayingList(result1.getString("TrackName"),result1.getString("TrackPath")));
      
            }
            stmt2.close();
            
            return nowPlayingList;
    }
    
    public ObservableList<PresentTableResults>  displayNowPlayingList()
   {
       userID = new TrackPlayerController().getUserId();
      // System.out.println("petra " + userID);
       try
        {
           
            PreparedStatement stmt = c.prepareStatement("SELECT idNP FROM NowPlayingUser WHERE idUser = '"+userID+"'");
            ResultSet result = stmt.executeQuery();
            if(result.next()==false)
            {
                return null;
            }
            else{
            String npid = result.getString("idNP");
            stmt.close();
            
            PreparedStatement stmt2 = c.prepareStatement("SELECT trackName,idTrack FROM NowPlayingList WHERE idNowPlaying =" +npid +"");
            ResultSet result1 = stmt2.executeQuery();
            
           while(result1.next())
           {
               
            String trackName=(result1.getString("trackName"));
            int trackid = (result1.getInt("idTrack"));
            DataTable.add(new PresentTableResults(trackid,trackName));
           // System.out.println("done");
           }
         
            stmt.close();
            
        return DataTable; 
            }
        }
        catch ( SQLException e ) 
        {
            throw new RuntimeException(e);
        }
   }
    
    
    
   public ObservableList<PresentTableResults>  getBySong()
   {
       String  textBoxValue = new TrackPlayerController().getSearchBoxValue();
       try
        {
           
            PreparedStatement stmt = c.prepareStatement("SELECT trackName,artistGenre,trackLength,artistName FROM artist,track,track_artist WHERE track.trackName ='"+ textBoxValue+"' AND track.idtrack=track_artist.idtrack_a AND track_artist.idartist_t=artist.idartist");
          
            ResultSet result = stmt.executeQuery();
            
           while(result.next())
           {
               
            String trackName=(result.getString("trackName"));
            String trackGenre=(result.getString("artistGenre"));
            int tracksLength=(result.getInt("trackLength"));
            String trackArtist=(result.getString("artistName"));
           
            DataTable.add(new PresentTableResults(trackName,trackGenre,tracksLength,trackArtist));
           
           }
         
            stmt.close();
            
        }
        catch ( Exception e ) 
        {
            throw new RuntimeException(e);
        }
        return DataTable; 
   }
   
   public ObservableList<PresentTableResults>  getByArtist()
   {
        String  textBoxValue = new TrackPlayerController().getSearchBoxValue();
       try
        {
           
            PreparedStatement stmt = c.prepareStatement("SELECT trackName,artistGenre,trackLength,artistName FROM artist,track,track_artist WHERE artist.artistName ='"+ textBoxValue+"' AND track.idtrack=track_artist.idtrack_a AND track_artist.idartist_t=artist.idartist");
          
            ResultSet result = stmt.executeQuery();
            
           while(result.next())
           {
               
            String trackName=(result.getString("trackName"));
            String trackGenre=(result.getString("artistGenre")); 
            int tracksLength=(result.getInt("trackLength"));
            String trackArtist=(result.getString("artistName"));
           
            DataTable.add(new PresentTableResults(trackName,trackGenre,tracksLength,trackArtist));
           
           }
         
            stmt.close();
            
        }
        catch ( Exception e ) 
        {
            throw new RuntimeException(e);
        }
        return DataTable; 
   }
   
   public ObservableList<PresentTableResults> getByGenre()
   {
       String  textBoxValue = new TrackPlayerController().getSearchBoxValue();
       try
        {
           
            PreparedStatement stmt = c.prepareStatement("SELECT trackName,artistGenre,trackLength,artistName FROM artist,track,track_artist WHERE artist.artistGenre ='"+ textBoxValue+"' AND track.idtrack=track_artist.idtrack_a AND track_artist.idartist_t=artist.idartist");
          
            ResultSet result = stmt.executeQuery();
            
           while(result.next())
           {
               
            String trackName=(result.getString("trackName"));
            String trackGenre=(result.getString("artistGenre"));
            int tracksLength=(result.getInt("trackLength"));
            String trackArtist=(result.getString("artistName"));
           
            DataTable.add(new PresentTableResults(trackName,trackGenre,tracksLength,trackArtist));
           
           }
           
            stmt.close();
            
        }
        catch ( SQLException e ) 
        {
            throw new RuntimeException(e);
        }
        return DataTable; 
   }
   
   public void setResult(ResultSet result )
   {
       queryResult=result; 
   }

    public ResultSet getQueryResult() {
        return queryResult;
    }

    public int getUserNPID()
    {
            try {
            userID = new TrackPlayerController().getUserId();

            PreparedStatement stmt = c.prepareStatement("SELECT idNP FROM NowPlayingUser WHERE idUser =" + userID);
            ResultSet result = stmt.executeQuery();
            if (result.next() == false) {
                npid = -1;
                return npid;
            } else {
                npid = result.getInt("idNP");
                stmt.close();

            }

        } catch (SQLException ex) {
            Logger.getLogger(DatabaseQueries.class.getName()).log(Level.SEVERE, null, ex);
        }
        return npid;
    }

    public void createNPID() {
        try {
            PreparedStatement stmt1 = c.prepareStatement("INSERT INTO NowPlayingUser(idUser)VALUES("+userID+")");
            stmt1.executeUpdate();
            stmt1.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseQueries.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void createNPID(int uID) {
        try {
            PreparedStatement stmt1 = c.prepareStatement("INSERT INTO NowPlayingUser(idUser)VALUES("+uID+")");
            stmt1.executeUpdate();
            stmt1.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseQueries.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addSingleTrack() {
        try {
            npid = getUserNPID();
            if (npid == -1) 
            {
                createNPID();
                npid = getUserNPID();

            }
         
            
           
            ObservableList<PresentTableResults> selected = new TrackPlayerController().getSelectedItemData();
           
            for(int i=0;i<selected.size();i++)
            {
                String name = selected.get(i).getTrackName();
                String genre = selected.get(i).getTrackGenre();
                String artist = selected.get(i).getTrackartist();
                
                //String sql = SELECT trackPath,idtrack FROM track,artist,track_artist ta WHERE artist.artistName ='"Drake"' AND artist.artistGenre='"Hip-hop"' AND track.trackName= "Track 1" AND ta.idtrack_a=track.idtrack AND ta.idartist_t=artist.idartist
                
                String sql = "SELECT trackPath,idtrack FROM track,artist,track_artist ta WHERE artist.artistName ='"+ artist+"' AND artist.artistGenre='"+genre+"'"+"AND track.trackName='"+name+"'"+"AND ta.idtrack_a=track.idtrack AND ta.idartist_t=artist.idartist";
                PreparedStatement stmt3 = c.prepareStatement(sql);
                ResultSet result2 = stmt3.executeQuery();
                String tPath = result2.getString("trackPath");
                int tid = result2.getInt("idtrack");
                stmt3.close();
                
                PreparedStatement stmt4 = c.prepareStatement("INSERT INTO NowPlayingList(idNowPlaying,TrackName,TrackPath,idTrack)VALUES("+npid+",'"+name+"','"+tPath+"',"+tid+")");
                stmt4.executeUpdate();   
                stmt4.close();
            }
            
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(DatabaseQueries.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public boolean addAllTrack()
    {
        try { 
             npid =  getUserNPID();
            
            if(npid==-1)
            {
                createNPID();
                npid = getUserNPID();
                
            }
            
            
            ObservableList<PresentTableResults> selected = new TrackPlayerController().getAllData();
            if(selected==null)
            {
                new AlertBox().display("Error","No records to add");
                return false;
            }
            else
            {
            for(int i=0;i<selected.size();i++)
            {
                String name = selected.get(i).getTrackName();
                String genre = selected.get(i).getTrackGenre();
                String artist = selected.get(i).getTrackartist();
              
                String sql = "SELECT trackPath,idtrack FROM track,artist,track_artist ta WHERE artist.artistName ='"+ artist+"' AND artist.artistGenre='"+genre+"'"+"AND track.trackName='"+name+"'"+"AND ta.idtrack_a=track.idtrack AND ta.idartist_t=artist.idartist";
                PreparedStatement stmt3 = c.prepareStatement(sql);
                ResultSet result2 = stmt3.executeQuery();
                String tPath = result2.getString("trackPath");
                int tid = result2.getInt("idtrack");
                stmt3.close();
                
                PreparedStatement stmt4 = c.prepareStatement("INSERT INTO NowPlayingList(idNowPlaying,TrackName,TrackPath,idTrack)VALUES("+npid+",'"+name+"','"+tPath+"',"+tid+")");
                stmt4.executeUpdate();   
                stmt4.close();
            }
            
        }
        }
        catch (SQLException ex) 
        {
            Logger.getLogger(DatabaseQueries.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
        
    public void deleteItemFromNowPlaying()
    {
        try 
        {
            PreparedStatement stmt = c.prepareStatement("DELETE FROM NowPlayingList WHERE idTrack="+new TrackPlayerController().getSelectedID()+" AND TrackName='"+new TrackPlayerController().getSelectedName()+"'");
            stmt.executeUpdate();
            stmt.close();
        }
        catch (SQLException ex) 
        {
            Logger.getLogger(DatabaseQueries.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
