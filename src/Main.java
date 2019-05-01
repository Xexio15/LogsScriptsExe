import GUI.*;
import Utils.Configuration;
import Utils.Utils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main extends JPanel  {
    private JFrame frame = new JFrame();
    private JTabbedPane tabbedPane = new JTabbedPane();
    private Tab[] tabs;
    private Configuration conf = Configuration.getInstance();

    public Main() {
        tabs = new Tab[4];
        tabs[0] = new ScriptsView();
        ChartsView c = new ChartsView();
        tabs[1] = new AlertsView(c);
        tabs[2] = new LogsView();
        tabs[3] = c;

        //Load Configutarion File
        Utils.loadConfFile();

        //Add all the tabs to the tab panel
        for (Tab t : tabs) {
            tabbedPane.add(t.getName(), t.getPanel());
        }
        tabbedPane.addChangeListener(new ChangeListener() { //add the Listener

            public void stateChanged(ChangeEvent e) {

                if(tabbedPane.getSelectedIndex() == 1) //Index starts at 0, so Index 2 = Tab3
                {
                    tabbedPane.setTitleAt(1,"Alerts");
                    ((AlertsView)tabs[1]).resetCount();
                }
            }
        });
        //Add tab panel to the main panel
        frame.getContentPane().add(tabbedPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1280, 720);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            } catch (InstantiationException e1) {
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            } catch (UnsupportedLookAndFeelException e1) {
                e1.printStackTrace();
            }
        }


        frame.addWindowListener(new WindowAdapter(){
            /**
             * Shows a dialog when the program is closing
             * @param e
             */
            public void windowClosing(WindowEvent e){
                int op = JOptionPane.showConfirmDialog(null, "Are you sure?");
                if(op == 0) {
                    for(Tab t : tabs){
                        t.close();
                    }
                }
            }
        });

    }
    public static void main(String[] args) {

        Main m = new Main();
    }
}
