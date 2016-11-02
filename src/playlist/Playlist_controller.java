/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package playlist;

import GUI_Template.User;
import account.Account;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import player.DatabaseQueries;
import player.PresentTableResults;
import player.TrackPlayerController;


/**
 * FXML Controller class
 *
 * @author Work
 */
public class Playlist_controller implements Initializable {
    
    @FXML
    private Label fxuserID;
    @FXML
    private Tab home_tab;
    @FXML
    private Tab playlist_tab;
    @FXML
    private Button searchButton;
    @FXML
    private TextField searchField;
    @FXML
    private ListView<String> playlistView;
    @FXML
    private Button removeTrack;
    @FXML
    private Button addTrack;
    @FXML
    private RadioButton friendsButton;
    @FXML
    private RadioButton PrivateButton;
    @FXML
    private RadioButton allButton;
    @FXML
    private MenuBar optionsBar;
    @FXML
    private MenuItem createPlaylistItem;
    @FXML
    private MenuItem removePlaylistItem;
    @FXML
    private MenuItem refreshMenuItem;
    @FXML
    private Tab radioStation_tab;
    @FXML
    private Tab social_tab;
    @FXML
    private Tab nowPlaying_tab;

    //Table
    @FXML
    private TableView playlist_table;
    @FXML
    private TableColumn<PlaylistTracks, String> Name;
    @FXML
    private TableColumn<PlaylistTracks, Integer> Time;
    @FXML
    private TableColumn<PlaylistTracks, String> Artist;
    @FXML
    private TableColumn<PlaylistTracks, String> Genre;

    //other variables    
    private ObservableList<String> playlistNameItems = FXCollections.observableArrayList();
    private ObservableList<PlaylistTracks> items = FXCollections.observableArrayList();
    private final ToggleGroup group = new ToggleGroup();

    private String selectedItemName = null; //name of the selected playlist in listview
    private String selectedItemName_addNPL; // same as selectedItemName but this gets
    private String selectedTrackName = null; // name of the track selected in table view
    private String selectedTrackArtist = null; // name of the track selected in table view
    private String input;
    private String addquery3;
    private String addquery4;
    private String addquery5;
    private int idplaylist_addTrack;
    private int idtrack_addTrack;
    private int userID;

    //new instance to establish one connection
    Main conn = new Main();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //get online user ID
        Account acc = User.getAcc();
        String user = String.valueOf(acc.getID());
        fxuserID.setText(user);
        userID = Integer.parseInt(user);    
       
        //display tableview with playlist tracks
        viewPlaylistTracks(selectedItemName);

        //set tableview cells and set items
        Name.setCellValueFactory(new PropertyValueFactory("Name"));
        Time.setCellValueFactory(new PropertyValueFactory("Time"));
        Artist.setCellValueFactory(new PropertyValueFactory("Artist"));
        Genre.setCellValueFactory(new PropertyValueFactory("Genre"));
        playlist_table.setItems(items);

        //group radio buttons
        allButton.setToggleGroup(group);
        PrivateButton.setToggleGroup(group);
        friendsButton.setToggleGroup(group);
        allButton.setSelected(true);

