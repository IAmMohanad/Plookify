/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI_Template;

import account.Account;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import player.TrackPlayerFunctionalities;

/**
 * FXML Controller class
 *
 * @author anastasiasmirnova
 */
public class IntegratedController implements Initializable {

    @FXML
    private Tab home_tab;
    @FXML
    private Tab playlist_tab;
    @FXML
    private Tab radioStation_tab;
    @FXML
    private Tab social_tab;
    @FXML
    private Tab nowPlaying_tab;
    @FXML
    private Tab settings;
    @FXML
    private Button logout_btn;

    @FXML
    public Label fxUserIdLabel;

    @FXML
    private GridPane PayPalPane;
    @FXML
    private GridPane CreditCardPane;
    @FXML
    private Label fxNames;
    @FXML
    private Label fxAccountType;
    @FXML
    private Label fxPaymentType;
    @FXML
    private RadioButton fxFree;
    @FXML
    private RadioButton fxPaid;

    @FXML
    private RadioButton fxNN;
    @FXML
    private RadioButton fxPP;
    @FXML
    private RadioButton fxCC;

    @FXML
    private Button fxChoose;
    @FXML
    private Button fxchoosePayment;
    @FXML
    private Button fxCloseButton;

    @FXML
    private RadioButton device1;
    @FXML
    private RadioButton device2;
    @FXML
    private RadioButton device3;
    @FXML
    private RadioButton device4;
    @FXML
    private RadioButton device5;

    @FXML
    private Button replaceDevice;

    @FXML
    private TextField newDevice;
    @FXML
    private Button addNewDevice;
    @FXML
    private Label lastPayment;
    @FXML
    private Button fxMakePayment;
    @FXML
    private Label fxNameDevice;

    private Account acc;
    private ResultSet rs;
    public String[] devNames;
    int[] devicesArray;

    long month = 2678400;

    public IntegratedController(Account account) throws ClassNotFoundException, SQLException {
        acc = account;
        User.setAcc(acc);
    }

