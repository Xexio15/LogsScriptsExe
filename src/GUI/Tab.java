package GUI;

import javax.swing.*;

public interface Tab {
    String getName();
    void close();
    JPanel getPanel();
}
