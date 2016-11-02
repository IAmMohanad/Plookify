/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package social;

import GUI_Template.User;
import account.Account;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;

/**
 * FXML Controller class
 *
 * @author Mohanad Al Sayegh
 */
public class SocialManagerController implements Initializable {

    @FXML
    private Button logout_btn;
    @FXML
    private ListView<?> friendsList;
    @FXML
    private ListView<?> playList;
    @FXML
    private ListView<?> songList;
    @FXML
    private Tab nowPlaying_tab1;
    @FXML
    private Button logout_btn1;
    @FXML
    private CheckBox discoverable;
    @FXML
    private Button addFriend;
    @FXML
    private Button deleteFriend;
    @FXML
    private Button updateTable;
    @FXML
    private TextField addFriendText;
    @FXML
    private Button updateFriendRequest;
    @FXML
    private TableView friendsTable;
    @FXML
    private ListView<?> friendRequest;
    @FXML
    private TableColumn IDCol;
    @FXML
    private TableColumn firstNameCol;
    @FXML
    private TableColumn lastNameCol;
    @FXML
    private Label fxUserIdLabel;
    /**
     * Initializes the controller class.
     */
    int user;
    Database instance;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        //ObservableList items =FXCollections.observableArrayList (friendName);
        //friendsList.setItems(items);

        Account acc = User.getAcc();
        user = Integer.parseInt(String.valueOf(acc.getID()));

        instance = new Database();
        //instance.connect();

        //initialize default values for
        System.out.println("LOGGED IN USER IS: " + user);
        discoverable.setAllowIndeterminate(false);
        friendRequest.setPlaceholder(new Label("No friend requests"));
        friendsTable.setPlaceholder(new Label("To update, click 'update friends table'"));
        playList.setPlaceholder(new Label("No playlists"));
        songList.setPlaceholder(new Label("No Songs"));
        //This sets the column names to the named value.
        IDCol.setCellValueFactory(new PropertyValueFactory<>("ID"));
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        addFriendText.setPromptText("Enter friend email here");

       
        if (instance.isDiscoverable(user) == true) {
            discoverable.setSelected(true);
        } else {
            discoverable.setSelected(false);
        }

