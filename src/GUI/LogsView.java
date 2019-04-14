package GUI;

import Alerts.Logger;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class LogsView  implements Tab, Observer {
    private JPanel panel1;
    private JTable logs;
    private String name = "Logs";
    private DefaultTableModel model;
    public LogsView(){
        new Thread(new Logger(this)).start();
         model = new DefaultTableModel() { //rows, fields.toArray()
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
         logs.setModel(model);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void close() {

    }

    @Override
    public JPanel getPanel() {
        return panel1;
    }

    @Override
    public void update(Observable observable, Object o) {
        ArrayList<Map<String,Object>> map = (ArrayList<Map<String,Object>>) o;
        if(map.size() > 0) {
            for (String key : map.get(0).keySet()) {
                if (model.findColumn(key) == -1) {
                    model.addColumn(key);

                }
            }
            for (int j = 0; j < map.size(); j++) {
                Map<String, Object> m = map.get(j);
                String[] row = new String[model.getColumnCount()];
                for (int i = 0; i < model.getColumnCount(); i++) {
                    if(m.containsKey(model.getColumnName(i))) {
                        row[i] = (m.get(model.getColumnName(i)).toString());
                    }else{
                        row[i] = "-";
                    }

                }
                model.insertRow(0, row);
            }
        }
        logs.updateUI();
        map.clear();
    }
}
