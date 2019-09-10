/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test1;

import javax.swing.table.AbstractTableModel;

/**
 *
 * @author zarour
 */
public class Modele_table extends AbstractTableModel{ 
   Object donnees[][];
   String titres[];
   public Modele_table(
      Object donnees[][], String titres[]){ 
      this.donnees = donnees; 
      this.titres = titres; 
   } 
   public int getColumnCount(){ 
      return donnees[0].length; 
   }
   public Object getValueAt(int parm1, int parm2){ 
      return donnees[parm1][parm2]; 
   }
   public int getRowCount() { 
      return donnees.length; 
   }
   public String getColumnName(int col){ 
      return titres[col]; 
   } 
}
