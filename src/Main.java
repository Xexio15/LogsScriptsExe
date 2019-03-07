import GUI.*;
import Utils.*;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main extends JPanel  {
    private JFrame frame = new JFrame();
    private JTabbedPane tabbedPane = new JTabbedPane();
    private Tab[] tabs = {new ScriptsView()};
    private Configuration conf = Configuration.getInstance();

    public Main() {
        Utils.loadConfFile();
        for (Tab t : tabs) {
            tabbedPane.add(t.getName(), t.getPanel());
        }
        frame.getContentPane().add(tabbedPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640, 480);
        frame.pack();
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
