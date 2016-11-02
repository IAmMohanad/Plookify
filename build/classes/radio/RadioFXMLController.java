package radio;

import GUI_Template.User;
import account.Account;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import playlist.Playlist_controller;

/**     ****RadioFXMLController*****
 * Author: Marcel Riederer Monclou
 * Controls GUI elements in RadioFXML.
 * Controls user input.
 */

public class RadioFXMLController implements Initializable {
    
    @FXML private Button save_button;
    @FXML private Label prompt;
    @FXML private CheckBox isPublic;
    @FXML private TextField search,playlist_name;
    @FXML private ListView artist_view, playlist_view;
    
    @FXML private Tab radioStation_tab;
        
    private RadioModel radioModel;
    private PlayList visableArtist;
    private PlayList visablePlaylist;    
    
    private int user;

    @FXML
    private void onMakePlaylistAction(){
        this.visablePlaylist = new PlayList();

        if(artist_view.getSelectionModel().isEmpty()){
            this.prompt.setText("Select Artist");
        }
        else{
            int position = this.artist_view.getSelectionModel().getSelectedIndex();
            MetaData entry = this.visableArtist.get(position);
            this.visablePlaylist = this.radioModel.getByGenre( entry.getGenreName() );         
            if(visablePlaylist.getSize()==0){
                prompt.setText("Sorry, no similar musicians");
            }
            
            else{
                ObservableList<String> list = FXCollections.observableList(this.visablePlaylist.getTrackNameList());
                this.playlist_view.setItems(list);
                this.save_button.setDisable(false);
            }
        }
    }
    
    @FXML
    private void onSavePlaylistAction(){
        //setUser();
        
        if(playlist_name.getText()  == null || playlist_name.getText().trim().isEmpty()){
            this.prompt.setText("Add Playlist Name");
        }  
        else{
            String plName = this.playlist_name.getText();
            boolean visable = this.isPublic.isSelected();
            if(radioModel.addPlaylist(this.visablePlaylist,plName,visable, this.user) == true){
                this.prompt.setText( plName + " added to your playlists!");
                this.playlist_view.getItems().clear();
                this.playlist_name.clear();
                this.save_button.setDisable(true);                
            }
            else
                this.prompt.setText("Playlist Not Added - Contact admin");
        }
        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setUser();
        try {
            this.radioModel = new RadioModel();
        } catch (SQLException ex) {
            Logger.getLogger(RadioFXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        search.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            PlayList pl = radioModel.getByArtistName(newValue);
            setViewArtist(pl);
        });
        
        
        
        this.save_button.setDisable(true);
        setViewArtist(radioModel.getAll());
    }    
    
    private void setViewArtist(PlayList pl){
        this.visableArtist = pl;
        ObservableList<String> list = FXCollections.observableList(pl.getArtistList());
        this.artist_view.setItems(list);
    }
    
    private void setUser(){
        Account acc = User.getAcc();
        this.user = acc.getID();
    }
    
}