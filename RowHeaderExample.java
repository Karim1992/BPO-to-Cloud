package test1;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author zarour
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.AbstractCellEditor;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * @version 1.0 11/09/98
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

public class RowHeaderExample extends JFrame {
    public static JTable table;
    public static DefaultTableModel dm;
    public static JButton bouton_criteria;
  public RowHeaderExample() {
    super("Row Header Example");
    setSize(500, 350);

    ListModel lm = new AbstractListModel() {
      String headers[] = { "Security", "Compliance", "Cost", "Performance" };
      public int getSize() {
        return headers.length;
      }

      public Object getElementAt(int index) {
        return headers[index];
      }
    };

    dm = new DefaultTableModel(lm.getSize(), 4) {
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
    
    table = new JTable(dm);
 


      
    
    //table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    String titres_colonnes[] = { "Security", "Compliance", "Cost", "Performance" };
    dm.setColumnIdentifiers(titres_colonnes);
    JList rowHeader = new JList(lm);
    rowHeader.setFixedCellWidth(90);
    table.setRowHeight(20);
    rowHeader.setFixedCellHeight(table.getRowHeight());
    //    + table.getRowMargin());
    //                           + table.getIntercellSpacing().height);
    rowHeader.setCellRenderer(new RowHeaderRenderer(table));

    JScrollPane scroll = new JScrollPane(table);
   scroll.setRowHeaderView(rowHeader);
    
    getContentPane().add(scroll, BorderLayout.CENTER);
    
    bouton_criteria = new JButton("Validate the comparison weights");
    getContentPane().add(bouton_criteria, BorderLayout.SOUTH);

 
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
    table.setDefaultRenderer(Object.class, new ColorRenderer());
    
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
    RowHeaderExample frame = new RowHeaderExample();
    frame.setLocation(500, 400);
    frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });
    frame.setVisible(true);
  }
*/
}