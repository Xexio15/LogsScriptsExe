import GUI.AlertsView;
import GUI.ScriptsView;
import GUI.Tab;
import Utils.Configuration;
import Utils.Utils;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main extends JPanel  {
    private JFrame frame = new JFrame();
    private JTabbedPane tabbedPane = new JTabbedPane();
    private Tab[] tabs = {new ScriptsView(), new AlertsView()};
    private Configuration conf = Configuration.getInstance();

    public Main() {
        //Load Configutarion File
        Utils.loadConfFile();

        //Add all the tabs to the tab panel
        for (Tab t : tabs) {
            tabbedPane.add(t.getName(), t.getPanel());
        }

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
            e.printStackTrace();
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
