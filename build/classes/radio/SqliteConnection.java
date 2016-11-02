/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package radio;

import GUI_Template.DBConnection;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author marce
 */
public class SqliteConnection {

    private static Connection conn = DBConnection.getConnection();

    public static Connection openConnection() throws ClassNotFoundException, SQLException {

        return conn;

    }
}
