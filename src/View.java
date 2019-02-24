import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;
import java.util.ArrayList;

public class View {
    private JPanel mainPanel;
    private JButton startLoggingButton;
    private JButton stopLoggingButton;
    private JButton triggerAlertsButton;
    private JTextArea textArea1;
    private JComboBox logsCombo;
    private JComboBox alertsCombo;
    private JTextField logArguments;
    private JTextField alertArguments;
    private JPanel logsPanel;
    private JPanel alertsPanel;
    ArrayList<LogExe> threads = new ArrayList<LogExe>();

    public View() {
        startLoggingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LogExe p = new LogExe("./logs/"+(String)logsCombo.getSelectedItem()+" "+logArguments.getText());
                p.start();
                threads.add(p);
                textArea1.append("[Running] "+(logsCombo.getSelectedItem() +" Logs as "+logArguments.getText()+"\n"));
            }
        });

        triggerAlertsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LogExe p = new LogExe("./alerts/"+(String)alertsCombo.getSelectedItem()+" "+alertArguments.getText());
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

    public static void main(String[] args) {
        JFrame frame = new JFrame("Logs");
        frame.setContentPane(new View().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setMinimumSize(new Dimension(470,250));
        frame.setMaximumSize(new Dimension(500,400));


    }

    public void update(){
        try {
            Utils.updateComboBox(logsCombo, "./logs/");
            Utils.updateComboBox(alertsCombo, "./alerts/");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

