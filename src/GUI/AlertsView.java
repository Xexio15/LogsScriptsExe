package GUI;

import Alerts.AlertChecker;
import Alerts.AlertObject;
import Utils.Utils;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class AlertsView implements Tab, Observer {
    private JPanel mainPanel;
    private JList alertsList;
    private JTextArea alertsText;
    private JButton clearBtn;
    private JButton exportButton;
    private String name = "Alerts";
    private DefaultListModel<AlertObject> model;
    private int newAlerts = 0;
    private ChartsView c;


    public AlertsView(ChartsView c){
        this.c = c;

        new AlertChecker(this).start();

        model = (DefaultListModel<AlertObject>) Utils.loadSerializable("alerts.lst");
        if (model == null){
            model = new DefaultListModel();
        }

        this.initAlertsList();
        this.initExportButton();

        clearBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                model.clear();
            }
        });


        c.updateCharts(alertsList);
    }

    private void initAlertsList(){
        alertsList.setModel(model);
        alertsList.setCellRenderer(new SeverityRenderer());
        alertsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    AlertObject a = (AlertObject)(alertsList.getModel().getElementAt(alertsList.getSelectedIndex()));
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

        alertsList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                AlertObject a = (AlertObject) alertsList.getModel().getElementAt(alertsList.getSelectedIndex());
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

    private void initExportButton(){
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

                int returnValue = jfc.showOpenDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = jfc.getSelectedFile();
                    System.out.println(selectedFile.getName());
                    AlertObject a = (AlertObject) alertsList.getModel().getElementAt(alertsList.getSelectedIndex());

                    Document document = new Document();
                    try {
                        @SuppressWarnings("unused")
                        PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(selectedFile.getAbsolutePath()+".pdf"));
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    document.open();

                    Paragraph paragraph = new Paragraph();
                    paragraph.add("SIEM Alert Report\n\n");
                    paragraph.add(a.fullMessage);
                    paragraph.add("===============================================================\n\n");
                    paragraph.add("Logs:\n\n");
                    paragraph.add(a.logs);

                    try {
                        document.add(paragraph);
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                    document.close();
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
        c.updateCharts(alertsList);
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

    public JList getAlertsList(){
        return alertsList;
    }

}
