/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package player;

import GUI_Template.User;
import account.Account;
import static java.lang.Math.floor;
import static java.lang.String.format;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * FXML Controller class
 *
 * @author Petra
 */
public class TrackPlayerController implements Initializable {

    
    @FXML
    private Label song_lbl;
    @FXML
    private Label curentTime_lbl;
    @FXML
    private Slider slider;
    @FXML
    private TextField search_tbox;
    @FXML
    private TableView<PresentTableResults> searchResult_table;
    @FXML
    private TableColumn<PresentTableResults, String> trackName_column;
    @FXML
    private TableColumn< PresentTableResults, String> genre_column;
    @FXML
    private TableColumn<PresentTableResults, Integer> length_column;
    @FXML
    private TableColumn<PresentTableResults, String> artist_column;
    @FXML
    private RadioButton artist_rbtn;
    @FXML
    private RadioButton song_rbtn;
    @FXML
    private RadioButton genre_rbtn;
    @FXML
    private Label addedtoPlaylist;     
    @FXML
    private TableView<PresentTableResults> nowPlayingBox;
    @FXML
    private TableColumn<PresentTableResults, String> track_column;
    @FXML
    private Label userid_lbl;
    @FXML
    private TableColumn<PresentTableResults, Integer> npid_column;

    private static MediaPlayer currentTrack;
    private static Duration duration;
    private static TrackPlayerFunctionalities track;
    private static ObservableList<PresentTableResults> DataTable,selectedItemData, allData;
    private static ObservableList<PresentTableResults> npDataTable=FXCollections.observableArrayList();
    private static String searchbox,nptrackName;
    private static int user;
    private DatabaseQueries db = new DatabaseQueries();
    private static int npid, trackPosition = 0;
    private static ArrayList<NowPlayingList> nowPlayingList;
    @FXML
    private ImageView image;
    @FXML
    private Button previous_btn;
    @FXML
    private Button play_btn;
    @FXML
    private Button pause_btn;
    @FXML
    private Button next_btn;
    @FXML
    private Button submit_btn;
    @FXML
    private Button add_btn;
    @FXML
    private Button addAll_btn;
    @FXML
    private Button removeTrack;
    @FXML
    private ToggleGroup toggle;


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //set image
        Image im = new Image("file:src/Images/MusicNotes.png");
        image.setImage(im);
        
        
        
        Account acc=User.getAcc();
        user = acc.getID();
        userid_lbl.setText(String.valueOf(user));
        // show in the now playing list 
        DatabaseQueries db = new DatabaseQueries();
        npDataTable = db.displayNowPlayingList();
        if(npDataTable!=null) // incase the user doesn't have any tracks in their now playing list
        {
            track_column.setCellValueFactory(new PropertyValueFactory<>("trackName"));
            npid_column.setCellValueFactory(new PropertyValueFactory<>("trackid"));
            nowPlayingBox.setItems(npDataTable);
            // get an arraylist to play the songs
            try 
            {
                nowPlayingList = db.getNowPlayingList(); // retrieves the now playing list 
            }
            catch (SQLException ex) 
            {
            Logger.getLogger(TrackPlayerController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    @FXML
    private void displaySeconds(MouseEvent event) {
        slider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (slider.isValueChanging()) {
// multiply duration by percentage calculated by slider position
                    if (duration != null) {
                        currentTrack.seek(duration.multiply(slider.getValue() / 100.0));
                    }
                    updateValues();

                }
            }
        });

        currentTrack.currentTimeProperty().addListener(new ChangeListener() {

            public void changed(ObservableValue observable, Duration oldValue, Duration newValue) {
                updateValues();
            }

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {

            }
        });
    }

    @FXML
    private void previousPressed(MouseEvent event) {
        try{
        currentTrack.stop();

        
        trackPosition -= 1;
        
         if (trackPosition <0) 
         {
            trackPosition = 0; // keeps song at the start
        }
        track = new TrackPlayerFunctionalities();
        track.setPath("src/MP3Files/" + nowPlayingList.get(trackPosition).getTrackPath());

        track.intialiseMedia();

        track.play();

        currentTrack = track.getActiveMedia();
        currentTrack.currentTimeProperty().addListener((Observable ov) -> {
            updateValues();
        });

        currentTrack.setOnReady(() -> {
            duration = currentTrack.getMedia().getDuration();
            updateValues();
        });
        song_lbl.setText(nowPlayingList.get(trackPosition).getTrackName());

    }
        catch(Exception e){
      new AlertBox().display("Error","Add a track then you can play");  
    }
    }

