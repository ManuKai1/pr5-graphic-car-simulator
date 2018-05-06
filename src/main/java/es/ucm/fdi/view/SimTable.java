package es.ucm.fdi.view;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import es.ucm.fdi.model.SimObj.SimObject;
import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.util.Describable;
import es.ucm.fdi.util.TableDataType;

@SuppressWarnings("serial")
public class SimTable extends JPanel {

    private JTable table;
    private List<TableDataType> headers;
    private List<? extends Describable> tableElements;
    private ListOfMapsTableModel model;
    

    private class ListOfMapsTableModel extends AbstractTableModel {
        
        public Map<TableDataType, Object> elementData = new HashMap<>();
        public Map<Integer, Boolean> reportChecks = new HashMap<>();


        @Override
        public String getColumnName(int columnIndex) {
            return  headers.get(columnIndex).toString();
        }

        @Override
        public int getRowCount() {
            return  tableElements.size();
        }

        @Override
        public int getColumnCount() {
            return headers.size();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            
            // Caso de la numeración de la tabla de eventos.
            if ( headers.get(columnIndex) == TableDataType.E_NUM ) {
                return  Integer.toString(rowIndex + 1);
            }

            // Caso de los checks para acotar reports.
            if ( headers.get(columnIndex) == TableDataType.REPORT ) {
                Boolean check = reportChecks.get(rowIndex);

                if (check == null) {
                    return true;
                }
                else {
                    return check;
                }
            }
            
            // Caso normal.
            tableElements.get(rowIndex).describe(elementData);

            return  elementData.get(headers.get(columnIndex));
        }

        /**
         * {@inheritDoc}
         * Overriden so that the JTable renders Booleans as
         * JCheckBoxes.
         */
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (headers.get(columnIndex) == TableDataType.REPORT) {
                return Boolean.class;
            }
            else {
                return String.class;
            }
        }

        /**
         * {@inheritDoc}
         * Overriden so that JCheckBoxex can be edited.
         */
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            if ( headers.get(columnIndex) == TableDataType.REPORT ) {
                return true;
            }
            else {
                return false;
            }
        }

        /**
         * {@inheritDoc}
         * Overriden so that JCheckBoxes can be (un)checked.
         * Only checkBoxes can be set.
         */
        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if ( headers.get(columnIndex) == TableDataType.REPORT ) {
                reportChecks.put(rowIndex, (Boolean) aValue);
            }
        }

        public Map<Integer, Boolean> getChecks() {
            return reportChecks;
        }
        
    }

    public SimTable(TableDataType[] head, List<? extends Describable> elements) {
        super( new BorderLayout() );
        
        headers = new ArrayList<>(Arrays.asList(head));
        tableElements = elements;
        model = new ListOfMapsTableModel();

        // Se crea la tabla basada en el modelo.
        table = new JTable(model);
        table.setRowSelectionAllowed(false);

        // Se añade la tabla al panel
        this.add( new JScrollPane(table,
        		JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED)
        );   
    }

    private void update() {
        model.fireTableDataChanged();
    }

    public void resetTable(TableDataType[] newHeaders, List<? extends Describable> newElements) {
        headers = new ArrayList<>(Arrays.asList(newHeaders));
        tableElements = newElements;

        // Limpieza del elemento de mapa.
        model.elementData.clear();

        // Actualización de la tabla.
        update();
    }

    public List<? extends Describable> getTableElements() {
        return tableElements;
    }
    
    public void setList(List<? extends Describable> newList){
        tableElements = newList;
        update();
    }
    
    public void clear(){
    	tableElements.clear();
    	update();
    }

    public void updateList(int minTime) {
        Iterator<? extends Describable> iter = tableElements.listIterator();
        
        while ( iter.hasNext() ) {
            Event e = (Event) iter.next();
            if (e.getTime() < minTime) {
                iter.remove();
            }
        }
    }

    public List<SimObject> getSelected() {
        List<SimObject> selected = new ArrayList<>();
        
        int columnIndex = headers.indexOf(TableDataType.REPORT);

        for (int row = 0; row < tableElements.size(); ++row) {
            if ( (Boolean) model.getValueAt(row, columnIndex) ) {
                SimObject d = (SimObject) tableElements.get(row);
                selected.add(d);
            }
        }
        
        return selected;
    }
}
