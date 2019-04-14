package GUI;

import javax.swing.table.DefaultTableModel;
import java.util.Vector;

public class LogsTableModel extends DefaultTableModel {
    private String[] messages;
    public LogsTableModel(Object [][] logs, Object [] cols, String[] messages){
        super(logs, cols);
        super.addColumn("m",messages);
    }
    public String getRowMessage(int i){
        if(i != -1) return (String) ((Vector)super.dataVector.get(i)).get(getColumnCount()-1);
        return null;
    }


    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
