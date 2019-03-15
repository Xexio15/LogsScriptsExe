package GUI;

import javax.swing.*;

public class DashboardView implements Tab {
    private JPanel mainPanel;
    private JList list1;
    private String name = "Dashboard";


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
}
