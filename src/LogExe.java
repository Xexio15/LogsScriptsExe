import com.sun.xml.internal.ws.api.message.ExceptionHasMessage;

import java.io.IOException;

public class LogExe extends Thread {
    private Process pr;
    private String fileName;

    public  LogExe( String fileName){
        this.fileName = fileName;
    }
    public void run() {
        Runtime rt = Runtime.getRuntime();
        try {
            pr = rt.exec("python " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void kill(){
        pr.destroy();
    }

}