        addFriend.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String friendEmail = addFriendText.getText();
                if ((!friendEmail.equals(null)) && (friendEmail.trim().length() > 0)) {//makes sure there is no null value & there is at least 1 character in addFriend textfield
                    // int friendID = Integer.parseInt(friend);
                    instance.addFriend(friendEmail, user);
                    updateFTable();
                }
                
            }
        });

        deleteFriend.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (friendsTable.getSelectionModel().getSelectedItem() != null) {
                    String ID = String.valueOf(((Friends) friendsTable.getSelectionModel().getSelectedItem()).getID());
                    System.out.println(ID);
                    int IDToRemove = Integer.parseInt(ID);
                    instance.deleteFriend(IDToRemove, user); 
                    updateFTable();
                }
                
            }
        });

        discoverable.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                boolean isChecked = discoverable.isSelected();
                instance.setDiscoverable(isChecked, user);
            }
        });

        //updates the friends table
        updateTable.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                updateFTable();
            }
        });

        //updates the friend requests table
        updateFriendRequest.setOnAction((ActionEvent event) -> {
            //friendRequest
            updateRTable();
        });

        friendRequest.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                 //System.out.println("ListView Selection Changed (selected: " + newValue.toString() + ")");
                //get friends details
                ArrayList<Map<String, Object>> info = (ArrayList<Map<String, Object>>) instance.getUserDetails(Integer.parseInt(newValue.toString()));
                String firstName = info.get(0).get("firstName").toString();
                String lastName = info.get(0).get("lastName").toString();
                int friendID = Integer.parseInt(newValue.toString());
                String sentence = firstName + " " + lastName + " would like to add you to their friends list. \n Accept?";
                int choice = JOptionPane.showConfirmDialog(null, sentence, "Friend Request", JOptionPane.YES_NO_OPTION);
                //returns 1 if NO is selected, 0 if YES is selected else -1
                System.out.println(choice);
                if (choice == 1) {                                //accept when 1 is returned
                    instance.updateFriendStatus(user, friendID, 0);//TODO replace 1 with userID
                   // friendRequest.getSelectionModel().clearSelection();
                } else if (choice == 0) {                           //decline when 0 is returned
                    instance.updateFriendStatus(user, friendID, 1);//
                    //friendRequest.getSelectionModel().clearSelection();
                } else {
                  //  friendRequest.getSelectionModel().clearSelection();
                    JOptionPane.showMessageDialog(null, "No update");
                }
            }

        });

        /**
         * *** This piece of code is used to update the play list List every
         * time a new friend is clicked on
         */
        friendsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            //  playList.getSelectionModel().clearSelection();
            playList.getItems().clear();
            songList.getItems().clear();
            if (newValue != null) {
                String Id = String.valueOf(((Friends) friendsTable.getSelectionModel().getSelectedItem()).getID());//what friend is clicked on in friends table
                int userID = Integer.parseInt(Id);
                System.out.println("User with ID " + userID + " clicked on");

                //gets selected playlist info - depending on which user is clicked on
                ArrayList playListInfo = instance.getPlayListInfo(userID);
                ArrayList playListName = new ArrayList<>();
                ArrayList playListID = new ArrayList<>();

                playListName = (ArrayList) playListInfo.get(0);
                playListID = (ArrayList) playListInfo.get(1);
                //add playlist names to the listview
                ObservableList items = FXCollections.observableArrayList(playListName);
                playList.setItems(items);//add playList names to playList listview 
            }
        });

        /**
         * *** This piece of code is used to update the track list whenever a
         * new play list is clicked on ****
         */
        playList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (instance.userHasPaidSubscription(user) == 1) {//TODO change 1 to the logged in userID!////////////////////////////////////////////////////////////////////!////////////////////////////////////////////////////////////////////
                    String selectedPlayListName = newValue.toString(); //this is the selected playlist

                    int selectedPlayListID = instance.getPlayListIdFromName(selectedPlayListName);

                    ArrayList trackInfoList = instance.getTrackInfo(selectedPlayListID);
                         //trackInfoList HAS .get(0) which is the track name
                    //trackInfoList HAS .get(1) which is the track ID

                    ArrayList trackNameList = new ArrayList<>();
                    ArrayList trackIdList = new ArrayList<>();

                    trackNameList = (ArrayList) trackInfoList.get(0);
                    trackIdList = (ArrayList) trackInfoList.get(1);
                    //add the track names to the listview
                    ObservableList items = FXCollections.observableArrayList(trackNameList);
                    songList.setItems(items);
                } else {
                    JOptionPane.showMessageDialog(null, "You must be subscribed to see your friends tracks");
                }
            }
        });
        
        updateFTable();
        updateRTable();
    }//END INITIALISE

    //called when friendsList is loaded.
    public ObservableList getFriends(List IDList, List firstNameList, List lastNameList) {//Used to update friendsTable
        ObservableList friendsList = FXCollections.observableArrayList();

        //puts each friends details into a observible list (friendsList)
        for (int i = 0; i < IDList.size(); i++) {
            //  int ID = IDList.get(i);
            String firstName, lastName;
            friendsList.add(new Friends(Integer.parseInt(IDList.get(i).toString()), firstNameList.get(i).toString(), lastNameList.get(i).toString()));
        }
        return friendsList;
    }
    
    public void updateFTable(){
        playList.getItems().clear();
                songList.getItems().clear();

                List IDList = new ArrayList<>();
                IDList = instance.getFriends(user);
                if (IDList != null) {
                    ObservableList IDItems = FXCollections.observableArrayList(IDList);

                    List firstNameList = new ArrayList<>();
                    List lastNameList = new ArrayList<>();

                    for (int i = 0; i < IDList.size(); i++) {
                        ArrayList<Map<String, Object>> info = (ArrayList<Map<String, Object>>) instance.getUserDetails((int) IDList.get(i));
                        String firstName = info.get(0).get("firstName").toString();
                        firstNameList.add(firstName);
                        String lastName = info.get(0).get("lastName").toString();
                        lastNameList.add(lastName);
                    }
                    ObservableList friendsList = getFriends(IDList, firstNameList, lastNameList); //use this to populate table
                    friendsTable.setItems(friendsList);
                }
            
    }

    private void updateRTable() {
            List list = instance.loadFriendRequests(user);
            System.out.println("Done");
            ObservableList friendsList = FXCollections.observableArrayList();
            System.out.println("Done 2");
            for (int i = 0; i < list.size(); i++) {
                friendsList.add(list.get(i));
            }
            System.out.println("Friends list is:" + list);
            System.out.println("Done 3");
            friendRequest.setItems(friendsList);
    }
}//END CLASS
