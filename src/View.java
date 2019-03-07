import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.Dimension;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

public class View {
    private JPanel mainPanel, logsPanel, alertsPanel;
    private JButton startLoggingButton, stopLoggingButton, demoButton, triggerAlertsButton;
    private JTextArea textArea1;
    private JComboBox logsCombo, alertsCombo;
    private JTextField logArguments, alertArguments;
    private JFrame frame;
    private ArrayList<LogExe> threads = new ArrayList<LogExe>();
    private Configuration conf = Configuration.getInstance();

    public View() {
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


        frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                int i=JOptionPane.showConfirmDialog(null, "Are you sure?");
                if(i == 0) {
                    for (LogExe l : threads) {
                        l.kill();
                    }
                    textArea1.setText("");
                }
            }
        });


    }



    public void start(){
        Utils.loadConfFile();
        frame = new JFrame("Logs");
        frame.setContentPane(new View().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setMinimumSize(new Dimension(470,250));
        frame.setMaximumSize(new Dimension(500,400));

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

