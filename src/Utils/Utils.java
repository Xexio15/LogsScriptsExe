package Utils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;

public final class Utils {
    public static final String[] ALL_IPS = {"192.168.2.10", "192.168.2.11", "192.168.2.12", "192.168.1.10", "192.168.1.11", "192.168.1.12", "192.168.1.13", "192.168.1.14", "192.168.1.15", "192.168.1.20", "192.168.1.21", "192.168.1.22"};
    public static final String [] LINUX_IPS = {"192.168.2.10", "192.168.2.11", "192.168.2.12", "192.168.1.10", "192.168.1.11", "192.168.1.12", "192.168.1.13", "192.168.1.14", "192.168.1.15"};
    public static final String [] WINDOWS_IPS = {"192.168.1.20", "192.168.1.21", "192.168.1.22"};
    public static final String DATABASE_IP = "192.168.0.10";

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
            throw ex.getCause();
        }
    }

    /**
     * Loads SIEM.conf file and saves the current configuration as a Utils.Configuration object
     */
    public static void loadConfFile(){
        Configuration conf = Configuration.getInstance();
        List<String> lines;
        try {
            if (!Files.exists(Paths.get("SIEM.conf"))){
                generateConfFile();
            }

            if (!Files.exists(Paths.get("/logs")) || !Files.exists(Paths.get("/alerts")) || !Files.exists(Paths.get("/generated_logs"))){
                createDirectories();
            }

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

                    String value = sep[1];

                    //Where logs will be written (URI)
                    if (attribute.equals("logsPath")){
                        if(value.equals(".")){
                            conf.setLogsPath(System.getProperty("user.dir").replaceAll("\\\\","/") + "/generated_logs/");
                        }else {
                            conf.setLogsPath(Paths.get(value).toString());
                        }
                    }

                    else if(attribute.equals("print")){
                        //
                    }

                    //WHERE logs scripts are located (URI)
                    else if(attribute.equals("logScriptsPath")){
                        //If value = . then default URI
                        if(value.equals(".")){
                            conf.setLogScriptsPath(System.getProperty("user.dir").replaceAll("\\\\","/") + "/logs/");
                        }

                        else{
                            if ((System.getProperty("os.name").toLowerCase().startsWith("windows"))){
                                conf.setLogScriptsPath((Paths.get(value).toString() + "\\"));
                            }else{
                                conf.setLogScriptsPath((Paths.get(value).toString() + "/"));
                            }
                        }
                    }

                    //Where alerts scripts are located (URI)
                    else if(attribute.equals("alertScriptsPath")){
                        if(value.equals(".")){
                            conf.setAlertScriptsPath(System.getProperty("user.dir").replaceAll("\\\\","/") + "/alerts/");

                        }else{
                            if ((System.getProperty("os.name").toLowerCase().startsWith("windows"))){
                                conf.setAlertScriptsPath((Paths.get(value).toString() + "\\"));
                            }else{
                                conf.setAlertScriptsPath((Paths.get(value).toString() + "/"));
                            }
                        }
                    }

                    else if(attribute.equals("python-exec")){
                        conf.setPython_exec(value);
                    }

                    else if (attribute.equals("urlBlacklist")){
                        conf.setBlacklist(listParser(value));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static String listParser(String list){
        list = list.replaceAll("\\[", "");
        list = list.replaceAll("]", "");
        list = list.replaceAll(",","");
        return list;
    }

    private static void generateConfFile(){
        List<String> lines = Arrays.asList(
                "#Evade spaces at the end of a line",
                "#All must be full Paths",
                "#A . determines that will use the current path",
                "#Every Path to a folder must exists, the program will not create any folder",
                "",
                "#Path where logs are written Linux logs: /var/log/test",
                "#Default: ./generated_logs",
                "logsPath : .",
                "",
                "#Path where log scripts are",
                "#Default: ./logs",
                "logScriptsPath : .",
                "",
                "#Path where alert scripts are",
                "#Default ./alerts",
                "alertScriptsPath : .",
                "",
                "#Python executable",
                "python-exec : python",
                "",
                "#URLs Blacklist with this format [url1, url2,...]",
                "urlBlacklist : []"
        );

        Path file = Paths.get("SIEM.conf");

        try {
            Files.write(file, lines, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createDirectories(){
        if (!Files.exists(Paths.get("/logs"))){
            new File("./logs").mkdirs();
        }
        if (!Files.exists(Paths.get("/alerts"))){
            new File("./alerts").mkdirs();
        }
        if (!Files.exists(Paths.get("/generated_logs"))){
            new File("./generated_logs").mkdirs();
        }
    }
}
