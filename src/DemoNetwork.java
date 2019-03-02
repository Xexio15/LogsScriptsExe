import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DemoNetwork {
    String [] all_IPS = {"192.168.2.10", "192.168.2.11", "192.168.2.12", "192.168.1.10", "192.168.1.11", "192.168.1.12", "192.168.1.13", "192.168.1.14", "192.168.1.15", "192.168.1.20", "192.168.1.21", "192.168.1.22"};
    String [] linux_IPS = {"192.168.2.10", "192.168.2.11", "192.168.2.12", "192.168.1.10", "192.168.1.11", "192.168.1.12", "192.168.1.13", "192.168.1.14", "192.168.1.15"};
    String [] windows_IPS = {"192.168.1.20", "192.168.1.21", "192.168.1.22"};
    String database_IP = "192.168.0.10";


    public DemoNetwork(){
    }

    public void launchNetwork(ArrayList<LogExe> threads, JTextArea textArea1){
        launchASAFirewall(threads,textArea1);
        launchUFW(threads,textArea1);
        launchSSH(threads,textArea1);

    }

    private void launchUFW(ArrayList<LogExe> threads, JTextArea textArea1){
        for (String ip : linux_IPS){
            LogExe p = new LogExe("./logs/UFW.py "+ip);
            p.start();
            threads.add(p);
            textArea1.append("[Running] UFW.py Logs as "+ip+"\n");
        }
    }

    private void launchSSH(ArrayList<LogExe> threads, JTextArea textArea1){
        for (String ip : all_IPS){
            LogExe p = new LogExe("./logs/ssh.py "+ip);
            p.start();
            threads.add(p);
            textArea1.append("[Running] ssh.py Logs as "+ip+"\n");
        }
    }

    private void launchASAFirewall(ArrayList<LogExe> threads, JTextArea textArea1){
        LogExe p = new LogExe("./logs/asa_firewall.py");
        p.start();
        threads.add(p);
        textArea1.append("[Running] asa_firewall.py Logs\n");
    }

    private void launchRouter(ArrayList<LogExe> threads, JTextArea textArea1){

    }

    private void launchWindowsFirewall(ArrayList<LogExe> threads, JTextArea textArea1){

    }

    private void launchSQL(ArrayList<LogExe> threads, JTextArea textArea1){

    }


}

