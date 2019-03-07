package GUI;
import Utils.*;
import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

public class ScriptsView implements Tab{
    private JPanel mainPanel, logsPanel, alertsPanel;
    private JButton startLoggingButton, stopLoggingButton, demoButton, triggerAlertsButton;
    private JTextArea textArea1;
    private JComboBox logsCombo, alertsCombo;
    private JTextField logArguments, alertArguments;
    private JFrame frame;
    private ArrayList<LogExe> threads = new ArrayList<LogExe>();
    private Configuration conf = Configuration.getInstance();
    public String name = "Scripts";

    public ScriptsView() {

        startLoggingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LogExe p = new LogExe(conf.getLogScriptsPath()+logsCombo.getSelectedItem()+" "+logArguments.getText(), conf.getLogsPath());
                p.start();
                threads.add(p);
                textArea1.append("[Running] "+(logsCombo.getSelectedItem() +" Logs as "+logArguments.getText()+"\n"));
            }
        });

        triggerAlertsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LogExe p = new LogExe(conf.getAlertScriptsPath()+alertsCombo.getSelectedItem()+" "+alertArguments.getText(), conf.getLogsPath());
                p.start();
            }
        });

        stopLoggingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (LogExe l : threads){
                    l.kill();
                }
                textArea1.setText("");
            }
        });

        demoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DemoNetwork demo = new DemoNetwork();
                demo.launchNetwork(threads,textArea1);
            }
        });


        logsCombo.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                update();
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) { }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {}
        });

        alertsCombo.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                update();
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) { }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {}
        });





    }

    public JPanel getPanel(){
        return mainPanel;
    }

    public String getName(){
        return name;
    }

    public void close(){
        for (LogExe l : threads) {
            l.kill();
        }
        textArea1.setText("");
    }

    public void update(){
        try {
            Utils.updateComboBox(logsCombo, conf.getLogScriptsPath());
            Utils.updateComboBox(alertsCombo, conf.getAlertScriptsPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

