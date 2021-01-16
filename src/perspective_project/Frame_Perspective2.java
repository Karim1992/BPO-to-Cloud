/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package perspective_project;

import perspective_project.matrices_transfer.matrix3;
import perspective_project.matrices_transfer.matrix2;
import perspective_project.matrices_transfer.matrix5;
import perspective_project.matrices_transfer.matrix4;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Vector;
import javax.sound.midi.SysexMessage;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import static perspective_project.Perspective_Project.motDePasse;
import static perspective_project.Perspective_Project.url;
import static perspective_project.Perspective_Project.utilisateur;
import perspective_project.matrices_separation.matrix2_separation;
import static perspective_project.matrices_separation.matrix2_separation.*;
import perspective_project.matrices_separation.matrix3_separation;
import static perspective_project.matrices_separation.matrix3_separation.*;
import perspective_project.matrices_separation.matrix4_separation;
import static perspective_project.matrices_separation.matrix4_separation.*;
import perspective_project.matrices_separation.matrix5_separation;
import static perspective_project.matrices_separation.matrix5_separation.*;
import perspective_project.matrices_separation.matrix9_separation;
import static perspective_project.matrices_separation.matrix9_separation.*;
import static perspective_project.matrices_transfer.matrix2.*;
import static perspective_project.matrices_transfer.matrix3.*;
import static perspective_project.matrices_transfer.matrix4.*;
import static perspective_project.matrices_transfer.matrix5.*;
import perspective_project.matrices_transfer.matrix9;
import static perspective_project.matrices_transfer.matrix9.*;
/**
 *
 * @author zarour
 */