        //listener for radio buttons
        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> ov,
                    Toggle old_toggle, Toggle new_toggle) {
                try {
                    if (allButton.isSelected()) {
                        playlistNameItems.clear();
                        //display playlist names as a list view
                        String sql = "SELECT playlistName from playlist WHERE iduser = '"+ userID +"';";
                        ResultSet rs = conn.connectDBrs(sql);
                        while (rs.next()) {
                            playlistNameItems.add(rs.getString(1));
                        }
                        playlistView.setItems(playlistNameItems);

                    } else if (PrivateButton.isSelected()) {
                        playlistNameItems.clear();
                        //display playlist names as a list view
                        String sql = "SELECT playlistName from playlist WHERE iduser = '"+ userID +"' AND playlistVisibility =0;";
                        ResultSet rs = conn.connectDBrs(sql);
                        while (rs.next()) {
                            playlistNameItems.add(rs.getString(1));
                        }
                        playlistView.setItems(playlistNameItems);
                    } else if (friendsButton.isSelected()) {
                        playlistNameItems.clear();
                        //display playlist names as a list view
                        String sql = "SELECT playlistName from playlist WHERE iduser = '"+ userID +"' AND playlistVisibility = 1;";
                        ResultSet rs = conn.connectDBrs(sql);
                        while (rs.next()) {
                            playlistNameItems.add(rs.getString(1));
                        }
                        playlistView.setItems(playlistNameItems);
                    }
                } catch (Exception e) {
                    System.err.println(e.getClass().getName() + ": addtrack - " + e.getMessage());
                    System.exit(0);
                }

            }

        });

        //display listview with playlist names
        viewPlaylistNames(playlistView);

        //add listener for listview
        //acts as refresh, everytime a user clicks on a playlist
        playlistView.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            selectedItemName = newValue;
            addTrack.setDisable(true);
            refresh();
        }); // when a playlist is selected save the name in newValue, clear the table and refresh.
    
        /*********************************************************************/
        //When you double click on a playlist, add it to the nowplaying list:
        playlistView.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent click) {
                //check for double click
                if (click.getClickCount() == 2) {
                   //if it has been double clicked, get the playlist name.
                   selectedItemName_addNPL = playlistView.getSelectionModel().getSelectedItem();
                   //System.out.println("Double clicked on " + selectedItemName_addNPL);
                   
                   try{
                   //get the tracks in that playlist
                      String query = "SELECT idplaylist FROM playlist WHERE playlistName = '" + selectedItemName_addNPL + "';";
                      ResultSet rs01 = conn.connectDBrs(query);
                      int id2 = rs01.getInt(1); //this is the id for that playlist
                      
                      
                      String query2 = "SELECT * FROM track t, track_playlist tp, track_artist ta, artist a WHERE t.idtrack=tp.idtrack AND ta.idtrack_a=tp.idtrack AND ta.idartist_t=a.idartist AND tp.idplaylist = '" + id2 + "';";
                      ResultSet rs02 = conn.connectDBrs(query2);
                   
                   //check if user has npid and get
                   String query3 = "SELECT idNP FROM NowPlayingUser WHERE idUser ="+ userID + ";";
                   ResultSet rs03 = conn.connectDBrs(query3);
                   
                   int idnp;
                   if(rs03.next()==false){
                       new DatabaseQueries().createNPID(userID);
                       String query04 = "SELECT idNP FROM NowPlayingUser WHERE idUser =" + userID + ";";
                       ResultSet rs04 = conn.connectDBrs(query04);
                       idnp = rs04.getInt(1);
                   }
                   else{
                       idnp = rs03.getInt(1);
                   }
                   
                   
                   while(rs02.next()){
                       
                       int idTrack = rs02.getInt("idtrack");
                       String trackPath = rs02.getString("trackPath");
                       String trackName = rs02.getString("trackName");
                       
                       String query4 = "INSERT INTO NowPlayingList (idNowPlaying, TrackName, TrackPath, idTrack) VALUES ('"+ idnp +"', '"+ trackName +"', '"+ trackPath +"','"+ idTrack +"' );";
                       conn.connectDB(query4);             
                   }
                   
                    //refresh nowplaying
                    
                   
                   }
                   catch (Exception e) {
                       System.err.println(e.getClass().getName() + ": doubleclick adding to nowplayling list- " + e.getMessage());
                   }
                   

                }
            }
        });

    }// close initializer

    @FXML
    private void createPlaylist(ActionEvent event) {
        try{
            //custom popup for creating a playlist
            JPanel myPanel = new JPanel(); // new jpanel
            myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS)); //set layout VERTICAL
            //
            myPanel.add(new JLabel("Enter playlist name:", SwingConstants.LEFT)); //new label aligned left
            JTextField textInput = new JTextField();// textinput
            textInput.setColumns(10);
            myPanel.add(textInput);
            //
            myPanel.add(Box.createVerticalStrut(10)); //spacer
            //
            myPanel.add(new JLabel("Choose playlist visibility:", SwingConstants.LEFT)); //new label aligned left
            DefaultComboBoxModel model = new DefaultComboBoxModel(); // new list
            model.addElement("Public"); // add to list
            model.addElement("Private");// add to list
            JComboBox comboBox = new JComboBox(model); // add list to new combo box
            myPanel.add(comboBox); // add combo box to jpanel
            //
            //JTextArea noInputMessage = new JTextArea(); //new text area
            //myPanel.add(noInputMessage); //if user does not provide input show message
                
                //get input(s) -- name of new playlist & visibility
                String name = null; //name of playlist created
                String visChosen = null; //visibility
                int result = JOptionPane.showConfirmDialog(null, myPanel,"Create a new playlist", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                   name = textInput.getText();
                   visChosen = comboBox.getSelectedItem().toString();
                   if(visChosen == "Public"){
                       visChosen = "1";
                   }
                   else if(visChosen == "Private"){
                       visChosen = "0";
                   }
                   //create new playlist sql query
                   String sql2 = "INSERT INTO playlist (iduser, playlistVisibility, playlistName) VALUES('" + userID + "','"+ Integer.parseInt(visChosen) +"','"+ name +"')";
                   conn.connectDB(sql2); //execute the query

                }
                else{
                    //user cancelled 
                   //noInputMessage.setText("All fields are required, please fill them in and try again..");  
                }                
                //Refresh the listView
                playlistView.getItems().clear();
                viewPlaylistNames(playlistView);
                playlistView.setItems(playlistNameItems);
        }
        catch (Exception e) {
            System.err.println(e.getClass().getName() + ": rename " + e.getMessage());
            System.exit(0);
        }     
    }

    public void renamePlaylist() {
        try {
            //check if a playlist is selected
            if (selectedItemName != null) {
                //then get the id from playlist where playlist name is the same as the value 
                String sql = "UPDATE playlist SET playlistName = '" + JOptionPane.showInputDialog("Enter new name.") + "' WHERE playlistName = '" + selectedItemName + "';";
                conn.connectDB(sql);
                playlistView.getItems().clear();
                viewPlaylistNames(playlistView);
                playlistView.setItems(playlistNameItems);

            } else {
                JOptionPane.showMessageDialog(null, "Please select a playlist and try again.");
            }

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": rename " + e.getMessage());
            System.exit(0);
        }

        //else if it is not selected print "please select a playlist and then try again"
    }

    public void viewPlaylistNames(ListView playlistView) {
        try {
            playlistNameItems.clear();
            //display playlist names as a list view
            String sql = "SELECT playlistName from playlist WHERE iduser = '"+ userID +"';";
            ResultSet rs = conn.connectDBrs(sql);
            while (rs.next()) {
                playlistNameItems.add(rs.getString(1));
            }
            playlistView.setItems(playlistNameItems);

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": viewplaylistname " + e.getMessage());
            System.exit(0);
        }
    }

    public void viewPlaylistTracks(String selectedItemName) {
        try {

            //if selectedItemName is != null, then get the id associated to it.
            //else dont.
            String query2 = null;
            if (selectedItemName != null) {
                String query = "SELECT idplaylist FROM playlist WHERE playlistName = '" + selectedItemName + "';";
                ResultSet rs = conn.connectDBrs(query);
                int id = rs.getInt(1); //this is the id for that playlist
                query2 = "SELECT * FROM track t, track_playlist tp, track_artist ta, artist a WHERE t.idtrack=tp.idtrack AND ta.idtrack_a=tp.idtrack AND ta.idartist_t=a.idartist AND tp.idplaylist = '" + id + "';";
            } else {
                System.out.println("Select a playlist to view tracks.");
            }

            if (query2 != null) {
                // execute the query, and get a java resultset
                ResultSet rs2 = conn.connectDBrs(query2);

                // iterate through the java resultset
                while (rs2.next()) {
                    String trackName = rs2.getString("trackName");
                    int trackLength = rs2.getInt("trackLength");
                    String artistName = rs2.getString("artistName");
                    String artistGenre = rs2.getString("artistGenre");
                    //System.out.println("1");

                    // add object to list
                    items.add(new PlaylistTracks(trackName, trackLength, artistName, artistGenre));
                }
            } else {
               //do nothing
            }

            //once items is complete, add it to the tableview
            playlist_table.setItems(items);

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": viewplaylisttracks" + e.getMessage());
            System.exit(0);
        }

    }//close viewPlaylistTracks

    public void searchForArtist() throws SQLException {
        //enable addTrack Button

        playlistView.getSelectionModel().clearSelection();
        
        String searchValue = searchField.getText(); 
        int pl = searchValue.indexOf(" ");
        if(pl<0){
        String s1 = searchValue.substring(0, 1).toUpperCase();
        String s2 = searchValue.substring(1).toLowerCase();
        searchValue = s1+s2;
        } else{
        String s1 = searchValue.substring(0, 1).toUpperCase();
        String s2 = searchValue.substring(1, pl).toLowerCase();
        String s3 = searchValue.substring(pl+1, pl+2).toUpperCase();
        String s4 = searchValue.substring(pl+2).toLowerCase();
        searchValue = s1+s2+" " +s3+s4;
        }
  
        //System.out.println(searchValue.length());
        if (searchValue.length() > 0) {
            String sql = "SELECT * FROM track t, track_artist ta, artist a WHERE a.artistName='" + searchValue + "' AND ta.idtrack_a=t.idtrack AND ta.idartist_t=a.idartist;";
            ResultSet rs = conn.connectDBrs(sql);
            ObservableList<PlaylistTracks> artistTracks = FXCollections.observableArrayList();
            while (rs.next()) {
                String trackName = rs.getString("trackName");
                int trackLength = rs.getInt("trackLength");
                String artistName = rs.getString("artistName");
                String artistGenre = rs.getString("artistGenre");
                //System.out.println("1");

                // add the rs values list as object
                artistTracks.add(new PlaylistTracks(trackName, trackLength, artistName, artistGenre));
            }

            //now clear the table and add the new items
            playlist_table.setItems(artistTracks);
            addTrack.setDisable(false);

        } else {
            JOptionPane.showMessageDialog(null, "Enter artist name and try again.");
        }
    }

    public void addTrack() {
        try{
            //get selected track info
            selectedTrackName = ((PlaylistTracks) playlist_table.getSelectionModel().getSelectedItem()).getName();
            selectedTrackArtist = ((PlaylistTracks) playlist_table.getSelectionModel().getSelectedItem()).getArtist();
                       
            String query2 = "SELECT playlistName from playlist WHERE iduser = '"+ userID +"';";
            ResultSet rs2 = conn.connectDBrs(query2);
            ObservableList<String> plNames2 = FXCollections.observableArrayList();

            int count = 0;
            while (rs2.next()) {
                plNames2.add(rs2.getString("playlistName"));
                System.out.println(rs2.getString("playlistName"));
                count++;
            }
            
            //pop up dialog box which has drop down, lists all playlist made by user
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);

            ChoiceBox cb = new ChoiceBox(plNames2);
            Button but = new Button("ok");
            but.setOnAction((ActionEvent event) -> {
                            
                            input = cb.getValue().toString();
                            try{
                            //get idtrack of the song
                            addquery3 = "SELECT idtrack FROM track t, artist a, track_artist ta WHERE a.artistName = '" + selectedTrackArtist + "' AND t.trackName = '" + selectedTrackName + "' AND ta.idtrack_a = idtrack AND ta.idartist_t = a.idartist;";
                            ResultSet rs3 = conn.connectDBrs(addquery3);
                            idtrack_addTrack = rs3.getInt(1);

                            //get idplaylist of the playlist selected
                            addquery4 = "SELECT idplaylist FROM playlist WHERE playlistName = '" + input + "';";
                            ResultSet rs4 = conn.connectDBrs(addquery4);
                            idplaylist_addTrack = rs4.getInt(1);

                            //add track to track_playlist table
                            addquery5 = "INSERT INTO track_playlist (idtrack,idplaylist) VALUES ('" + idtrack_addTrack + "', '" + idplaylist_addTrack + "');";
                            conn.connectDB(addquery5);
                            }
                            catch (Exception e) {
                                System.err.println(e.getClass().getName() + ": addTrack - but.setonAction method- - " + e.getMessage());
                                System.exit(0);
                            }
                            
            
                            dialogStage.close();

            });
            
            
            dialogStage.setScene(new Scene(VBoxBuilder.create().
                    children(cb, but).
                    alignment(Pos.CENTER).padding(new Insets(5)).build()));
            dialogStage.show();            
           
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": addtrack - " + e.getMessage());
            System.out.println("Search for an artist and then select a track first.");
            //System.exit(0);
        }

    }

    public void removeTrack() {
        try {
            selectedTrackName = ((PlaylistTracks) playlist_table.getSelectionModel().getSelectedItem()).getName();
            selectedTrackArtist = ((PlaylistTracks) playlist_table.getSelectionModel().getSelectedItem()).getArtist();
            
            //get idtrack of the song being removed
            String query = "SELECT idtrack FROM track t, artist a, track_artist ta WHERE a.artistName = '" + selectedTrackArtist + "' AND t.trackName = '" + selectedTrackName + "' AND ta.idtrack_a = idtrack AND ta.idartist_t = a.idartist;";
            ResultSet rs = conn.connectDBrs(query);
            int idtrack = rs.getInt(1);

            //get idplaylist of the playlist selected
            String query2 = "SELECT idplaylist FROM playlist WHERE playlistName = '" + selectedItemName + "';";
            ResultSet rs2 = conn.connectDBrs(query2);
            int idplaylist = rs2.getInt(1);

            //remove that track from playlist
            String query3 = "DELETE FROM track_playlist WHERE idtrack = " + idtrack + " AND idplaylist = " + idplaylist + ";";
            conn.connectDB(query3);

            //refresh the tableview
            refresh();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,"Select playlist - then track and try again");
            System.err.println(e.getClass().getName() + ": removeTrack - " + e.getMessage());
            
        }

    }

    public void refresh() {
        items.removeAll(items);
        viewPlaylistTracks(selectedItemName);
    }
    
    public void refreshPLV() { //refresh playlistView
        playlistView.getItems().clear();
        viewPlaylistNames(playlistView);
        playlistView.setItems(playlistNameItems);
    }
    
    
}
