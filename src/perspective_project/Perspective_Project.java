/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package perspective_project;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author zarour
 */
public class Perspective_Project {
   public static String url = "jdbc:mysql://localhost:3306/bd_test?autoReconnect=true&useSSL=false";
   public static String utilisateur = "root";
   public static String motDePasse = "";
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Frame_Perspective2 FP = new Frame_Perspective2();   
        FP.setVisible(true);
        
    }
    
}
