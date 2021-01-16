/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package perspective_project.matrices_separation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.AbstractListModel;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.SwingConstants;
import static javax.swing.SwingConstants.CENTER;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import static perspective_project.Frame_Perspective2.model_margin_estimation;
import static perspective_project.matrices_separation.matrix3_separation.bouton3_validation_separation;
import static perspective_project.matrices_separation.matrix3_separation.matrix3_separation_model;

/**
 *
 * @author zarour
 */

class RowHeaderRenderer extends JLabel implements ListCellRenderer {
 
  RowHeaderRenderer(JTable table) {
    JTableHeader header = table.getTableHeader();
    setOpaque(true);
    setBorder(UIManager.getBorder("TableHeader.cellBorder"));
    setHorizontalAlignment(CENTER);
    setForeground(header.getForeground());
    setBackground(header.getBackground());
    setFont(header.getFont());
  }

  public Component getListCellRendererComponent(JList list, Object value,
      int index, boolean isSelected, boolean cellHasFocus) {
    setText((value == null) ? "" : value.toString());
    return this;
  } 
}

public class matrix2_separation extends JFrame {
    public static JTable table_separation2;
    public static DefaultTableModel matrix2_separation_model;
    public static JButton bouton2_validation_separation;
    String task_name1, task_name2;
  public matrix2_separation() {
   super("Task separation matrix ");
   setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
   task_name1 = model_margin_estimation.getValueAt(0, 0).toString();
   task_name2 = model_margin_estimation.getValueAt(1, 0).toString();
  

   ListModel lm = new AbstractListModel() {
      String headers[] = { task_name1, task_name2};
      public int getSize() {
        return headers.length;
      }

      public Object getElementAt(int index) {
        return headers[index];
      }
    };

    matrix2_separation_model = new DefaultTableModel(lm.getSize(), 2) {
		public boolean isCellEditable(int row, int col) {
                    if(row>=col){ return false; }
                
			return true;
		};
	};
 
    /*
    public class MyTableModel extends DefaultTableModel { 
        @Override public boolean isCellEditable(int rowIndex, int columnIndex) 
        { //ici la cellule (1, 2) est non-editable if (rowIndex == 1 && columnIndex == 2) return false; //le reste est editable return true; } }
    
    */
    
    table_separation2 = new JTable(matrix2_separation_model);
 


      
    
    //table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    String titres_colonnes[] = { task_name1, task_name2 };
    matrix2_separation_model.setColumnIdentifiers(titres_colonnes);
   //remplir la diagonale avec des 0 puisque une tâche ne peut pas être séparée d'elle même 
    matrix2_separation_model.setValueAt(0, 0, 0);
    matrix2_separation_model.setValueAt(0, 1, 1);
    
    JList rowHeader = new JList(lm);
    rowHeader.setFixedCellWidth(90);
    table_separation2.setRowHeight(25);
    rowHeader.setFixedCellHeight(table_separation2.getRowHeight());
    //    + table.getRowMargin());
    //                           + table.getIntercellSpacing().height);
    rowHeader.setCellRenderer(new RowHeaderRenderer(table_separation2));

    JScrollPane scroll = new JScrollPane(table_separation2);
   scroll.setRowHeaderView(rowHeader);
    this.setLayout(new BorderLayout());
    this.add(scroll, BorderLayout.CENTER);
    
    bouton2_validation_separation = new JButton("Validate task separation");


       JButton bouton_remplissage = new JButton("Complete matrix");
       JPanel panel = new JPanel();
       panel.add(bouton2_validation_separation, BorderLayout.EAST);
       panel.add(bouton_remplissage, BorderLayout.WEST);
       this.getContentPane().add(panel, BorderLayout.SOUTH);
   
   bouton_remplissage.addActionListener(new ActionListener() {    // définir la réaction du bouton en dessous de la matrice de separation
                    public void actionPerformed(ActionEvent evt) {
                      for(int i=0; i<=1; i++){
                          for(int j=0; j<=1; j++){
                           if(i < j){   
                               int value = Integer.valueOf(matrix2_separation_model.getValueAt(i, j).toString());
                               matrix2_separation_model.setValueAt(value, j, i);
                           }
                          }
                      }   
                         
                    }
                });   
   
 
    // Aligner le cotenu des cellules au centre
/*   DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
  renderer.setHorizontalAlignment(SwingConstants.CENTER);
  
    TableColumn col0 = table.getColumnModel().getColumn(0);
    TableColumn col1 = table.getColumnModel().getColumn(1);
    TableColumn col2 = table.getColumnModel().getColumn(2);
    TableColumn col3 = table.getColumnModel().getColumn(3);
    

    col0.setCellRenderer(renderer);
    col1.setCellRenderer(renderer);
    col2.setCellRenderer(renderer);
    col3.setCellRenderer(renderer);
    // fin alignement du contenu
  */  
    table_separation2.setDefaultRenderer(Object.class, new ColorRenderer());
    
  }
  
  
public class ColorRenderer extends DefaultTableCellRenderer {
 
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)  {
 
    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    Component c2 = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

    if(column==row) {
      c.setBackground(Color.LIGHT_GRAY);
     return c;
    }
    if(row >= column) {
      c.setBackground(Color.GREEN);
     return c;
    }
    this.setHorizontalAlignment(SwingConstants.CENTER);
    c2.setBackground(Color.WHITE);
      return c2;
  }
}
  

  
  
  
/*
  public static void main(String[] args) {
    matrix2 m = new matrix2();
    m.setSize(400, 140);
    m.setLocation(500, 400);
    m.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });
    m.setVisible(true);
 }
*/
}/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

