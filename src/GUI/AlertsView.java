package GUI;

import Alerts.AlertChecker;
import Alerts.AlertObject;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;

public class AlertsView implements Tab, Observer {
    private JPanel mainPanel;
    private JList list1;
    private JTextArea alertsText;
    private String name = "Alerts";
    private DefaultListModel<AlertObject> model;

    public AlertsView(){
        new AlertChecker(this).start();
        model = new DefaultListModel();
        list1.setModel(model);
        list1.setCellRenderer(new SeverityRenderer());
        list1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    AlertObject a = (AlertObject)(list1.getModel().getElementAt(list1.getSelectedIndex()));
                    JOptionPane.showMessageDialog(null, a.fullMessage);
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
        return mainPanel;
    }

    @Override
    public void update(Observable observable, Object o) {
        AlertObject alert = (AlertObject) o;
        model.addElement(alert);
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
