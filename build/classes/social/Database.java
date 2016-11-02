/*
Author: Mohanad Al Sayegh
 */
package social;

import GUI_Template.DBConnection;
import java.sql.*;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

public class Database {

    PreparedStatement stmt = null;
    private final static Connection c = DBConnection.getConnection();

    public Database() {
        System.out.println("database instance created");
    }

    /*public static void closeInstance() throws SQLException {
     if (c.isValid(0)) {
     c.close();
     System.out.println("social conn close");
     }
     }*/
    public void deleteFriend(int friendID, int userID) {
        try {
            String sql = "DELETE FROM friendsList "
                    + "WHERE userID = '" + userID + "' AND friendID = '" + friendID + "' ";
            stmt = c.prepareStatement(sql);
            stmt.executeUpdate();
            sql = "DELETE FROM friendsList "
                    + "WHERE userID = '" + friendID + "' AND friendID = '" + userID + "' ";
            stmt = c.prepareStatement(sql);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "User with ID of: " + friendID + " deleted.");
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": deleteFriend: " + e.getMessage());
        }
    }

    public int getUserID(String email) {
        int userID = -1;
        ResultSet rs = null;
        try {
            String sql = "SELECT iduser FROM user "
                    + "WHERE email = '" + email + "' ";
            stmt = c.prepareStatement(sql);
            rs = stmt.executeQuery();
            userID = rs.getInt("iduser");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": getUserID: " + e.getMessage());
        }
        return userID;
    }
    //////////////////////////////////////////////////////////////////////////////////////////

    public int userHasPaidSubscription(int userID) {
        int subscribed = -1;
        ResultSet info;
        try {
            String sql = "SELECT * FROM user "
                    + "WHERE iduser = '" + userID + "' ";
            stmt = c.prepareStatement(sql);
            info = stmt.executeQuery();
            if (info.next()) {
                // System.out.println("users subscription is: " +info.getInt("subscriptionType"));
                subscribed = info.getInt("subscriptionType");
            }
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + "userHasPaidSubscription:  " + e.getMessage());
        }
        // System.out.println("subscribed is: " + subscribed);
        return subscribed;
    }

    public boolean isDiscoverable(int userID) {
        int discoverable = 0;
        try {
            String sql = "SELECT visibility FROM user "
                    + "WHERE iduser = '" + userID + "'";
            stmt = c.prepareStatement(sql);
            ResultSet info = stmt.executeQuery();
            discoverable = info.getInt("visibility");
            //System.out.println("VISIBILITY IS: "+ discoverable);
            stmt.close();
            if (discoverable == 1) {
                return true;
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": isDiscoverable: " + e.getMessage());
        }
        return false;
    }
    /*public boolean isDiscoverable(int userID) {
        int discoverable = 0;
        try {
            String sql = "SELECT visibility FROM user "
                    + "WHERE iduser = '" + userID + "'";
            stmt = c.prepareStatement(sql);
            ResultSet info = stmt.executeQuery();
            discoverable = info.getInt("visibility");
            //System.out.println("VISIBILITY IS: "+ discoverable);
            stmt.close();
      if(discoverable==1){
                return true;
            }
     } catch(Exception e){
         System.err.println( e.getClass().getName() + ": isDiscoverable: " + e.getMessage() );
        }
        return false;
    }*/

    public List getUserDetails(int userID) {
        List<Map<String, Object>> resultList = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT * FROM user "
                    + " WHERE iduser = '" + userID + "' ";
            stmt = c.prepareStatement(sql);
            rs = stmt.executeQuery();

            resultList = new ArrayList<Map<String, Object>>();
            Map<String, Object> row = null;

            ResultSetMetaData metaData = rs.getMetaData();
            Integer columnCount = metaData.getColumnCount();

            while (rs.next()) {
                row = new HashMap<String, Object>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                resultList.add(row);
            }
            stmt.close();

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + "getUserDetails: " + e.getMessage());
        }
        return resultList;
    }

    public void addFriend(String email, int userID) {
        int friendHasPaidSubscription;
        int userHasPaidSubscription;

        int friendID = getUserID(email);

        //Check if user has paid subscription
        userHasPaidSubscription = userHasPaidSubscription(userID);
        if (userHasPaidSubscription == 1) {
            //Check if friend has paid subscription
            friendHasPaidSubscription = userHasPaidSubscription(friendID);
            if ((friendHasPaidSubscription == 1) || (friendHasPaidSubscription == 0)) {//Makes sure user exists
                if (friendHasPaidSubscription == 1) {
                    //check if friend is discoverable
                    if (isDiscoverable(friendID)) {
                        //Add friend to friend list
                        try {
                            //0 means NO, 1 YES, 2 PENDING, 3 means REQUESTED
                            String sql = "INSERT OR REPLACE INTO friendsList ('userID', 'friendID', status) "
                                    + "VALUES ('" + userID + "', '" + friendID + "', '2') ";
                            stmt = c.prepareStatement(sql);
                            stmt.executeUpdate();
                            //***********IF ERROR DELETE THIS PART************//
                            sql = "INSERT OR REPLACE INTO friendsList ('userID', 'friendID', status) "
                                    + "VALUES ('" + friendID + "', '" + userID + "', '3') ";
                            stmt = c.prepareStatement(sql);
                            stmt.executeUpdate();
                            //***********IF ERROR DELETE THIS PART************//
                            stmt.close();
                            //show confirmation message
                            ArrayList<Map<String, Object>> info = (ArrayList<Map<String, Object>>) getUserDetails(friendID);
                            String firstName = info.get(0).get("firstName").toString();
                            String lastName = info.get(0).get("lastName").toString();
                            JOptionPane.showMessageDialog(null, firstName + " " + lastName + " with ID of " + friendID + " has been sent a friend request. ");
                            //      c.close();
                        } catch (Exception e) {

                            System.err.println(e.getClass().getName() + ": " + "addFriend: " + e.getMessage());
                        }
                    } else {//friend is not discoverable
                        JOptionPane.showMessageDialog(null, "User has set their profile to private!");
                    }
                } else {//friend does not have a paid subscirption
                    JOptionPane.showMessageDialog(null, "User does not have paid subscription!");
                }
            } else {
                JOptionPane.showMessageDialog(null, "User does not exist!");
            }
        } else {//user does not have a paid subscription
            JOptionPane.showMessageDialog(null, "You do not have a paid subscription!");
        }
    }

    public void setDiscoverable(boolean isSelected, int userID) {
        int discoverable = -1;
        if (isSelected == true) {
            discoverable = 1;
        } else {
            discoverable = 0;
        }
        try {
            String sql = "UPDATE user "
                    + "SET visibility = '" + discoverable + "' "
                    + "WHERE iduser = '" + userID + "' ";
            stmt = c.prepareStatement(sql);
            stmt.executeUpdate();
            stmt.close();
            //       c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + "setDiscoverable: " + e.getMessage());
        }
    }

    public List getFriends(int userID) {
        List resultList = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT friendID FROM friendsList "
                    + "WHERE userID = '" + userID + "' "
                    + "AND status = '1' ";
            stmt = c.prepareStatement(sql);
            rs = stmt.executeQuery();
            resultList = new ArrayList<>();
            while (rs.next()) {
                resultList.add(rs.getInt("friendID"));
            }
            // System.out.println("the friendslist is: "+ resultList);
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + "getFriends: " + e.getMessage());
        }
        return resultList;
    }

    public List loadFriendRequests(int userID) throws IndexOutOfBoundsException {///DELETE IF ERROR
        List resultList = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT friendID FROM friendsList " +//make sure friendRequest does not show up to the person that initiated the request!
                    "WHERE userID = '" + userID + "' "
                    + "AND status = '3' ";
            stmt = c.prepareStatement(sql);
            rs = stmt.executeQuery();
            resultList = new ArrayList<>();
            while (rs.next()) {
                //System.out.println("friend added: " + rs.getInt("friendID"));//remove this after testing
                resultList.add(rs.getInt("friendID"));
            }
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + "loadFriendRequests: " + e.getMessage());
        }
        return resultList;
    }

    public void updateFriendStatus(int userID, int friendID, int status) throws IndexOutOfBoundsException {
        //  System.out.println("Entered updateFriendStatus");
        try {
            String sql = "UPDATE friendsList "
                    + "SET status = '" + status + "' "
                    + "WHERE userID = '" + userID + "' AND friendID = '" + friendID + "' ";
            stmt = c.prepareStatement(sql);
            stmt.executeUpdate();
            //  System.out.println("UPDATEFRIENDSTATUS 1");
            sql = "UPDATE friendsList "
                    + "SET status = '" + status + "' "
                    + "WHERE userID = '" + friendID + "' AND friendID = '" + userID + "' ";
            stmt = c.prepareStatement(sql);
            //  System.out.println("UPDATEFRIENDSTATUS 2");
            stmt.executeUpdate();
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + "updateFriendStatus: " + e.getMessage());
        }
    }

    public ArrayList<ArrayList> getPlayListInfo(int userID) {//Gets all playlist from a specific user.//***************************************************************************
        //  List<Map<String, Object>> resultList = null;
        ResultSet rs = null;

        ArrayList playListNameList = new ArrayList<>();
        ArrayList playListIdList = new ArrayList<>();
        ArrayList playListInfo = new ArrayList<ArrayList>();

        try {
            String sql = "SELECT idplaylist, playlistName FROM playlist "
                    + "WHERE iduser = '" + userID + "' AND playlistVisibility = '1' ";
            stmt = c.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                playListNameList.add(rs.getString("playlistName"));
                playListIdList.add(rs.getInt("idplaylist"));
            }
            playListInfo.add(playListNameList);
            playListInfo.add(playListIdList);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + "getPlayListInfo: " + e.getMessage());
        }
        return playListInfo;
    }

    public int getPlayListIdFromName(String playListName) {
        ResultSet rs = null;
        int playListID = -1;
        try {
            String sql = "SELECT idplaylist FROM playlist "
                    + "WHERE playlistName = '" + playListName + "' "; //AND playlistVisibility = '1' ";
            stmt = c.prepareStatement(sql);
            rs = stmt.executeQuery();
            playListID = rs.getInt("idplaylist");
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + "getPlayListIdFromName: " + e.getMessage());
        }
        return playListID;
    }

    public ArrayList<ArrayList> getTrackInfo(int selectedPlayListID) {
        ResultSet rs = null;

        ArrayList trackNameList = new ArrayList<>();
        ArrayList trackIdList = new ArrayList<>();

        ArrayList<ArrayList> trackInfoList = new ArrayList<>();
        try {
            String sql = "SELECT a.idtrack, a.idplaylist, b.trackName "
                    + "FROM track_playlist a "
                    + "JOIN track b "
                    + "ON b.idtrack = a.idtrack ";
            stmt = c.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                if (rs.getInt("idplaylist") == selectedPlayListID) {
                    trackNameList.add(rs.getString("trackName"));
                    trackIdList.add(rs.getInt("idtrack"));
                }
            }

            trackInfoList = new ArrayList<ArrayList>();

            trackInfoList.add(trackNameList);
            trackInfoList.add(trackIdList);

            /*  for (int i = 0; i < trackNameList.size(); i++) {
             System.out.println("Track name: " + trackInfoList.get(0).get(i) + " Track ID: " + trackInfoList.get(1).get(i));
             }*/
            //  System.out.println(trackNameList);
            //  System.out.println(trackIdList);
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + "getTrackInfo: " + e.getMessage());
        }
        return trackInfoList;
    }
}//END CLASS