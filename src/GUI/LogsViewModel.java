package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.util.HashMap;

public class LogsViewModel extends DefaultTableModel {
    private HashMap hiddenColumns = new HashMap();
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public boolean isHidden(String key){
        return hiddenColumns.containsKey(findColumn(key));
    }

    public void hideColumn(JTable logs, String key){
        int vIndex = logs.convertColumnIndexToView(this.findColumn(key));
        TableColumnModel columnModel = logs.getColumnModel();
        TableColumn column = columnModel.getColumn(vIndex);
        columnModel.removeColumn(column);
        hiddenColumns.put(this.findColumn(key), column);

    }

    public void showColumn(JTable logs, String key) {
        TableColumn column = (TableColumn) hiddenColumns.remove(this.findColumn(key));
        TableColumnModel columnModel = logs.getColumnModel();
        columnModel.addColumn(column);
        final int addedViewIndex = columnModel.getColumnCount() - 1;
        columnModel.moveColumn(addedViewIndex, this.findColumn(key));
    }
}
