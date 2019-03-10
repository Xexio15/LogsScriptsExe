package GUI;

import javax.swing.*;

public interface Tab {
    /**
     *
     * @return The name of the tab
     */
    String getName();

    /**
     * Realizes an action when closing the application if necessary
     */
    void close();

    /**
     *
     * @return The main panel of the tab
     */
    JPanel getPanel();
}
