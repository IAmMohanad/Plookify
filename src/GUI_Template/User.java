/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI_Template;

import account.Account;
import player.TrackPlayerController;

/**
 *
 * @author anastasiasmirnova
 */
public class User {
    private static Account acc;
    private static TrackPlayerController tpc;
    
    public static void setAcc(Account account){
    acc=account;
    }
    
    public static Account getAcc(){
    return acc;
    }
    
    public static TrackPlayerController getTPC(){
        return tpc;
    }
   
}
