import javax.swing.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public final class Utils {
    /**
     * Updates combo boxes items
     * @param combo Component to update
     * @param path Path where scripts are
     * @throws IOException
     */
    public static void updateComboBox(JComboBox combo, String path) throws IOException {
        combo.removeAllItems();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(path), "*.py")) {
            for (Path entry: stream) {
                combo.addItem(entry.getFileName().toString());
            }
        } catch (DirectoryIteratorException ex) {
            // I/O error encounted during the iteration, the cause is an IOException
            throw ex.getCause();
        }
    }

    /**
     * Loads SIEM.conf file and saves the current configuration as a Configuration object
     */
    public static void loadConfFile(){
        Configuration conf = Configuration.getInstance();
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get("SIEM.conf"));

            for(String line : lines){
                if(line.startsWith("#") || line.equals("") || line.equals("\n")){
                }
                else{

                    line = line.replaceAll("\\\\","/");
                    String[] sep = line.split(":",2);
                    String attribute = sep[0];
                    attribute = attribute.replaceAll(" ","");
                    while (sep[1].startsWith(" ")){
                        sep[1] = sep[1].replaceFirst(" ", "");
                    }
                    String value = Paths.get(sep[1]).toString();

                    if (attribute.equals("logsPath")){
                        conf.setLogsPath(value);
                    }
                    else if(attribute.equals("print")){
                        //
                    }else if(attribute.equals("logScriptsPath")){
                        if(value.equals(".")){
                            String test = System.getProperty("user.dir");
                            conf.setLogScriptsPath(System.getProperty("user.dir").replaceAll("\\\\","/")+"/logs/");
                        }
                        else{
                            conf.setLogScriptsPath(value);
                        }
                    }else if(attribute.equals("alertScriptsPath")){
                        if(value.equals(".")){
                            conf.setAlertScriptsPath(System.getProperty("user.dir").replaceAll("\\\\","/")+"/alerts/");

                        }else{
                            conf.setAlertScriptsPath(value);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
