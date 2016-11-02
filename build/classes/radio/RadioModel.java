package radio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/*      ****RadioModel****
 * Author: Marcel Riederer Monclou
 * Performs database queries and statements for the Radio Module.
 * Chooses random artists and tracks for the radio playlist

 */

public class RadioModel {
    Connection connection = null;
    
    ArrayList<Integer> artistIdList = new ArrayList<Integer>();
    ArrayList<Integer> trackIdList;
    
    Random rng = new Random(); 
    int playlistLength = 10;

    
    public RadioModel() throws SQLException{
        try {
            this.connection = SqliteConnection.openConnection();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(RadioModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (this.connection == null) System.exit(1);
    }
    
    public boolean isDbConnected(){
        try {
            return !connection.isClosed();
        } catch (SQLException ex) {
            Logger.getLogger(RadioModel.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    ///***Returns PlayList with artistID, artistName and genre***//
    public PlayList getAll(){
        PlayList pl = new PlayList();
        String artistStatement = "SELECT * FROM artist;";
        try {
            PreparedStatement ps = connection.prepareStatement(artistStatement);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                int artistId = rs.getInt("idartist");
                String  artistName = rs.getString("artistName");
                String genre = rs.getString("artistGenre");
                artistName = artistName.replace("\"", "");
                genre = genre.replace("\"", "");          
                MetaData entry = new MetaData(-1,artistId,null,artistName,genre);
                pl.add(entry);
           }
            ps.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            return null;
        }
        return pl;
    }
    
    ///***Returns PlayList with artistID, artistName and genre***//
    public PlayList getByArtistName(String name){
        PlayList pl = new PlayList();
        String artistStatement = "SELECT * FROM artist WHERE artistName LIKE ?;";
        try {
            PreparedStatement ps = connection.prepareStatement(artistStatement);
            ps.setString(1, "%"+ name +"%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                int artistId = rs.getInt("idartist");
                String  artistName = rs.getString("artistName");
                String genre = rs.getString("artistGenre");
                artistName = artistName.replace("\"", "");
                genre = genre.replace("\"", "");
                MetaData entry = new MetaData(-1,artistId,null,artistName,genre);
                pl.add(entry);
            }
            ps.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            return null;
        }
        return pl;
    }
    
    ///***Returns Playlist with trackID and trackName
    public PlayList getByGenre(String genre){
        /*
            gets idartist FROM artist WITH artistGenre
                picks random artists
            gets idtrack_a FROM track_artist WITH idartist
                picks one random track from each artists
            gets trackName FROM from track
            
            returns idtrack_a and trackName
        */
        ArrayList<Integer> artistIdList = new ArrayList();
        ArrayList<Integer> shortArtistIdList = new ArrayList();
        ArrayList<Integer> tracks;
        this.trackIdList = new ArrayList();
        PlayList trackList = new PlayList();
        
        PreparedStatement ps;
        String artistStatement = "SELECT idartist FROM artist WHERE artistGenre=? ;";
        String track_artistStatement = "SELECT idtrack_a FROM track_artist WHERE idartist_t=?;";
        String trackStatement = "SELECT trackName FROM track WHERE idtrack=?;";

        
        try {
            ps = connection.prepareStatement( artistStatement );
            ps.setString(1,genre);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                int artistId = rs.getInt("idartist");
                artistIdList.add(artistId);
            }
            
            //PICK RANDOM ARTISTS FROM LIST
            if(artistIdList.size() < 10){
                shortArtistIdList = artistIdList;
            }
            else{
                int numbersNeeded = this.playlistLength;
                int max = artistIdList.size();
                //Gets list of random numbers with no repeats
                Set<Integer> generated = new LinkedHashSet<Integer>();
                while (generated.size() < numbersNeeded){
                    Integer next = this.rng.nextInt(max);
                    generated.add(next);
                }
                Iterator itr = generated.iterator();
                while(itr.hasNext()){
                    shortArtistIdList.add( artistIdList.get( (int) itr.next() ));
                }
            }
            
            for(int i=0; i<artistIdList.size(); i++){
                tracks = new ArrayList();
                ps = connection.prepareStatement( track_artistStatement);
                ps.setInt(1,shortArtistIdList.get(i));
                rs = ps.executeQuery();
                while (rs.next()){
                    int trackId = rs.getInt("idtrack_a");
                    tracks.add(trackId);
                }
                int max = tracks.size();
                int pos = this.rng.nextInt(max);
                this.trackIdList.add(tracks.get(pos));
            }

            for(int i=0; i<this.trackIdList.size(); i++){
                ps = connection.prepareStatement(trackStatement);
                ps.setInt(1,this.trackIdList.get(i));
                rs = ps.executeQuery();
                while(rs.next()){
                    String trackName = rs.getString("trackName");
                    MetaData entry = new MetaData(this.trackIdList.get(i),-1,trackName,null,genre);
                    trackList.add(entry);
                }
            }
            ps.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            return trackList;
        }
        
        return trackList;
    }
    
    public boolean addPlaylist(PlayList pl, String plName, boolean visable, int userId){
        
        int plId = 0;
        String playlistStatement = "INSERT INTO playlist (iduser,playlistVisibility,playlistName) VALUES (?,?,?)";   
        String track_playlistStatement = "INSERT INTO track_playlist VALUES (?,?)";

        
        try {
            PreparedStatement ps = connection.prepareStatement(playlistStatement);
            ps.setInt(1,userId);
            ps.setBoolean(2,visable);
            ps.setString(3,plName);
            ps.executeUpdate();
            
            ResultSet rs = ps.getGeneratedKeys();
            while (rs.next())
                 {plId = rs.getInt(1); }
            PreparedStatement ps2 = connection.prepareStatement(track_playlistStatement);
                      
            for(int i=0; i<pl.getSize(); i++){
                ps2.setInt(1,pl.get(i).getTrackId());
                ps2.setInt(2, plId);
                ps2.addBatch();
            }
                ps2.executeBatch();
				ps.close();
				ps2.close();
                return true;
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            return false;
        }
    }
    
}
