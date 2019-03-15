package Utils;

import java.io.File;
import java.io.IOException;

public class LogExe extends Thread {
    private Process pr;
    private String fileName, logDir;
    private Configuration conf = Configuration.getInstance();

    public  LogExe( String fileName, String logDir){
        this.fileName = fileName;
        this.logDir = logDir;
    }

    public void run() {
        Runtime rt = Runtime.getRuntime();
        try {
            //Runs the command "python <Path to the script>" in logDir Path
            pr = rt.exec(conf.getPython_exec() + " " + fileName, null, new File(logDir));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void kill(){
        pr.destroy();
    }

}
