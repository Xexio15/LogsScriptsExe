package Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class LogExe extends Thread {
    private Process pr;
    private String fileName;
    private String logDir;

    public  LogExe( String fileName, String logDir){
        this.fileName = fileName;
        this.logDir = logDir;
    }

    public void run() {
        Runtime rt = Runtime.getRuntime();
        try {
            pr = rt.exec("python "+fileName, null, new File(Paths.get(logDir).toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void kill(){
        pr.destroy();
    }

}
