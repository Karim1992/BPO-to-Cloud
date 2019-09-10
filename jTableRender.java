/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test1;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
 
/**
 * Définir l'affichage dans un JTable
 * @author Fobec 2010
 */
public class jTableRender extends DefaultTableCellRenderer {
 
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
       float max_score=0;
       int index;       
       
      ArrayList list_index = new ArrayList();

       int nbr_act = table.getRowCount();
       
       System.out.println("nbrrr     lignes   " + nbr_act);
       
       for(int i=0; i<nbr_act; i++){
         index=1;
         max_score = Float.parseFloat(table.getValueAt(i,1).toString().replace(',','.'));
         for(int j=1; j<4; j++){
             if(Float.parseFloat(table.getValueAt(i,j).toString().replace(',','.')) > max_score){
                max_score = Float.parseFloat(table.getValueAt(i,j).toString().replace(',','.'));
                index = j; 
             }
         }
         list_index.add(index);
        }  
        
        Font f = new Font("Arial", Font.BOLD, 12); 

       
         if ((row==0) && (column== (int)list_index.get(0)) ){  
              component.setBackground(Color.green);
              component.setFont(f);
              
         }
         else if ((row==1) && (column== (int)list_index.get(1)) ){  
              component.setBackground(Color.green);
              component.setFont(f);
         }
         else if ((row==2) && (column== (int)list_index.get(2)) ){  
              component.setBackground(Color.green);
              component.setFont(f);
         } 
         else if ((row==3) && (column== (int)list_index.get(3)) ){  
              component.setBackground(Color.green);
              component.setFont(f);
         }
         else{  
            component.setBackground(Color.white);
         }
         
        return component;
    }
       
}