    public Account getAccount() {
        return acc;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            //check for subscription
            if(acc.isSubscriptionPaid()==0)
            {
                social_tab.setDisable(true); 
                radioStation_tab.setDisable(true);
             }
            
            fxCC.setDisable(true);
            fxPP.setDisable(true);
            fxNN.setDisable(false);
            fxNN.setSelected(true);
            fxNameDevice.setVisible(false);
            disableAll();

            ResultSet result = acc.select("select count(iddevice) c from device where idDevice_User='" + acc.getID() + "'");
            if (result.getInt("c") == 5) {
                addNewDevice.setDisable(true);
            }
            
                        acc.closeStmt2();

            //  String.valueOf(acc.getID())
//            fxUserIdLabelNP.setText(String.valueOf(acc.getID()));
            //          fxUserIdLabelPL.setText(String.valueOf(acc.getID()));
            //        fxUserIdLabelRS.setText(String.valueOf(acc.getID()));
            //      fxUserIdLabelSO.setText(String.valueOf(acc.getID()));
            fxNames.setText(acc.getFirstName() + " " + acc.getLastName());

            if (acc.getLastPayment() != 0) {
                String d = formatDate(acc.getLastPayment());
                lastPayment.setText(d);
                long now = System.currentTimeMillis() / 1000L;
                if (now - acc.getLastPayment() > month) {
                    Stage dialogStage = new Stage();
                    dialogStage.initModality(Modality.APPLICATION_MODAL);

                    Button but = new Button("ok");
                    but.setOnAction((ActionEvent ev) -> {

                        dialogStage.close();
                        try {
                            updateAccount();
                        } catch (SQLException ex) {
                            Logger.getLogger(IntegratedController.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    });
                    dialogStage.setScene(new Scene(VBoxBuilder.create().
                            children(new Text("You have to make new payment to continue using paid services. You are now using free subscription."), but).
                            alignment(Pos.CENTER).padding(new Insets(5)).build()));
                    dialogStage.showAndWait();

                }
            }
            else{
            fxMakePayment.setDisable(true);
            }

            if (acc.isSubscriptionPaid() == 1) {
                fxAccountType.setText("Paid");

                fxMakePayment.setDisable(false);

            } else {
                fxAccountType.setText("Free");
                fxMakePayment.setDisable(true);
            }

            switch (acc.getPaymentType()) {
                case 2:
                    fxPaymentType.setText("PayPal");
                    break;
                case 1:
                    fxPaymentType.setText("Credit Card");
                    break;
                default:
                    fxPaymentType.setText("None");
                    break;
            }

            devicesArray = acc.getDeviceId();

            rs = acc.select("SELECT deviceType FROM device WHERE idDevice_User = '" + acc.getID() + "'");
            if (rs != null) {
                int i = 0;
                while (rs.next()) {
                    System.out.println(rs.getString(1));
                    ToggleGroup tg = device1.getToggleGroup();
                    ObservableList<Toggle> toggles = tg.getToggles();
                    RadioButton rad = (RadioButton) toggles.get(i);
                    rad.setText(rs.getString(1));
                    i++;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(IntegratedController.class.getName()).log(Level.SEVERE, null, ex);
        } 

    }

    public void updateAccount() throws SQLException {

        acc.setSubscriptionType(0);
        fxAccountType.setText("Free");
        acc.setPaymentType(3);
        fxPaymentType.setText("No payment");

    }

    @FXML
    public void updateSubPay(ActionEvent e) throws SQLException {

        if (fxCC.isSelected()) {
            acc.setPaymentType(1);
            fxPaymentType.setText("Credit Card");
        } else if (fxPP.isSelected()) {
            acc.setPaymentType(2);
            fxPaymentType.setText("PayPal");
        } else {
            acc.setPaymentType(3);
            fxPaymentType.setText("No payment");
        }

        if (fxFree.isSelected()) {
            acc.setSubscriptionType(0);
            fxAccountType.setText("Free");
            fxMakePayment.setDisable(true);

        } else if (fxPaid.isSelected()) {
            acc.setSubscriptionType(1);
            fxAccountType.setText("Paid");
            fxMakePayment.setDisable(false);

        }
        updatePayment();
        //update subscription tabs.. enable
         if(acc.isSubscriptionPaid()==0)
        {
            social_tab.setDisable(true);
            radioStation_tab.setDisable(true);
        }
          else
          {
              radioStation_tab.setDisable(false);
              social_tab.setDisable(false);
          }
    }

    @FXML
    public void addDevic(ActionEvent e) throws SQLException, ClassNotFoundException {

        String nd = newDevice.getText();
        if (validate(nd)) {
            fxNameDevice.setVisible(false);

            if (device1.isSelected() && device1.getText().equals("-")) {
                acc.addDevice(nd, 0);
                device1.setText(nd);
            } else if (device2.isSelected() && device2.getText().equals("-")) {
                acc.addDevice(nd, 1);
                device2.setText(nd);

            } else if (device3.isSelected() && device3.getText().equals("-")) {
                acc.addDevice(nd, 2);
                device3.setText(nd);

            } else if (device4.isSelected() && device4.getText().equals("-")) {
                acc.addDevice(nd, 3);
                device4.setText(nd);

            } else if (device5.isSelected() && device5.getText().equals("-")) {
                acc.addDevice(nd, 4);
                device5.setText(nd);
            } else {
                fxNameDevice.setText("Select empty slot.");
                fxNameDevice.setVisible(true);
            }
        } else {
            fxNameDevice.setText("Please name the device.");
            fxNameDevice.setVisible(true);

        }

    }

    public boolean validate(String s) {
        if (s.equals(null)) {
            return false;
        } else if (s.length() == 0) {
            return false;
        } else {
            return true;
        }
    }

    @FXML
    public void repDev(ActionEvent e) throws SQLException, ClassNotFoundException {
        int count = 0;
        boolean success = true;
        for (int i = 0; i < acc.getDeviceId().length; i++) {
            if (acc.getDevId(i) != 0) {
                String nd = newDevice.getText();

                if (device1.isSelected()) {
                    success = acc.replaceDevice(nd, 0);
                    if (success) {
                        device1.setText(nd);
                    }
                } else if (device2.isSelected()) {
                    success = acc.replaceDevice(nd, 1);
                    if (success) {
                        device2.setText(nd);
                    }
                } else if (device3.isSelected()) {
                    success = acc.replaceDevice(nd, 2);
                    if (success) {
                        device3.setText(nd);
                    }
                } else if (device4.isSelected()) {
                    success = acc.replaceDevice(nd, 3);
                    if (success) {
                        device4.setText(nd);
                    }
                } else if (device5.isSelected()) {
                    success = acc.replaceDevice(nd, 4);
                    if (success) {
                        device5.setText(nd);
                    }
                }

            } else {
                count++;
            }

        }
        if (count > 0) {
            fxNameDevice.setText("First, add a device.");
            fxNameDevice.setVisible(true);
        }
        if (!success) {

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Button but = new Button("ok");
            but.setOnAction((ActionEvent event) -> {
                dialogStage.close();
            });
            dialogStage.setScene(new Scene(VBoxBuilder.create().
                    children(new Text("You can replace device once a month."), but).
                    alignment(Pos.CENTER).padding(new Insets(5)).build()));
            dialogStage.show();

        }

    }
    @FXML
    
   public void sure(ActionEvent e){ 
       Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Button butYES = new Button("yes");
            Button butNO = new Button("no");
            butYES.setOnAction((ActionEvent event) -> {
                    dialogStage.close();
           try {
               closeAccount(e);
           } catch (ClassNotFoundException | SQLException | IOException ex) {
               Logger.getLogger(IntegratedController.class.getName()).log(Level.SEVERE, null, ex);
           }
            });
            butNO.setOnAction((ActionEvent event) -> {
                    dialogStage.close();
          
            });
            dialogStage.setScene(new Scene(VBoxBuilder.create().
                    children(new Text("Are you sure you want to close your account? This is final."), butYES, butNO).
                    alignment(Pos.CENTER).padding(new Insets(5)).build()));
            dialogStage.show();
    }
    @FXML
    public void closeAccount(ActionEvent e) throws ClassNotFoundException, SQLException, IOException {
        acc.closeAccount();
        openWelcome(e);
    }

    @FXML
    public void logout(ActionEvent e) throws SQLException, IOException {
        openWelcome(e);
    }

    public void openWelcome(ActionEvent event) throws SQLException, IOException {
   
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Welcome.fxml"));

        Parent setView_parent = loader.load();
        Scene setView_scene = new Scene(setView_parent);
        Stage setView_Stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        URL url = this.getClass().getResource("style.css");
        if (url == null) {
            System.out.println("Resource not found. Aborting.");
            System.exit(-1);
        }
        String css = url.toExternalForm();
        setView_scene.getStylesheets().add(css);
        setView_Stage.setScene(setView_scene);
        setView_Stage.setTitle("Welcome");
        setView_Stage.show();
        new TrackPlayerFunctionalities().stop();
    }

    @FXML
    public void makePayment() throws SQLException {
        if (acc.isSubscriptionPaid() == 1) {
            long l = System.currentTimeMillis() / 1000L;
            String d = formatDate(l);
            lastPayment.setText(d);
            acc.update("UPDATE sub SET subDate='" + l + "' WHERE iduserSub='" + acc.getID() + "'");
        }
    }

    public void updatePayment() throws SQLException {
        if (acc.isSubscriptionPaid() == 1) {
            long l = System.currentTimeMillis() / 1000L;
            String d = formatDate(l);
            lastPayment.setText(d);
            if (acc.getLastPayment()==0){
            acc.setLastPayment(l);}
            else{
            
            acc.update("UPDATE sub SET subDate='" + l + "' WHERE iduserSub='" + acc.getID() + "'");
            }
        }
    }

    @FXML
    public void disableAll() {
        PayPalPane.setDisable(true);
        CreditCardPane.setDisable(true);
    }

    @FXML
    public void disablePayPal() {
        PayPalPane.setDisable(true);
        CreditCardPane.setDisable(false);

    }

    @FXML
    public void disableCreditCard() {
        CreditCardPane.setDisable(true);
        PayPalPane.setDisable(false);

    }

    @FXML
    public void disablePaid() {
        fxCC.setDisable(true);
        fxPP.setDisable(true);
        fxNN.setDisable(false);
        fxNN.setSelected(true);
        disableAll();

    }

    @FXML
    public void disableFree() {
        fxCC.setDisable(false);
        fxCC.setSelected(true);
        disablePayPal();
        fxPP.setDisable(false);
        fxNN.setDisable(true);

    }

    public String formatDate(long d) {
        SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy");
        String date = sdf.format(d * 1000L);
        return date;
    }


}
