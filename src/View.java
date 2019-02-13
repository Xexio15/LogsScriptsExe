import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class View {
    private JPanel mainPanel;
    private JCheckBox checkBox1;
    private JButton startLoggingButton;
    private JButton stopLoggingButton;
    private JButton triggerAlertsButton;
    private JTextArea textArea1;
    private JPanel logsPanel;
    private JPanel alertsPanel;
    ArrayList<LogExe> threads = new ArrayList<LogExe>();

    public View() {
        startLoggingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Component c : logsPanel.getComponents()) {
                    if (((JCheckBox)c).isSelected()) {
                        LogExe p = new LogExe(((JCheckBox)c).getText());
                        p.start();
                        threads.add(p);
                        textArea1.append("[Running] "+((JCheckBox)c).getText()+" Logs\n");
                    }
                }
            }
        });

        triggerAlertsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Component c : alertsPanel.getComponents()) {
                    if (((JCheckBox)c).isSelected()) {
                        LogExe p = new LogExe(((JCheckBox)c).getText());
                        p.start();
                    }
                }
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
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Logs");
        frame.setContentPane(new View().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setMinimumSize(new Dimension(470,385));
        frame.setMaximumSize(new Dimension(500,400));
    }


}

