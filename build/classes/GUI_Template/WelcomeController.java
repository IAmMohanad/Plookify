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
import java.util.ResourceBundle;
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
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author anastasiasmirnova
 */
public class WelcomeController implements Initializable {

    @FXML
    private TextField fxFirstName;
    @FXML
    private TextField fxLastName;
    @FXML
    private TextField fxEmail;
    @FXML
    private PasswordField fxPassword;
    @FXML
    private TextField fxPhone;
    @FXML
    private TextField fxDOB;
    @FXML
    private TextField fxAddress;

    @FXML
    private Label valFirstName;
    @FXML
    private Label valLastName;
    @FXML
    private Label valEmail;
    @FXML
    private Label valPass;
    @FXML
    private Label valAddress;
    @FXML
    private Label valLogEm;
    @FXML
    private Label valLogPass;

    @FXML
    private GridPane PayPalPane;
    @FXML
    private GridPane CreditCardPane;

    @FXML
    private RadioButton fxFree;
    @FXML
    private RadioButton fxPaid;

    @FXML
    private RadioButton fxCC;
    @FXML
    private RadioButton fxPP;
    @FXML
    private RadioButton fxNN;

    @FXML
    private Button fxRegister;

    @FXML
    private TextField fxLoginEmail;
    @FXML
    private PasswordField fxLoginPassword;
    @FXML
    private Button fxLogin;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private String dob;
    private String address;
    private int subscriptionType;
    private int paymentType;
    private long dateJoined;

    private String loginEmail;
    private String loginPassword;

    private Account acc;
    private ResultSet loggingIn;
    long month = 2678400;
    @FXML
    private Label fxNotCorrect;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        disableAll();
        fxCC.setDisable(true);
        fxPP.setDisable(true);
        fxNN.setDisable(false);
        fxNN.setSelected(true);

    }

    @FXML
    public void register(ActionEvent event) throws IOException, SQLException, ClassNotFoundException {
        boolean AllGood = true;
        acc = new Account();
        firstName = fxFirstName.getText();
        if (!validate(firstName)) {
            valFirstName.setText("* Required");
            AllGood = false;
        } else {
            valFirstName.setText("*");
        }
        lastName = fxLastName.getText();
        if (!validate(lastName)) {
            valLastName.setText("* Required");
            AllGood = false;
        } else {
            valLastName.setText("*");
        }
        email = fxEmail.getText();

        if (!validate(email)) {
            valEmail.setText("* Required");
            AllGood = false;
            if (!checkemail(email)) {
              Stage dialogStage = new Stage();
                    dialogStage.initModality(Modality.APPLICATION_MODAL);

                    Button but = new Button("ok");
                    but.setOnAction((ActionEvent ev) -> {
                        dialogStage.close();
                    });
                    dialogStage.setScene(new Scene(VBoxBuilder.create().
                            children(new Text("This email already exists in our system. Please use another email to register."), but).
                            alignment(Pos.CENTER).padding(new Insets(5)).build()));
                    dialogStage.showAndWait();
                AllGood = false;
            }
        } else {
            valEmail.setText("*");
            if (!checkemail(email)) {
               Stage dialogStage = new Stage();
                    dialogStage.initModality(Modality.APPLICATION_MODAL);

                    Button but = new Button("ok");
                    but.setOnAction((ActionEvent ev) -> {
                        dialogStage.close();
                    });
                    dialogStage.setScene(new Scene(VBoxBuilder.create().
                            children(new Text("This email already exists in our system. Please use another email to register."), but).
                            alignment(Pos.CENTER).padding(new Insets(5)).build()));
                    dialogStage.showAndWait();
                AllGood = false;
            }
        }

        password = fxPassword.getText();
        if (!validate(password)) {
            valPass.setText("* Required");
            AllGood = false;
        } else {
            valEmail.setText("*");
        }
        phone = fxPhone.getText();
        if (!validate(phone)) {
            phone = " ";
        }
        dob = fxDOB.getText();
        if (!validate(dob)) {
            dob = " ";
        }
        address = fxAddress.getText();
        if (!validate(address)) {
            valAddress.setText("* Required");
            AllGood = false;
        } else {
            valAddress.setText("*");
        }
        dateJoined = System.currentTimeMillis() / 1000L;

        if (fxPaid.isSelected()) {
            subscriptionType = 1;//paid
            if (fxPP.isSelected()) {
                paymentType = 2;//paypal
            } else {
                paymentType = 1;//credit card
            }
        } else {
            subscriptionType = 0;//free
            paymentType = 3;//no payment
        }
        if (AllGood) {
            Account registerAcc = new Account(firstName, lastName, email, password, phone, dob, address, dateJoined, subscriptionType, paymentType);
            openSettings(registerAcc, event);
        } else {

        }
    }

    @FXML
    public void login(ActionEvent event) throws SQLException, IOException, ClassNotFoundException {
        acc = new Account();
        boolean allgood = true;
        loginEmail = fxLoginEmail.getText();
        if (!validate(loginEmail)) {
            valLogEm.setText("* Required");
            allgood = false;

        }
        loginPassword = fxLoginPassword.getText();
        if (!validate(loginPassword)) {
            valLogPass.setText("* Required");
            allgood = false;
        }

        if (allgood) {
            boolean logger = false;

            loggingIn = acc.select("SELECT email, password FROM user");
            int count = 0;
            while (loggingIn.next()) {
                String logg = loggingIn.getString("email");
                String pass = loggingIn.getString("password");
                if (loginEmail.equals(logg) && loginPassword.equals(pass)) {
                    count = 0;
                    fxNotCorrect.setVisible(false);
                    logger = true;

                } else {
                    count++;
                }
            }
            if (count > 0) {
                fxNotCorrect.setText("Enter valid details.");
                fxNotCorrect.setVisible(true);
            }
            acc.closeStmt2();
            if (logger) {
                Account acco = new Account(loginEmail, loginPassword);
                openSettings(acco, event);
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

    public void openSettings(Account a, ActionEvent e) throws IOException, ClassNotFoundException, SQLException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("GUI_Template.fxml"));
        IntegratedController controller = new IntegratedController(a);
        loader.setController(controller);
        Parent setView_parent = loader.load();
        Scene setView_scene = new Scene(setView_parent);
        URL url = this.getClass().getResource("style.css");
        if (url == null) {
            System.out.println("Resource not found. Aborting.");
            System.exit(-1);
        }
        String css = url.toExternalForm();
        setView_scene.getStylesheets().add(css);
        Stage setView_Stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        setView_Stage.setScene(setView_scene);
        setView_Stage.setTitle("Plookify");
        setView_Stage.show();
    }

    public boolean checkemail(String s) throws SQLException {

        boolean emailer = true;

        ResultSet rs = acc.select("SELECT email FROM user");
        while (rs.next()) {
            String email1 = rs.getString("email");

            if (email1.equals(s)) {
                emailer = false;
            }
        }
        return emailer;

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
}
