package GUI;

import Alerts.AlertChecker;
import Alerts.AlertObject;
import Utils.Utils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class AlertsView implements Tab, Observer {
    private JPanel mainPanel;
    private JList list1;
    private JTextArea alertsText;
    private JButton clearBtn;
    private String name = "Alerts";
    private DefaultListModel<AlertObject> model;
    private int newAlerts = 0;

    public AlertsView(){
        new AlertChecker(this).start();

        model = (DefaultListModel<AlertObject>) Utils.loadSerializable("alerts.lst");
        if (model == null){
            model = new DefaultListModel();
        }

        list1.setModel(model);
        list1.setCellRenderer(new SeverityRenderer());
        list1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    AlertObject a = (AlertObject)(list1.getModel().getElementAt(list1.getSelectedIndex()));
                    ArrayList<String> fields = new ArrayList();
                    ArrayList<Map<String,Object>> map = a.getMap();
                    String [][] rows = new String [map.size()][];
                    String [] messages = a.logsMessages;

                    for (String key : map.get(0).keySet()){
                        fields.add(key);
                    }

                    for (int j = 0; j < map.size(); j++){
                        Map<String,Object> m = map.get(j);
                        String[] row = new String[m.size()];
                        for (int i = 0; i < fields.size(); i++){
                           row[i] = (m.get(fields.get(i)).toString());
                        }
                        rows[j] = row;
                    }

                    TableModel model = new LogsTableModel(rows, fields.toArray(), messages);

                    JTable table = new JTable(model);
                    table.removeColumn(table.getColumn("m"));
                    table.setAutoCreateRowSorter(true);
                    JTextArea m = new JTextArea();
                    table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
                        public void valueChanged(ListSelectionEvent event) {
                            int log = table.convertRowIndexToModel(table.getSelectedRow());
                            m.setText("");
                            m.setText(((LogsTableModel)table.getModel()).getRowMessage(log));
                        }
                    });
                    JPanel pan=new JPanel();
                    JPanel mpan=new JPanel();
                    JSplitPane js = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
                    pan.setLayout(new BorderLayout());
                    mpan.setLayout(new BorderLayout());

                    mpan.add(new JScrollPane(m));
                    m.setEditable(false);
                    pan.add(new JScrollPane(table));

                    js.add(pan);
                    js.add(mpan);
                    js.setDividerLocation(0.3);
                    js.setDividerSize(5);

                    Dialog jd = new JDialog();
                    jd.add(js);
                    jd.pack();
                    jd.setVisible(true);
                }
            }
        });

        list1.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                AlertObject a = (AlertObject)list1.getModel().getElementAt(list1.getSelectedIndex());
                alertsText.setText("");
                alertsText.setText(a.fullMessage+"\n");
                if(!a.logs.equals("")) {
                    alertsText.append("===============================================================\n\n");
                    alertsText.append("Logs:\n\n");
                    alertsText.append(a.logs);
                }
            }
        });

        clearBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                model.clear();
            }
        });
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void close() {
        Utils.saveSerializable(model, "alerts.lst");
    }

    @Override
    public JPanel getPanel() {
        return mainPanel;
    }

    @Override
    public void update(Observable observable, Object o) {
        AlertObject alert = (AlertObject) o;
        model.insertElementAt(alert,0);
        if(((JTabbedPane)mainPanel.getParent()).getSelectedIndex() != 1) {
            newAlerts++;
            ((JTabbedPane) mainPanel.getParent()).setTitleAt(1, this.name + " (" + newAlerts + ")");
        }
    }

    public void resetCount(){
        this.newAlerts = 0;
    }

    private static class SeverityRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
            Component c = super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
            if ( ((AlertObject)value).severity == 1 ) {
                c.setBackground( Color.yellow );
            }
            else if  (((AlertObject)value).severity == 2 ) {
                c.setBackground( Color.orange );
            }
            else if  (((AlertObject)value).severity == 3 ) {
                c.setBackground( Color.red );
            }
            else {
                c.setBackground( Color.white );
            }
            return c;
        }
    }

}
