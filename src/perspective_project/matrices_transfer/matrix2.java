/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package perspective_project.matrices_transfer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import static perspective_project.matrices_transfer.matrix3.matrix3_transfer_model;

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
public class matrix2 extends JFrame {
    public static JTable table_transfer2;
    public static DefaultTableModel matrix2_transfer_model;
    public static JButton bouton2_validation_transfer;
    String task_name1, task_name2;
  public matrix2() {
   super("Data transfer matrix");
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

    matrix2_transfer_model = new DefaultTableModel(lm.getSize(), 2) {
		public boolean isCellEditable(int row, int col) {
                    if(row==col){ return false; }
                
			return true;
		};
	};
 
    /*
    public class MyTableModel extends DefaultTableModel { 
        @Override public boolean isCellEditable(int rowIndex, int columnIndex) 
        { //ici la cellule (1, 2) est non-editable if (rowIndex == 1 && columnIndex == 2) return false; //le reste est editable return true; } }
    
    */
    
    table_transfer2 = new JTable(matrix2_transfer_model);
 

    //remplir la diagonale avec des 0 puisque une tâche ne peut pas être séparée d'elle même 
    matrix2_transfer_model.setValueAt(0, 0, 0);
    matrix2_transfer_model.setValueAt(0, 1, 1);
      
    
    //table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    String titres_colonnes[] = { task_name1, task_name2 };
    matrix2_transfer_model.setColumnIdentifiers(titres_colonnes);
    JList rowHeader = new JList(lm);
    rowHeader.setFixedCellWidth(90);
    table_transfer2.setRowHeight(25);
    rowHeader.setFixedCellHeight(table_transfer2.getRowHeight());
    //    + table.getRowMargin());
    //                           + table.getIntercellSpacing().height);
    rowHeader.setCellRenderer(new RowHeaderRenderer(table_transfer2));

    JScrollPane scroll = new JScrollPane(table_transfer2);
   scroll.setRowHeaderView(rowHeader);
    this.setLayout(new BorderLayout());
    this.add(scroll, BorderLayout.CENTER);
    
   bouton2_validation_transfer = new JButton("Validate data transfer");
   getContentPane().add(bouton2_validation_transfer, BorderLayout.SOUTH);

 
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
    table_transfer2.setDefaultRenderer(Object.class, new ColorRenderer());
    
  }
  
  
public class ColorRenderer extends DefaultTableCellRenderer {
 
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)  {
 
    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    Component c2 = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

    if(column==row) {
      c.setBackground(Color.LIGHT_GRAY);
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