    @FXML
    private void playPressed(MouseEvent event) 
    {   
        try{
            
            try 
            {
                nowPlayingList = db.getNowPlayingList();
            } 
            catch (SQLException ex) 
            {
                Logger.getLogger(TrackPlayerController.class.getName()).log(Level.SEVERE, null, ex);
            }
               
        setSelectedItemData1();
        ObservableList<PresentTableResults> trackPressed = nowPlayingBox.getSelectionModel().getSelectedItems();  
        try
        {
          nowPlayingList = db.getNowPlayingList();
        } catch (SQLException ex) {
            Logger.getLogger(TrackPlayerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        track = new TrackPlayerFunctionalities();

        if(trackPressed.isEmpty()==false)
        {
            if (track.isStarted() == true)
            {
                track.stop();
                track.setStarted(false);
            }
            trackPosition= nowPlayingBox.getSelectionModel().getSelectedIndex(); 
        }
        
        track.setPath("src/MP3Files/" + nowPlayingList.get(trackPosition).getTrackPath());
        //System.out.println("../" + nowPlayingList.get(trackPosition).getTrackPath()); 
        
        if (track.isStarted() == false)
        {
            track.intialiseMedia();
        }

        track.play();

        currentTrack = track.getActiveMedia();
        currentTrack.currentTimeProperty().addListener((Observable ov) -> {
            updateValues();
        });

        currentTrack.setOnReady(() -> {
            duration = currentTrack.getMedia().getDuration();
            updateValues();
        });
        song_lbl.setText(nowPlayingList.get(trackPosition).getTrackName()); 
    }
    catch(Exception e){
      new AlertBox().display("Error","Add a track then you can play");  
    }
    }
    @FXML
    private void pausePressed(MouseEvent event) {
        try{
            currentTrack.pause();
        }
        catch(Exception e){
          new AlertBox().display("Error","Add a track then you can play");  
        }
    }


    @FXML
    private void nextPressed(MouseEvent event) {
        try{
        if (trackPosition  >= nowPlayingList.size()-1) {
            trackPosition = -1; // goes to the start so starts at -1 so it can be incremented to start at 0  
        }
        currentTrack.stop(); // stops the current track playing 
        trackPosition += 1;// increments the track position
        
        track = new TrackPlayerFunctionalities();
        track.setPath("MP3" + nowPlayingList.get(trackPosition).getTrackPath());// sets the track path 
        track.intialiseMedia();
        track.play();

        currentTrack = track.getActiveMedia();
        currentTrack.currentTimeProperty().addListener((Observable ov) -> {
            updateValues();
        });

        currentTrack.setOnReady(() -> {
            duration = currentTrack.getMedia().getDuration();
            updateValues();
        });
        song_lbl.setText(nowPlayingList.get(trackPosition).getTrackName());
        
    }
        catch(Exception e){
      new AlertBox().display("Error","Add a track then you can play");  
    }
    }

    @FXML
    private void submitPressed(MouseEvent event) throws SQLException 
    {
        
        if(search_tbox.getText().isEmpty()==false)
        {
        DatabaseQueries db = new DatabaseQueries();
        if (song_rbtn.isSelected() == true) 
        {
            this.searchbox = search_tbox.getText(); // returning what was typed in the search  box to be later retrieved by getTypedValue method

            DataTable = db.getBySong();
            allData = DataTable;

            trackName_column.setCellValueFactory(new PropertyValueFactory<>("TrackName"));
            genre_column.setCellValueFactory(new PropertyValueFactory<>("trackGenre"));
            length_column.setCellValueFactory(new PropertyValueFactory<>("tracklength"));
            artist_column.setCellValueFactory(new PropertyValueFactory<>("trackartist"));
            searchResult_table.setItems(DataTable);
            if(DataTable.isEmpty()==true)
            {
                new AlertBox().display("Error","Sorry we don't have record of this track");
            }
            
        } else if (artist_rbtn.isSelected() == true) {
            this.searchbox = search_tbox.getText();

            DataTable = db.getByArtist();
            allData = DataTable;

            trackName_column.setCellValueFactory(new PropertyValueFactory<>("TrackName"));
            genre_column.setCellValueFactory(new PropertyValueFactory<>("trackGenre"));
            length_column.setCellValueFactory(new PropertyValueFactory<>("tracklength"));
            artist_column.setCellValueFactory(new PropertyValueFactory<>("trackartist"));
            searchResult_table.setItems(DataTable);
             if(DataTable.isEmpty()==true)
            {
                new AlertBox().display("Error","Sorry we don't have record of this track");
            }
        } else if (genre_rbtn.isSelected() == true) {
            this.searchbox = search_tbox.getText();

            DataTable = db.getByGenre();
            allData = DataTable;

            trackName_column.setCellValueFactory(new PropertyValueFactory<>("TrackName"));
            genre_column.setCellValueFactory(new PropertyValueFactory<>("trackGenre"));
            length_column.setCellValueFactory(new PropertyValueFactory<>("tracklength"));
            artist_column.setCellValueFactory(new PropertyValueFactory<>("trackartist"));
            searchResult_table.setItems(DataTable);
             if(DataTable.isEmpty()==true)
            {
                new AlertBox().display("Error","Sorry we don't have record of this track");
            }
        }
        }
        else
        {
            new AlertBox().display("Error","Please enter a value"); 
        }
       }
      
    

    @FXML
    private void addbtnPressed(MouseEvent event) {
        setSelectedItemData1();
        if(selectedItemData.isEmpty()==true)
        {
            new AlertBox().display("Error","Please select a track to add");
        }
         
        else
        {
            db.addSingleTrack();
            addedtoPlaylist.setText("Track added check NowPlayingList");
       DatabaseQueries db = new DatabaseQueries();
        npDataTable = db.displayNowPlayingList();
        if(npDataTable!=null) // incase the user doesn't have any tracks in their now playing list
        {
            track_column.setCellValueFactory(new PropertyValueFactory<>("trackName"));
            npid_column.setCellValueFactory(new PropertyValueFactory<>("trackid"));
            nowPlayingBox.setItems(npDataTable);
            // get an arraylist to play the songs
            try 
            {
                nowPlayingList = db.getNowPlayingList(); // retrieves the now playing list 
            }
            catch (SQLException ex) 
            {
            Logger.getLogger(TrackPlayerController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    }

    @FXML
    private void addAllbtnPressed(MouseEvent event) 
    {

            boolean haveTracks = db.addAllTrack();
     
            if(haveTracks==true)
            {
                addedtoPlaylist.setText("Tracks added check NowPlayingList");
                try 
                {
                    nowPlayingList = db.getNowPlayingList(); // update the nowplaying list 
                } 
                catch (SQLException ex) 
                {
                    Logger.getLogger(TrackPlayerController.class.getName()).log(Level.SEVERE, null, ex);
                }
                refresh();
            }
            else
            {}
          
    }
    
    @FXML
    private void refreshNP(MouseEvent event) 
    {

            
     
            
                addedtoPlaylist.setText("Tracks added check NowPlayingList");
                try 
                {
                    nowPlayingList = db.getNowPlayingList(); // update the nowplaying list 
                } 
                catch (SQLException ex) 
                {
                    Logger.getLogger(TrackPlayerController.class.getName()).log(Level.SEVERE, null, ex);
                }
                refresh();

          
    }

    protected void updateValues() {
        if (curentTime_lbl != null && slider != null && duration != null) {
            Platform.runLater(new Runnable() {
                public void run() {
                    Duration currentTime = currentTrack.getCurrentTime();
                    curentTime_lbl.setText(formatTime(currentTime, duration));
                    slider.setDisable(duration.isUnknown());
                    if (!slider.isDisabled() && duration.greaterThan(Duration.ZERO) && !slider.isValueChanging()) {
                        slider.setValue(currentTime.divide(duration).toMillis() * 100.0);
                    }

                }
            });
        }
    }

    private static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int) floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);

        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);

            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60 - durationMinutes * 60;

            if (durationHours > 0) {
                return format("%d:%02d:%02d/%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);
            } else {
                return format("%02d:%02d/%02d:%02d", elapsedMinutes, elapsedSeconds, durationMinutes, durationSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return format("%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
            } else {
                return format("%02d:%02d", elapsedMinutes, elapsedSeconds);
            }
        }
    }

    public String getSearchBoxValue() {
        return searchbox;

    }

    public void setSelectedItemData1() 
    {
        selectedItemData = searchResult_table.getSelectionModel().getSelectedItems();
      
    }

    public ObservableList<PresentTableResults> getSelectedItemData() {
        return selectedItemData;
    }

    public ObservableList<PresentTableResults> getAllData() {
        return allData;
    }

    public ObservableList<PresentTableResults> getTableResults() {
        return DataTable;
    }

    @FXML
    private void removebtnPressed(MouseEvent event) 
    {
       
        ObservableList<PresentTableResults> isSelected = nowPlayingBox.getSelectionModel().getSelectedItems();
        if(isSelected.isEmpty()==true)
        {
            new AlertBox().display("Error","Please select a track to delete");
        }
        else 
        {
            npid = nowPlayingBox.getSelectionModel().getSelectedItem().getTrackid();
            nptrackName = nowPlayingBox.getSelectionModel().getSelectedItem().getTrackName();
            DatabaseQueries db = new DatabaseQueries();
            db.deleteItemFromNowPlaying();
            npDataTable.removeAll(npDataTable);
            refresh();
        }

    }

    public void refresh() 
    {
        if(npDataTable!=null)
        {
            npDataTable.removeAll(npDataTable);
        }
        npDataTable = db.displayNowPlayingList();
         if(npDataTable!=null) // incase the user doesn't have any tracks in their now playing list
        {
        track_column.setCellValueFactory(new PropertyValueFactory<>("trackName"));
        npid_column.setCellValueFactory(new PropertyValueFactory<>("trackid"));
      
        nowPlayingBox.setItems(npDataTable);
        
        try 
            {
                nowPlayingList = db.getNowPlayingList();
            } 
            catch (SQLException ex) 
            {
                Logger.getLogger(TrackPlayerController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
   
    
    public int getUserId() 
    {
        int userID = this.user;
        return userID;
    }

    public int getSelectedID() {
        return npid;
    }

    public String getSelectedName() 
    {
        return nptrackName;
    }
}