public class Frame_Perspective2 extends javax.swing.JFrame {
    int nbr_task;
    Vector vect_ban_ofr = new Vector(); // la taille maximum du vecteur est 3 puisque dans notre cas on peut comparer 3 offres
    Vector vect_trsfr_time = new Vector(); // la taille max du vect est (Task nbr - 1) = (N-1) dans le cas où une tâche reçoit des data de toutes les autres tâche
    Object[][] tab_rank = new Object[3][3]; // tableau dans lequel on tri (selon cost) les offres autorisées pr chaque tâche
    Connection connexion = null;
    Statement statement; 
    public static DefaultTableModel model_margin_estimation, model_task_assignment;
    DefaultTableModel model_price_trsfr;
    /**
     * Creates new form Frame_Perspective2
     */
    public Frame_Perspective2() {
        initComponents();
        this.setLocation(200, 0); 
        this.setVisible(true);
        model_margin_estimation = (DefaultTableModel) jTable1.getModel();
        model_task_assignment = (DefaultTableModel) jTable2.getModel();
        model_price_trsfr = (DefaultTableModel) jTable3.getModel();
        
       try {
          connexion = DriverManager.getConnection( url, utilisateur, motDePasse );
          /* charger les noms des offres dans les listes déroulantes à partir de la BD */
           System.out.println("exécution du bloc try");
           statement = connexion.createStatement();
           ResultSet resultat = statement.executeQuery( "SELECT offer_name FROM offer;" );
           /* Récupération des données du résultat de la requête de lecture */
           while ( resultat.next() ) {
            list_offer1.addItem(resultat.getString( "offer_name" ));
            list_offer2.addItem(resultat.getString( "offer_name" ));
            list_offer3.addItem(resultat.getString( "offer_name" ));
           }
           list_offer2.setSelectedIndex(1);
           list_offer3.setSelectedIndex(2);
       } catch ( SQLException e ) {
             /* Gérer les éventuelles erreurs ici */
             System.out.println("exception catch   BD");
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
 
       //***** changer le titre des colonnes selon les offres séléctionnées dans les listes déroulantes ******
        
       String [] list_nom_colonnes = new String [] {"Task", "Margin", list_offer1.getSelectedItem().toString(), list_offer2.getSelectedItem().toString(), list_offer3.getSelectedItem().toString(), list_offer1.getSelectedItem().toString(), list_offer2.getSelectedItem().toString(), list_offer3.getSelectedItem().toString()};
       model_margin_estimation.setColumnIdentifiers(list_nom_colonnes);
       
        /******** fin changement titre des colonnes ******/
        
        filling_tab_price_trsfr(); /******** remplir tab_price_trsfr selon les offres sélectionnées dans les 3 listes déroulantes  ******/
        
        
        
    }

 
    public boolean Banned_Offer (String ofr_name){  // cette fonction petmet de voir si une offre est interdite (existe dans vect_ban_ofr) ou non
      boolean b=false;
      int vect_size = vect_ban_ofr.size();
            for(int y=0; y<vect_size; y++){
               if(ofr_name.equals(vect_ban_ofr.get(y))){ 
                  b = true;   
               }
            }
      return b;
    }
    
    public void sort_tab_rank(){
        float var_temp1;
        String var_temp2;
        if(tab_rank[0][2].toString().equals("")==false){ // tab_rank contient trois offres
         System.out.println("tab_rank contient 3 offres");
         for(int i=2; i>=1; i--){      
             for(int j=0; j<=i-1; j++){
               if(Float.valueOf(tab_rank[1][j].toString()) > Float.valueOf(tab_rank[1][j+1].toString())){
                    // permuter les noms des offres
                    var_temp2 = tab_rank[0][j].toString();
                    tab_rank[0][j] = tab_rank[0][j+1];
                    tab_rank[0][j+1] = var_temp2;
                    // permuter les valeurs de cost
                    var_temp1 = Float.valueOf(tab_rank[1][j].toString());
                    tab_rank[1][j] = tab_rank[1][j+1];
                    tab_rank[1][j+1] = var_temp1;
                    // permuter les valeurs de runtime
                    var_temp1 = Float.valueOf(tab_rank[2][j].toString());
                    tab_rank[2][j] = tab_rank[2][j+1];
                    tab_rank[2][j+1] = var_temp1;
                }
               else if(Objects.equals(Float.valueOf(tab_rank[1][j].toString()), Float.valueOf(tab_rank[1][j+1].toString()))){ // si deux offres ont le même cost
                   if(Float.valueOf(tab_rank[2][j].toString()) > Float.valueOf(tab_rank[2][j+1].toString())){  //alors mettre en 1er l'ofr qui le runtime le plus faible
                      // permuter les noms des offres
                      var_temp2 = tab_rank[0][j].toString();
                      tab_rank[0][j] = tab_rank[0][j+1];
                      tab_rank[0][j+1] = var_temp2;
                      // permuter les valeurs de cost
                      var_temp1 = Float.valueOf(tab_rank[1][j].toString());
                      tab_rank[1][j] = tab_rank[1][j+1];
                      tab_rank[1][j+1] = var_temp1;
                      // permuter les valeurs de runtime
                      var_temp1 = Float.valueOf(tab_rank[2][j].toString());
                      tab_rank[2][j] = tab_rank[2][j+1];
                      tab_rank[2][j+1] = var_temp1;
                   }
               }

            }
         }
        }
        else if(tab_rank[0][1].toString().equals("")==false){  //tab_rank contient deux offres
           System.out.println("tab_rank contient 2 offres");
           if(Float.valueOf(tab_rank[1][0].toString()) > Float.valueOf(tab_rank[1][1].toString())){
                    // permuter les noms des offres
                    var_temp2 = tab_rank[0][0].toString();
                    tab_rank[0][0] = tab_rank[0][1];
                    tab_rank[0][1] = var_temp2;
                    // permuter les valeurs de cost
                    var_temp1 = Float.valueOf(tab_rank[1][0].toString());
                    tab_rank[1][0] = tab_rank[1][1];
                    tab_rank[1][1] = var_temp1;
                    // permuter les valeurs de runtime
                    var_temp1 = Float.valueOf(tab_rank[2][0].toString());
                    tab_rank[2][0] = tab_rank[2][1];
                    tab_rank[2][1] = var_temp1;
            }
           else if(Objects.equals(Float.valueOf(tab_rank[1][0].toString()), Float.valueOf(tab_rank[1][1].toString()))){ // si deux offres ont le même cost
               if(Float.valueOf(tab_rank[2][0].toString()) > Float.valueOf(tab_rank[1][1].toString())){  // alors voir l'ofr qui a le runtime le plus faible
                   // permuter les noms des offres
                    var_temp2 = tab_rank[0][0].toString();
                    tab_rank[0][0] = tab_rank[0][1];
                    tab_rank[0][1] = var_temp2;
                    // permuter les valeurs de cost
                    var_temp1 = Float.valueOf(tab_rank[1][0].toString());
                    tab_rank[1][0] = tab_rank[1][1];
                    tab_rank[1][1] = var_temp1;
                    // permuter les valeurs de runtime
                    var_temp1 = Float.valueOf(tab_rank[2][0].toString());
                    tab_rank[2][0] = tab_rank[2][1];
                    tab_rank[2][1] = var_temp1;
               }
           }
        }
    }
    
    public float min_time_search(){
       float min;
       min = Float.valueOf(tab_rank[2][0].toString());
       if(tab_rank[0][2].toString().equals("")==false){ // tab_rank contient trois offres
          System.out.println("tab_rank contient 3 offres");
          for(int i=1; i<=2; i++){      
             if( Float.valueOf(tab_rank[2][i].toString()) < min){
                 min = Float.valueOf(tab_rank[2][i].toString());
             }
          }
        }
        else if(tab_rank[0][1].toString().equals("")==false){  //tab_rank contient deux offres
           System.out.println("tab_rank contient 2 offres");
           if( Float.valueOf(tab_rank[2][1].toString()) < min){
               min = Float.valueOf(tab_rank[2][1].toString());
           }
        }
       return min;
    }
    
    public float max_trsfr_time_search(){
       float max=0;
       int vect_size = vect_trsfr_time.size();         
       System.out.println("vect_size fffffoooooooooonnnnnnncccccccctttttttiiiiiooooooonnnnn  "+vect_size);
       if (vect_size > 0){
         max = Float.valueOf(vect_trsfr_time.elementAt(0).toString());
         for(int i=0; i<vect_size; i++){
             if(Float.valueOf(vect_trsfr_time.elementAt(i).toString()) > max){
                 max = Float.valueOf(vect_trsfr_time.elementAt(i).toString());
             }
         }
       }
       return max;
    }
    
 
    public void filling_tab_price_trsfr(){  /******** procédure qui remplit tab_price_trsfr selon les offres sélectionnées dans les 3 listes déroulantes  ******/
        float price_trsfr, bandwidth;
        String ofr_name1, ofr_name2, ofr_name3;
        if ((list_offer1.getSelectedItem() != null) && (list_offer2.getSelectedItem() != null) && (list_offer3.getSelectedItem() != null))  {

         try {
            connexion = DriverManager.getConnection( url, utilisateur, motDePasse );
            /* Création de l'objet gérant les requêtes */
            Statement statement = connexion.createStatement();
            ofr_name1 = list_offer1.getSelectedItem().toString();
            ofr_name2 = list_offer2.getSelectedItem().toString();
            ofr_name3 = list_offer3.getSelectedItem().toString();
            
            ResultSet  resultat1 = statement.executeQuery("SELECT offer_name, price_trsfr, bandwidth FROM offer WHERE offer_name = '"+ofr_name1+"'");
            resultat1.next();
            ofr_name1 = resultat1.getString( "offer_name" );
            price_trsfr = resultat1.getFloat( "price_trsfr" );
            bandwidth = resultat1.getFloat( "bandwidth" );
            model_price_trsfr.setValueAt(ofr_name1, 0, 0);
            model_price_trsfr.setValueAt(price_trsfr, 0, 1);
            model_price_trsfr.setValueAt(bandwidth, 0, 2);ofr_name1 = list_offer1.getSelectedItem().toString();
            
            
            ResultSet resultat2 = statement.executeQuery("SELECT offer_name, price_trsfr, bandwidth FROM offer WHERE offer_name = '"+ofr_name2+"'");
            resultat2.next();
            ofr_name2 = resultat2.getString( "offer_name" );
            price_trsfr = resultat2.getFloat( "price_trsfr" );
            bandwidth = resultat2.getFloat( "bandwidth" );
            model_price_trsfr.setValueAt(ofr_name2, 1, 0);
            model_price_trsfr.setValueAt(price_trsfr, 1, 1);
            model_price_trsfr.setValueAt(bandwidth, 1, 2);ofr_name1 = list_offer1.getSelectedItem().toString();
            
            ResultSet resultat3 = statement.executeQuery("SELECT offer_name, price_trsfr, bandwidth FROM offer WHERE offer_name = '"+ofr_name3+"'");
            resultat3.next();
            ofr_name3 = resultat3.getString( "offer_name" );
            price_trsfr = resultat3.getFloat( "price_trsfr" );
            bandwidth = resultat3.getFloat( "bandwidth" );
            model_price_trsfr.setValueAt(ofr_name3, 2, 0);
            model_price_trsfr.setValueAt(price_trsfr, 2, 1);
            model_price_trsfr.setValueAt(bandwidth, 2, 2);
            
            statement.close();
            connexion.close();

        }
        catch ( SQLException e ) {

            e.printStackTrace();

        }
        }
    }
    
    public void vider_champs(){
        Field_Task.setText("");    Field_Margin.setText("");
        Field_Cost1.setText("");    Field_Cost2.setText("");    Field_Cost3.setText("");  
        Field_Runtime1.setText(""); Field_Runtime2.setText(""); Field_Runtime3.setText("");
    }
    
 public void Banned_offer_search(int indx_task){
        String offer_name;
        boolean offer_exist;
        int y;            
        nbr_task = model_margin_estimation.getRowCount();  //voir le nbr de tasks ajoutées dans le tableau
        System.out.println("nbr_task = "+nbr_task);
        for(int z=0; z<nbr_task; z++){  // parcourir les tâches horizontalement pour la tâche task indx et voir les valeurs "1"
           switch (nbr_task) {
              case 2:          // le cas où le processus contient 2 tâches
                if( (Integer.valueOf(matrix2_separation_model.getValueAt(indx_task, z).toString()) == 1) && (model_task_assignment.getValueAt(z, 1).toString().equals("")==false)){ // verify if task(indx) must be separated from task z and it is already assigned to an offer        
                  offer_name = model_task_assignment.getValueAt(z, 1).toString(); // récuopérer le nom de l'offre interdite et déjà affectée
                  System.out.println("offer_name= "+offer_name );
                  offer_exist = false;  y=0; 
                  System.out.println("z= "+z+"  indx_task= "+indx_task);
                   
                  while ((y < vect_ban_ofr.size()) && (offer_exist == false)) {  // parcourir vect_ban_ofr tant que l'offre n'a pas été déjà interdite 
                      if(offer_name.equals(vect_ban_ofr.get(y).toString())){
                          offer_exist = true;  // arreter le parcour si l'ofr a été déjà interdite (éviter d'interdire une ofr 2 fois)
                      }
                      y++;
                  }
                  if(offer_exist == false){
                      vect_ban_ofr.add(offer_name);  // interdire l'ofr de task z (l'ajouter dans vect_ban_ofr)
                  }     
                  System.out.println("offer_exist == "+offer_exist);
                }
              break;
              case 3:
                  if( (Integer.valueOf(matrix3_separation_model.getValueAt(indx_task, z).toString()) == 1) && (model_task_assignment.getValueAt(z, 1).toString().equals("")==false)){ // verify if task(indx) must be separated from task z and it is already assigned to an offer        
                    offer_name = model_task_assignment.getValueAt(z, 1).toString(); // récuopérer le nom de l'offre interdite et déjà affectée
                    System.out.println("offer_name= "+offer_name );
                    offer_exist = false;  y=0; 
                    System.out.println("z= "+z+"  indx_task= "+indx_task);
                   
                    while ((y < vect_ban_ofr.size()) && (offer_exist == false)) {  // parcourir vect_ban_ofr tant que l'offre n'a pas été déjà interdite 
                        if(offer_name.equals(vect_ban_ofr.get(y).toString())){
                            offer_exist = true;  // arreter le parcour si l'ofr a été déjà interdite (éviter d'interdire une ofr 2 fois)
                        }
                        y++;
                    }
                    if(offer_exist == false){
                        vect_ban_ofr.add(offer_name);  // interdire l'ofr de task z (l'ajouter dans vect_ban_ofr)
                    }     
                    System.out.println("offer_exist == "+offer_exist);
                  }        
              break;
              case 4:
                  if( (Integer.valueOf(matrix4_separation_model.getValueAt(indx_task, z).toString()) == 1) && (model_task_assignment.getValueAt(z, 1).toString().equals("")==false)){ // verify if task(indx) must be separated from task z and it is already assigned to an offer        
                    offer_name = model_task_assignment.getValueAt(z, 1).toString(); // récuopérer le nom de l'offre interdite et déjà affectée
                    System.out.println("offer_name= "+offer_name );
                    offer_exist = false;  y=0; 
                    System.out.println("z= "+z+"  indx_task= "+indx_task);
                   
                    while ((y < vect_ban_ofr.size()) && (offer_exist == false)) {  // parcourir vect_ban_ofr tant que l'offre n'a pas été déjà interdite 
                        if(offer_name.equals(vect_ban_ofr.get(y).toString())){
                            offer_exist = true;  // arreter le parcour si l'ofr a été déjà interdite (éviter d'interdire une ofr 2 fois)
                        }
                        y++;
                    }
                    if(offer_exist == false){
                        vect_ban_ofr.add(offer_name);  // interdire l'ofr de task z (l'ajouter dans vect_ban_ofr)
                    }     
                    System.out.println("offer_exist == "+offer_exist);
                  }                   
              break;
              case 5:
                  if( (Integer.valueOf(matrix5_separation_model.getValueAt(indx_task, z).toString()) == 1) && (model_task_assignment.getValueAt(z, 1).toString().equals("")==false)){ // verify if task(indx) must be separated from task z and it is already assigned to an offer        
                    offer_name = model_task_assignment.getValueAt(z, 1).toString(); // récuopérer le nom de l'offre interdite et déjà affectée
                    System.out.println("offer_name= "+offer_name );
                    offer_exist = false;  y=0; 
                    System.out.println("z= "+z+"  indx_task= "+indx_task);
                   
                    while ((y < vect_ban_ofr.size()) && (offer_exist == false)) {  // parcourir vect_ban_ofr tant que l'offre n'a pas été déjà interdite 
                        if(offer_name.equals(vect_ban_ofr.get(y).toString())){
                            offer_exist = true;  // arreter le parcour si l'ofr a été déjà interdite (éviter d'interdire une ofr 2 fois)
                        }
                        y++;
                    }
                    if(offer_exist == false){
                        vect_ban_ofr.add(offer_name);  // interdire l'ofr de task z (l'ajouter dans vect_ban_ofr)
                    }     
                    System.out.println("offer_exist == "+offer_exist);
                  }                        
              break;
              case 9:
                  if( (Integer.valueOf(matrix9_separation_model.getValueAt(indx_task, z).toString()) == 1) && (model_task_assignment.getValueAt(z, 1).toString().equals("")==false)){ // verify if task(indx) must be separated from task z and it is already assigned to an offer        
                    offer_name = model_task_assignment.getValueAt(z, 1).toString(); // récuopérer le nom de l'offre interdite et déjà affectée
                    System.out.println("offer_name= "+offer_name );
                    offer_exist = false;  y=0; 
                    System.out.println("z= "+z+"  indx_task= "+indx_task);
                   
                    while ((y < vect_ban_ofr.size()) && (offer_exist == false)) {  // parcourir vect_ban_ofr tant que l'offre n'a pas été déjà interdite 
                        if(offer_name.equals(vect_ban_ofr.get(y).toString())){
                            offer_exist = true;  // arreter le parcour si l'ofr a été déjà interdite (éviter d'interdire une ofr 2 fois)
                        }
                        y++;
                    }
                    if(offer_exist == false){
                        vect_ban_ofr.add(offer_name);  // interdire l'ofr de task z (l'ajouter dans vect_ban_ofr)
                    }     
                    System.out.println("offer_exist == "+offer_exist);
                  }                        
              break;
            } 
            
        }
    } 
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jSlider1 = new javax.swing.JSlider();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton5 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        list_offer1 = new javax.swing.JComboBox<>();
        list_offer2 = new javax.swing.JComboBox<>();
        list_offer3 = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        Field_Cost1 = new javax.swing.JTextField();
        Field_Runtime1 = new javax.swing.JTextField();
        Field_Cost2 = new javax.swing.JTextField();
        Field_Runtime2 = new javax.swing.JTextField();
        Field_Cost3 = new javax.swing.JTextField();
        Field_Runtime3 = new javax.swing.JTextField();
        Field_Margin = new javax.swing.JTextField();
        Field_Task = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Business Process Outsourcing to the Cloud");

        jTabbedPane1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jPanel1.setBackground(new java.awt.Color(154, 168, 182));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("Task name");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setText("Margin");

        jButton2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/test1/icons/icon_ add_activity.png"))); // NOI18N
        jButton2.setText("       Add task");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jButton4.setIcon(new javax.swing.ImageIcon("C:\\Users\\zarour\\Desktop\\icons\\task separation 32.png")); // NOI18N
        jButton4.setText(" Task separation");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jButton3.setIcon(new javax.swing.ImageIcon("C:\\Users\\zarour\\Desktop\\icons\\cloud transfer 32.png")); // NOI18N
        jButton3.setText("  Data transfer");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jSlider1.setBackground(new java.awt.Color(180, 190, 199));
        jSlider1.setFont(new java.awt.Font("Calibri", 1, 12)); // NOI18N
        jSlider1.setForeground(new java.awt.Color(42, 68, 86));
        jSlider1.setMajorTickSpacing(1);
        jSlider1.setMaximum(10);
        jSlider1.setPaintLabels(true);
        jSlider1.setPaintTicks(true);
        jSlider1.setValue(0);
        jSlider1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jTable1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Task", "Margin", "offer 1 CostExe", "offer 2 CostExe", "offer 3 CostExe", "offer 1 Runtime", "offer 2 Runtime", "offer 3 Runtime"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jButton5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/test1/icons/icon_autmation2.png"))); // NOI18N
        jButton5.setText("   Generate configuration");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Task", "Cloud offer"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable2);

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Cloud offer", "Price transfer", "Bandwidth"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable3.setIntercellSpacing(new java.awt.Dimension(1, 2));
        jTable3.setRowHeight(21);
        jTable3.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                jTable3InputMethodTextChanged(evt);
            }
        });
        jScrollPane3.setViewportView(jTable3);

        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        list_offer1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                list_offer1ItemStateChanged(evt);
            }
        });
        list_offer1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                list_offer1MouseClicked(evt);
            }
        });
        list_offer1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                list_offer1ActionPerformed(evt);
            }
        });

        list_offer2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                list_offer2ItemStateChanged(evt);
            }
        });
        list_offer2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                list_offer2ActionPerformed(evt);
            }
        });

        list_offer3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                list_offer3ItemStateChanged(evt);
            }
        });
        list_offer3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                list_offer3ActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Cost Execution");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("Runtime");

        Field_Cost1.setText("3");

        Field_Runtime1.setText("44");

        Field_Cost2.setText("5");

        Field_Runtime2.setText("66");

        Field_Cost3.setText("7");
        Field_Cost3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Field_Cost3ActionPerformed(evt);
            }
        });

        Field_Runtime3.setText("88");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(list_offer1, 0, 79, Short.MAX_VALUE)
                    .addComponent(Field_Cost1)
                    .addComponent(Field_Runtime1, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE))
                .addGap(56, 56, 56)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(Field_Runtime2, javax.swing.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                            .addComponent(Field_Cost2))
                        .addGap(61, 61, 61)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Field_Cost3)
                            .addComponent(Field_Runtime3)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(list_offer2, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(61, 61, 61)
                        .addComponent(list_offer3, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 59, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(list_offer1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(list_offer2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(list_offer3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(Field_Cost1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Field_Cost2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Field_Cost3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(Field_Runtime1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Field_Runtime2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Field_Runtime3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(27, Short.MAX_VALUE))
        );

        Field_Margin.setText("2");
        Field_Margin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Field_MarginActionPerformed(evt);
            }
        });

        jPanel4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(0, 102, 102), new java.awt.Color(0, 102, 102), new java.awt.Color(0, 102, 102), new java.awt.Color(0, 102, 102)));

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(19, 80, 110));
        jLabel7.setText("??  $");

        jLabel5.setBackground(new java.awt.Color(89, 128, 128));
        jLabel5.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel5.setIcon(new javax.swing.ImageIcon("C:\\Users\\zarour\\Desktop\\icons\\dollar.png")); // NOI18N
        jLabel5.setText("Global cost");

        jLabel6.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel6.setIcon(new javax.swing.ImageIcon("C:\\Users\\zarour\\Desktop\\icons\\time (1).png")); // NOI18N
        jLabel6.setText("Global time");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(19, 80, 110));
        jLabel8.setText("?? sec");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel8))
                .addGap(21, 21, 21))
        );

        jLabel9.setBackground(new java.awt.Color(175, 194, 203));
        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/test1/icons/icon_weight.png"))); // NOI18N
        jLabel9.setText("  Tolerate Delay");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 955, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGap(59, 59, 59)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel3)
                                            .addComponent(jLabel4))
                                        .addGap(38, 38, 38)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(Field_Margin, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(Field_Task, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(41, 41, 41)
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(36, 36, 36)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(2, 2, 2)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(50, 50, 50)
                                        .addComponent(jLabel9)))
                                .addGap(55, 55, 55)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(61, 61, 61)
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(34, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Field_Task, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(19, 19, 19)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Field_Margin, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addGap(18, 18, 18)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(55, 55, 55)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(98, 98, 98))
        );

        jTabbedPane1.addTab("Cloud offer selection", new javax.swing.ImageIcon("C:\\Users\\zarour\\Desktop\\icons\\cloud search32.png"), jPanel1); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1048, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 686, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Cloud offer management", new javax.swing.ImageIcon("C:\\Users\\zarour\\Desktop\\icons\\cloud management 32.png"), jPanel2); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void Field_Cost3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Field_Cost3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Field_Cost3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        String task_name = Field_Task.getText();
        int margin = Integer.valueOf(Field_Margin.getText());
        float cost1 = Float.valueOf(Field_Cost1.getText());
        float cost2 = Float.valueOf(Field_Cost2.getText());
        float cost3 = Float.valueOf(Field_Cost3.getText());
        float runtime1 = Float.valueOf(Field_Runtime1.getText());
        float runtime2 = Float.valueOf(Field_Runtime2.getText());
        float runtime3 = Float.valueOf(Field_Runtime3.getText());
        model_margin_estimation.addRow(new Object[]{task_name, margin, cost1, cost2, cost3, runtime1, runtime2, runtime3});
        model_task_assignment.addRow(new Object[]{task_name, ""});
        try {
            connexion = DriverManager.getConnection( url, utilisateur, motDePasse );
            /* Création de l'objet gérant les requêtes */
            System.out.println("exécution du bloc try");

            Statement statement = connexion.createStatement();
            /* Exécution d'une requête d'écriture */

            /* enregister les exigences de sécurité dans la table Activity */
            statement.executeUpdate( "INSERT INTO margin_estimation (task_name, margin, costexe1, costexe2, costexe3, runtime1, runtime2, runtime3) VALUES ('"+task_name+"', '"+margin+"', '"+cost1+"', '"+cost2+"','"+cost3+"', '"+runtime1+"','"+runtime2+"','"+runtime3+"');" );
            statement.close();
            connexion.close();

        } catch ( SQLException e ) {
            /* Gérer les éventuelles erreurs ici */
            System.out.println("exception catch");
            e.printStackTrace();

        }
        //  vider_champs();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        nbr_task = model_margin_estimation.getRowCount();
        switch (nbr_task) {
            case 2:     // le cas où le processus contient 2 tâches
            matrix2_separation matrix2_sep = new matrix2_separation();
            matrix2_sep.setSize(400, 150);
            matrix2_sep.setLocation(600, 400);
            matrix2_sep.setVisible(true);
            bouton2_validation_separation.addActionListener(new ActionListener() {    // définir la réaction du bouton en dessous de la matrice de separation
                public void actionPerformed(ActionEvent evt) {
                    table_separation2 = new JTable(matrix2_separation_model);  // recharger les valeurs (separations) saisies dans le modèle du jtable
                    matrix2_sep.setVisible(false);     // fermer la fenêtre après validation
                }
            });
            break;
            case 3:
            matrix3_separation matrix3_sep = new matrix3_separation();
            matrix3_sep.setSize(400, 175);
            matrix3_sep.setLocation(600, 400);
            matrix3_sep.setVisible(true);
            bouton3_validation_separation.addActionListener(new ActionListener() {    // définir la réaction du bouton en dessous de la matrice de separation
                public void actionPerformed(ActionEvent evt) {
                    table_separation3 = new JTable(matrix3_separation_model);  // recharger les valeurs (separations) saisies dans le modèle du jtable
                    matrix3_sep.setVisible(false);     // fermer la fenêtre après validation
                }
            });
            break;
            case 4:
            matrix4_separation matrix4_sep = new matrix4_separation();
            matrix4_sep.setSize(500, 180);
            matrix4_sep.setLocation(600, 400);
            matrix4_sep.setVisible(true);
            bouton4_validation_separation.addActionListener(new ActionListener() {    // définir la réaction du bouton en dessous de la matrice de separation
                public void actionPerformed(ActionEvent evt) {
                    table_separation4 = new JTable(matrix4_separation_model);  // recharger les valeurs (separations) saisies dans le modèle du jtable
                    matrix4_sep.setVisible(false);     // fermer la fenêtre après validation
                }
            });
            break;
            case 5:
            matrix5_separation matrix5_sep = new matrix5_separation();
            matrix5_sep.setSize(500, 200);
            matrix5_sep.setLocation(600, 400);
            matrix5_sep.setVisible(true);
            bouton5_validation_separation.addActionListener(new ActionListener() {    // définir la réaction du bouton en dessous de la matrice de separation
                public void actionPerformed(ActionEvent evt) {
                    table_separation5 = new JTable(matrix5_separation_model);  // recharger les valeurs (separations) saisies dans le modèle du jtable
                    matrix5_sep.setVisible(false);     // fermer la fenêtre après validation
                }
            });
            break;
            case 9:
            matrix9_separation matrix9_sep = new matrix9_separation();
            matrix9_sep.setSize(800, 280);
            matrix9_sep.setLocation(600, 400);
            matrix9_sep.setVisible(true);
            bouton9_validation_separation.addActionListener(new ActionListener() {    // définir la réaction du bouton en dessous de la matrice de separation
                public void actionPerformed(ActionEvent evt) {
                    table_separation9 = new JTable(matrix9_separation_model);  // recharger les valeurs (separations) saisies dans le modèle du jtable
                    matrix9_sep.setVisible(false);     // fermer la fenêtre après validation
                }
            });
            break;
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        nbr_task = model_margin_estimation.getRowCount();  //voir le nbr de tasks ajoutées dans le tableau
        switch (nbr_task) {
            case 2:          // le cas où le processus contient 2 tâches
            matrix2 matrice_transfer2 = new matrix2();
            matrice_transfer2.setSize(400, 140);
            matrice_transfer2.setLocation(200, 400);
            matrice_transfer2.setVisible(true);
            bouton2_validation_transfer.addActionListener(new ActionListener() {    // définir la réaction du bouton en dessous de la matrice de transfert
                public void actionPerformed(ActionEvent evt) {
                    table_transfer2 = new JTable(matrix2_transfer_model);  // recharger les valeurs (data transfer) saisies dans le modèle du jtable
                    matrice_transfer2.setVisible(false);     // fermer la fenêtre après validation
                }
            });
            break;
            case 3:
            matrix3 matrice_transfer3 = new matrix3();
            matrice_transfer3.setSize(400, 160);
            matrice_transfer3.setLocation(200, 400);
            matrice_transfer3.setVisible(true);
            bouton3_validation_transfer.addActionListener(new ActionListener() {    // définir la réaction du bouton en dessous de la matrice de transfert
                public void actionPerformed(ActionEvent evt) {
                    table_transfer3 = new JTable(matrix3_transfer_model);  // recharger les valeurs (data transfer) saisies dans le modèle du jtable
                    matrice_transfer3.setVisible(false);     // fermer la fenêtre après validation
                }
            });
            break;
            case 4:
            matrix4 matrice_transfer4 = new matrix4();
            matrice_transfer4.setSize(500, 170);
            matrice_transfer4.setLocation(200, 400);
            matrice_transfer4.setVisible(true);
            bouton4_validation_transfer.addActionListener(new ActionListener() {    // définir la réaction du bouton en dessous de la matrice de transfert
                public void actionPerformed(ActionEvent evt) {
                    table_transfer4 = new JTable(matrix4_transfer_model);  // recharger les valeurs (data transfer) saisies dans le modèle du jtable
                    matrice_transfer4.setVisible(false);     // fermer la fenêtre après validation
                }
            });
            break;
            case 5:
            matrix5 matrice_transfer5 = new matrix5();
            matrice_transfer5.setSize(500, 185);
            matrice_transfer5.setLocation(200, 400);
            matrice_transfer5.setVisible(true);
            bouton5_validation_transfer.addActionListener(new ActionListener() {    // définir la réaction du bouton en dessous de la matrice de transfert
                public void actionPerformed(ActionEvent evt) {
                    table_transfer5 = new JTable(matrix5_transfer_model);  // recharger les valeurs (data transfer) saisies dans le modèle du jtable
                    matrice_transfer5.setVisible(false);     // fermer la fenêtre après validation
                }
            });
            break;
            case 9:
            matrix9 matrice_transfer9 = new matrix9();
            matrice_transfer9.setSize(800, 265);
            matrice_transfer9.setLocation(200, 400);
            matrice_transfer9.setVisible(true);
            bouton9_validation_transfer.addActionListener(new ActionListener() {    // définir la réaction du bouton en dessous de la matrice de transfert
                public void actionPerformed(ActionEvent evt) {
                    table_transfer9 = new JTable(matrix9_transfer_model);  // recharger les valeurs (data transfer) saisies dans le modèle du jtable
                    matrice_transfer9.setVisible(false);     // fermer la fenêtre après validation
                }
            });
            break;
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        String name_offer;
        nbr_task = model_margin_estimation.getRowCount();
        float accepted_delay = jSlider1.getValue();
        float difference, margin;
        int r;
        float min_time;
        int indx;
        float total_cost=0, total_time=0;
        boolean sufficient_margin;
        //vider la 2ème colonne de tab_task_assign pour recalculer une nouv configuration
        for(int i=0; i<nbr_task; i++){
            model_task_assignment.setValueAt("", i, 1);
        }

        for(int l=0; l<=2; l++){
            for(int m=0; m<=2; m++){
                tab_rank[l][m]="";
            }
        }
        for(int i=0; i<nbr_task; i++){
            System.out.println("******************* num task *********************** = "+i);
            vect_ban_ofr.removeAllElements();
            Banned_offer_search(i);
            int vect_size = vect_ban_ofr.size();
            if(vect_size==3){
                JOptionPane.showMessageDialog(null, "There is no offer available for "+ model_margin_estimation.getValueAt(i, 0), "Warning", JOptionPane.WARNING_MESSAGE);
            }
            for(int y=0; y<vect_size; y++){
                System.out.println(" Vect_Ban_Ofr ["+y+"] = "+vect_ban_ofr.get(y));
            }
            r=0;
            float qte_trsfr, price_trsfr = 0, cost_trsfr, time_trsfr = 0, bandwidth = 0, cost_exe, runtime, cost, time;
            String ofr_task_k;
            for(int j=2; j<=4; j++){  //  parcourir chaque offre, j commence de 2 puisque les couts et runtimes commencent dans la 3ème colonne
                name_offer = model_margin_estimation.getColumnName(j);
                if (Banned_Offer(name_offer)==false){     // si l'offre est autorisée
                    System.out.println("offre autorisée "+name_offer);
                    // ajouter ici une boucle pr calculer le total des coûts de trsfr
                    cost_trsfr = 0;
                    vect_trsfr_time.removeAllElements(); // réinitialiser vect_trsfr_time
                    for (int k=0; k<nbr_task; k++){
                        //recup la qte de data trsfr selon le nbr task (à partir de matrix2_transfer_model ou matrix3_transfer_model ...)
                        qte_trsfr=0;
                        switch (nbr_task) {
                            case 2:     // le cas où le processus contient 2 tâches
                            qte_trsfr = Float.valueOf(matrix2_transfer_model.getValueAt(k, i).toString());  // récupérer la qte de data transférée de task k vers task i
                            break;
                            case 3:
                            qte_trsfr = Float.valueOf(matrix3_transfer_model.getValueAt(k, i).toString());  // récupérer la qte de data transférée de task k vers task i
                            break;
                            case 4:
                            qte_trsfr = Float.valueOf(matrix4_transfer_model.getValueAt(k, i).toString());  // récupérer la qte de data transférée de task k vers task i
                            break;
                            case 5:
                            qte_trsfr = Float.valueOf(matrix5_transfer_model.getValueAt(k, i).toString());  // récupérer la qte de data transférée de task k vers task i
                            break;
                            case 9:
                            qte_trsfr = Float.valueOf(matrix9_transfer_model.getValueAt(k, i).toString());  // récupérer la qte de data transférée de task k vers task i
                            break;
                        }
                        // fin recup qte data transfer
                        System.out.println("qte_trsfr "+qte_trsfr);
                        ofr_task_k = model_task_assignment.getValueAt(k, 1).toString();  // recup le nom de l'ofr affectée à task k
                        System.out.println("ofr_task_k "+ofr_task_k);
                        if(name_offer.equals(ofr_task_k)){  // si le trsfr est entre deux tâches affectées à la même ofr
                            cost_trsfr = cost_trsfr + 0;
                            System.out.println("offres similaires "+name_offer+" && "+ofr_task_k);
                        }
                        else if(ofr_task_k.equals("")==false){
                            try {  // récupérer à partir du offer_name le price et bandwidth de l'offre affectée à task k d'où proviennent les data
                                connexion = DriverManager.getConnection( url, utilisateur, motDePasse );
                                /* Création de l'objet gérant les requêtes */
                                Statement statement = connexion.createStatement();
                                ResultSet  resultat = statement.executeQuery( "SELECT price_trsfr, bandwidth FROM offer WHERE offer_name = '"+ofr_task_k+"'" );
                                resultat.next();
                                price_trsfr = resultat.getFloat( "price_trsfr" );
                                bandwidth = resultat.getFloat( "bandwidth" );
                                System.out.println("price_trsfr "+price_trsfr);
                                System.out.println("bandwidth "+bandwidth);

                                statement.close();
                                connexion.close();
                            }
                            catch ( SQLException e ) {    e.printStackTrace();  }
                            cost_trsfr = cost_trsfr + qte_trsfr * price_trsfr;
                            time_trsfr = qte_trsfr / bandwidth;
                            System.out.println("time_trsfr oooooooooooooooooooooooooooooo   "+time_trsfr);
                            vect_trsfr_time.add(time_trsfr);
                            System.out.println("vect_size  oooooooooooooooooooooooooo   "+vect_trsfr_time.size());

                        }
                        System.out.println("cost_trsfr "+cost_trsfr);
                    }
                    cost_exe = Float.valueOf(model_margin_estimation.getValueAt(i, j).toString());  // récup le cost_exe de task i dans ofr j
                    cost = cost_exe + cost_trsfr;
                    runtime = Float.valueOf(model_margin_estimation.getValueAt(i, j+3).toString()); // j+5 puisque le runtime de la 1ère offre est ds la 6ème colonne
                    time_trsfr = max_trsfr_time_search(); // recup seulmnt le max du vect_time_trsfr puisq les trsfr se font en parallèle
                    System.out.println("max trsfr_time "+time_trsfr);
                    time = runtime + time_trsfr;
                    // remplir dans tab_rank le nom, cost et runtime de chaq offre autorisée (qui peuvent être 1, 2 ou 3)
                    tab_rank[0][r] = name_offer;  // dans la 1ère boucle j-2=2-2=0, donc la 1ère colonne, dans la 2ème boucle 3-2=1, donc la 2ème colonne
                    tab_rank[1][r] = cost;
                    tab_rank[2][r] = time;
                    r++;

                }

            }
            System.out.println("************* tab_rank before sorting ************");
            for(int l=0; l<=2; l++){
                for(int m=0; m<=2; m++){
                    System.out.print(" // "+tab_rank[l][m]+" // ");
                }
                System.out.println("");
            }
            sort_tab_rank();
            System.out.println("****************  tab_rank after sorting  *************");
            for(int l=0; l<=2; l++){
                for(int m=0; m<=2; m++){
                    System.out.print(" // "+tab_rank[l][m]+" // ");
                }
                System.out.println("");
            }
            min_time = min_time_search();
            System.out.println("min_time = "+ min_time);

            indx=0;
            sufficient_margin=false;

            while ((sufficient_margin==false) && (tab_rank[0][indx].toString().equals("")==false)){
                difference = Float.valueOf(tab_rank[2][indx].toString()) - min_time;
                margin = Float.valueOf(model_margin_estimation.getValueAt(i, 1).toString());
                System.out.println("margin = "+margin);
                System.out.println("accepted_delay = "+accepted_delay);
                System.out.println("difference = "+difference);
                if(difference <= margin + accepted_delay) {
                    model_task_assignment.setValueAt(tab_rank[0][indx], i, 1);
                    sufficient_margin = true;
                    total_cost = total_cost + Float.valueOf(tab_rank[1][indx].toString());
                    if (margin == 0){  // tenir compte uniquement des tâches du chemin critique (l'algo considère uniquemnt les tâches ayant une marge 0 (pas les marges les plus faibles)
                        total_time = total_time + Float.valueOf(tab_rank[2][indx].toString());
                    }
                }
                indx++;
            }
            // reinitialiser tab_rank pour classer les offres selon la tâche suivante
            for(int l=0; l<=2; l++){
                for(int m=0; m<=2; m++){
                    tab_rank[l][m]="";
                }
            }
        }
        DecimalFormat df = new DecimalFormat(".##");
        jLabel7.setText(String.valueOf(df.format(total_cost))+" $");
        jLabel8.setText(String.valueOf(df.format(total_time))+" sec");
        System.out.println("total_cost = "+total_cost+" $");
        System.out.println("total_time = "+total_time+" sec");
        /*
        for(int i=0; i<nbr_task; i++){
            for(int j=0; j<8; j++){
                System.out.print(jTable1.getValueAt(i, j)+" // ");
            }
        }
        */
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jTable3InputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_jTable3InputMethodTextChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_jTable3InputMethodTextChanged

    private void list_offer1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_list_offer1ItemStateChanged
        //***** changer le titre des colonnes selon les offres séléctionnées dans les listes déroulantes ******
        if ((list_offer1.getSelectedItem() != null) && (list_offer2.getSelectedItem() != null) && (list_offer3.getSelectedItem() != null))  {
            String [] list_nom_colonnes = new String [] {"Task", "Margin", list_offer1.getSelectedItem().toString(), list_offer2.getSelectedItem().toString(), list_offer3.getSelectedItem().toString(), list_offer1.getSelectedItem().toString(), list_offer2.getSelectedItem().toString(), list_offer3.getSelectedItem().toString()};
            model_margin_estimation.setColumnIdentifiers(list_nom_colonnes);
        }
        /******** fin changement titre des colonnes ******/
        filling_tab_price_trsfr(); //remplir tab_rank selon les off sélectionnées
    }//GEN-LAST:event_list_offer1ItemStateChanged

    private void list_offer1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_list_offer1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_list_offer1MouseClicked

    private void list_offer1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_list_offer1ActionPerformed

    }//GEN-LAST:event_list_offer1ActionPerformed

    private void list_offer2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_list_offer2ItemStateChanged
        if ((list_offer1.getSelectedItem() != null) && (list_offer2.getSelectedItem() != null) && (list_offer3.getSelectedItem() != null))  {
            String [] list_nom_colonnes = new String [] {"Task", "Margin", list_offer1.getSelectedItem().toString(), list_offer2.getSelectedItem().toString(), list_offer3.getSelectedItem().toString(), list_offer1.getSelectedItem().toString(), list_offer2.getSelectedItem().toString(), list_offer3.getSelectedItem().toString()};
            model_margin_estimation.setColumnIdentifiers(list_nom_colonnes);
            filling_tab_price_trsfr(); //remplir tab_rank selon les off sélectionnées
        }
    }//GEN-LAST:event_list_offer2ItemStateChanged

    private void list_offer2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_list_offer2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_list_offer2ActionPerformed

    private void list_offer3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_list_offer3ItemStateChanged
        if ((list_offer1.getSelectedItem() != null) && (list_offer2.getSelectedItem() != null) && (list_offer3.getSelectedItem() != null))  {
            String [] list_nom_colonnes = new String [] {"Task", "Margin", list_offer1.getSelectedItem().toString(), list_offer2.getSelectedItem().toString(), list_offer3.getSelectedItem().toString(), list_offer1.getSelectedItem().toString(), list_offer2.getSelectedItem().toString(), list_offer3.getSelectedItem().toString()};
            model_margin_estimation.setColumnIdentifiers(list_nom_colonnes);
            filling_tab_price_trsfr(); //remplir tab_rank selon les off sélectionnées
    }//GEN-LAST:event_list_offer3ItemStateChanged
    }
    private void list_offer3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_list_offer3ActionPerformed
    }//GEN-LAST:event_list_offer3ActionPerformed

    private void Field_MarginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Field_MarginActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Field_MarginActionPerformed

    /**
     * @param args the command line arguments
     */
 

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField Field_Cost1;
    private javax.swing.JTextField Field_Cost2;
    private javax.swing.JTextField Field_Cost3;
    private javax.swing.JTextField Field_Margin;
    private javax.swing.JTextField Field_Runtime1;
    private javax.swing.JTextField Field_Runtime2;
    private javax.swing.JTextField Field_Runtime3;
    private javax.swing.JTextField Field_Task;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JTabbedPane jTabbedPane1;
    public javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JComboBox<String> list_offer1;
    private javax.swing.JComboBox<String> list_offer2;
    private javax.swing.JComboBox<String> list_offer3;
    // End of variables declaration//GEN-END:variables
}
