/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package account;

import GUI_Template.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *  
 * @author anastasiasmirnova
 */
public class DatabaseConnection {

    private PreparedStatement stmt1;
    private PreparedStatement stmt2;
    private final Connection c = DBConnection.getConnection();

    public void update(String command) throws SQLException {
       
        stmt1 = c.prepareStatement(command);
        stmt1.executeUpdate();
        stmt1.close();
    }

    public ResultSet select(String command) throws SQLException {
       
        stmt2 = c.prepareStatement(command);
        ResultSet rs = stmt2.executeQuery();
        return rs;
    }

    public void closeStmt2() throws SQLException {
        stmt2.close();
    }
}
