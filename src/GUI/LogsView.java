package GUI;

import Alerts.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class LogsView  implements Tab, Observer {
    private JPanel panel1;
    private JTable logs;
    private JComboBox timeCombo;
    private JList columnList;
    private JButton showHideButton;
    private String name = "Logs";
    private LogsViewModel model;
    private DefaultListModel<String> columnsModel;
    private Logger logsThread;
    private Thread t;
    private LogsView c = this;
    public LogsView(){
         logsThread = new Logger(this, 5);
         t = new Thread(logsThread);
         t.start();
         model = new LogsViewModel();
         logs.setModel(model);

         columnsModel = new DefaultListModel();
         columnList.setModel(columnsModel);
         columnList.setCellRenderer(new ColumnsListRenderer());

         timeCombo.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent actionEvent) {
                 logsThread.stopTask();
                 if(!t.isAlive()){
                     logsThread = new Logger(c, Integer.parseInt((String) timeCombo.getItemAt(timeCombo.getSelectedIndex())));
                     t = new Thread(logsThread);
                     t.start();
                 }
             }
         });

         showHideButton.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent actionEvent) {
                 if(!columnList.isSelectionEmpty()) {
                     String column = columnsModel.get(columnList.getSelectedIndex());
                     if (model.isHidden(column)) {
                         model.showColumn(logs, column);
                     } else {
                         model.hideColumn(logs, column);
                     }
                     ((ColumnsListRenderer)columnList.getCellRenderer()).changeState();
                     columnList.updateUI();
                 }

             }
         });
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
                if (model.findColumn(key) == -1 && !model.isHidden(key)) {
                    model.addColumn(key);
                    columnsModel.addElement(key);
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
        logs.repaint();
        map.clear();
    }

    public class ColumnsListRenderer extends DefaultListCellRenderer {
        private boolean buttonClicked = false;

        public void changeState(){
            buttonClicked = true;
        }
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (isSelected && buttonClicked) {
                c.setEnabled(!c.isEnabled());
                //c.setBackground(Color.GRAY);
                buttonClicked = false;
            }
            else {
                c.setEnabled(c.isEnabled());
            }

            return c;
        }
    }

}
