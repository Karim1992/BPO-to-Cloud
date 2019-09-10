/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test1;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author zarour
 */
public class Test1 {
public static String url = "jdbc:mysql://localhost:3306/bd_bpo?zeroDateTimeBehavior=convertToNull";
public static String utilisateur = "root";
public static String motDePasse = "";
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
     
        /* Connexion à la base de données */

Connection connexion = null;

try {
    connexion = DriverManager.getConnection( url, utilisateur, motDePasse );
/* Création de l'objet gérant les requêtes */
            System.out.println("exécution du bloc try");

Statement statement = connexion.createStatement();
/* Exécution d'une requête de lecture */
ResultSet resultat = statement.executeQuery( "SELECT name, confidentiality FROM Activity;" );
/* Récupération des données du résultat de la requête de lecture */
while ( resultat.next() ) {
   String s1 = resultat.getString( "name" );
   Boolean s2 = resultat.getBoolean("confidentiality" );
   System.out.println(s1+"  "+s2); 

    /* Traiter ici les valeurs récupérées. */
}

} catch ( SQLException e ) {
    /* Gérer les éventuelles erreurs ici */
                System.out.println("exception catch");

} finally {
    if ( connexion != null )
        try {
            /* Fermeture de la connexion */
            connexion.close();
            System.out.println("fermeture de la connexion");
        } catch ( SQLException ignore ) {
            /* Si une erreur survient lors de la fermeture, il suffit de l'ignorer. */
        }
}





       configuration c = new configuration();
       c.setVisible(true);
    }
    
}
