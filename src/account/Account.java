/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package account;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author anastasiasmirnova
 */
public class Account extends DatabaseConnection {

    long month = 2678400;
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String password;
    protected String phone;
    protected String dob;
    protected String address;
    protected long dateJoined;
    protected int subscriptionPaid;
    protected int paymentType;
    protected int id;
    protected int visibility;
    private long lastPayment;
    private String paymentStatus;

    public long getLastPayment() {
        return lastPayment;
    }

    private int[] deviceId = new int[5];
    private String[] deviceName = new String[5];
    private int[] dateAdded = new int[5];
    private ResultSet rs;

    public Account(String firstName, String lastName, String email, String password, String phone, String dob, String address, long dateJoined, int subscriptionType, int paymentType) throws SQLException, ClassNotFoundException {

        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.dob = dob;
        this.address = address;
        this.dateJoined = dateJoined;
        this.subscriptionPaid = subscriptionType;

        this.paymentType = paymentType;
        this.visibility = 1;
        String insert = "INSERT INTO user (firstName, lastName, email, password, phone, dob, address, datejoined, subscriptionType, paymentType, visibility) VALUES ('" + firstName + "','" + lastName + "','" + email + "','" + password + "','" + phone + "','" + dob + "','" + address + "','" + dateJoined + "','" + subscriptionType + "','" + paymentType + "','" + visibility + "')";
        update(insert);
        setID();
        if (subscriptionPaid == 1) {
            this.lastPayment = dateJoined;
            setLastPayment(dateJoined);
        } else {
            this.lastPayment = 0;
            setLastPayment(0);
        }
        setDevices();

    }

    public Account(String email, String password) throws SQLException, ClassNotFoundException {

        ResultSet rss = select("Select * from user WHERE email='" + email + "'AND password='" + password + "'");
        this.id = rss.getInt("iduser");
        this.firstName = rss.getString("firstName");
        this.lastName = rss.getString("lastName");
        this.email = email;
        this.password = password;
        this.phone = rss.getString("phone");
        this.dob = rss.getString("dob");
        this.address = rss.getString("address");
        this.dateJoined = rss.getLong("datejoined");
        this.subscriptionPaid = rss.getInt("subscriptionType");
        this.paymentType = rss.getInt("paymentType");
        if (subscriptionPaid == 1) {
            paymentTime();
        } else {
            this.lastPayment = 0;
        }
       ResultSet rs2 = select("SELECT visibility FROM user WHERE iduser = '"+id+"'");
      
        visibility  = rs2.getInt("visibility");
        System.out.println(visibility);
        setDevices();

    }

    public Account() throws ClassNotFoundException, SQLException {
    }

    public void paymentTime() throws SQLException {

        ResultSet rsss = select("Select subDate from sub WHERE iduserSub='" + id + "'");
        if (rsss != null) {
            this.lastPayment = rsss.getInt("subDate");
        }
    }

    public void setLastPayment(long lastPayment) throws SQLException {
        this.lastPayment = lastPayment;
        update("INSERT OR REPLACE INTO sub (subDate, iduserSub) VALUES ('" + lastPayment + "','" + id + "')");
    }

    public void updateLastPayment(long lastPayment) throws SQLException {
        this.lastPayment = lastPayment;
    }

    public void setPaymentDate() {

    }

    public int getID() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public long getDateJoined() {
        return dateJoined;
    }

    public int isSubscriptionPaid() {
        return subscriptionPaid;
    }

    public int getPaymentType() {
        return paymentType;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setID() throws ClassNotFoundException, SQLException {
        ResultSet as = select("SELECT * FROM user WHERE iduser=(Select max(iduser) from user)");
        this.id = as.getInt("iduser");
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public void setSubscriptionType(int subscriptionType) throws SQLException {
        update("UPDATE user SET subscriptionType = '" + subscriptionType + "' WHERE iduser = '" + id + "'");
        this.subscriptionPaid = subscriptionType;
    }

    public void setPaymentType(int paymentType) throws SQLException {
        update("UPDATE user SET paymentType = '" + paymentType + "' WHERE iduser = '" + id + "'");
        this.paymentType = paymentType;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String pay) throws SQLException {
        update("UPDATE sub SET paymentStatus = '" + pay + "' WHERE iduserSub = '" + id + "'");
        this.paymentStatus = pay;
    }

    public void closeAccount() throws ClassNotFoundException, SQLException {
        update("DELETE FROM user WHERE iduser = '" + id + "'");
    }

    public int[] getDeviceId() {
        return deviceId;
    }

    public String[] getDeviceName() {
        return deviceName;
    }

    public int[] getDateAdded() {
        return dateAdded;
    }

    public void setDevices() throws SQLException {
        ResultSet rrs = select("SELECT * FROM device WHERE idDevice_User='" + id + "'");
        if (rrs != null) {
            int i = 0;
            while (rrs.next()) {
                deviceId[i] = rrs.getInt("iddevice");
                deviceName[i] = rrs.getString("deviceType");
                dateAdded[i] = rrs.getInt("dateAdded");
                i++;
            }
        }

    }

    public void setDevId(int loc) throws SQLException {
        ResultSet rss = select("SELECT * FROM device WHERE iddevice=(Select max(iddevice) from device)");
        deviceId[loc] = rss.getInt("iddevice");
    }

    public int getDevId(int loc) {
        return deviceId[loc];
    }

    public void addDevice(String devicename, int loc) throws SQLException, ClassNotFoundException {
        long dateAdd = System.currentTimeMillis() / 1000L;
        update("INSERT INTO DEVICE (deviceType, idDevice_User, dateAdded) VALUES ('" + devicename + "','" + id + "','" + dateAdd + "')");
        setDevId(loc);
    }

    public boolean replaceDevice(String devicename, int loc) throws SQLException {
        long dateAdd = System.currentTimeMillis() / 1000L;

        if (dateAdded[loc] - dateAdd > month) {

            int idD = getDevId(loc);
            update("update device SET deviceType='" + devicename + "', dateAdded ='" + dateAdd + "', idDevice_User ='" + id + "' WHERE iddevice = '" + idD + "'");

            return true;
        } else {
            return false;
        }
    }
}
