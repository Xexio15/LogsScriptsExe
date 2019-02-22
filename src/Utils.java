import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.*;

public final class Utils {
    public static void updateComboBox(JComboBox combo, String path) throws IOException {
        combo.removeAllItems();
        try (DirectoryStream<Path> stream = (DirectoryStream<Path>) Files.newDirectoryStream(Paths.get(path), "*.py")) {
            for (Path entry: stream) {
                combo.addItem(entry.getFileName().toString());
            }
        } catch (DirectoryIteratorException ex) {
            // I/O error encounted during the iteration, the cause is an IOException
            throw ex.getCause();
        }
    }
}
