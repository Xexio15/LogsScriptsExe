import javax.swing.*;
import java.util.ArrayList;

public class DemoNetwork {
    String [] all_IPS = {"192.168.2.10", "192.168.2.11", "192.168.2.12", "192.168.1.10", "192.168.1.11", "192.168.1.12", "192.168.1.13", "192.168.1.14", "192.168.1.15", "192.168.1.20", "192.168.1.21", "192.168.1.22"};
    String [] linux_IPS = {"192.168.2.10", "192.168.2.11", "192.168.2.12", "192.168.1.10", "192.168.1.11", "192.168.1.12", "192.168.1.13", "192.168.1.14", "192.168.1.15"};
    String [] windows_IPS = {"192.168.1.20", "192.168.1.21", "192.168.1.22"};
    String database_IP = "192.168.0.10";
    Configuration conf = Configuration.getInstance();

    public DemoNetwork(){
    }

    /**
     * Launches the Demo
     * @param threads List of scripts threads
     * @param textArea1 TextArea to write
     */
    public void launchNetwork(ArrayList<LogExe> threads, JTextArea textArea1){
        launchRouter(threads, textArea1);
        launchASAFirewall(threads,textArea1);
        launchSQL(threads, textArea1);
        launchUFW(threads,textArea1);
        launchWindowsFirewall(threads,textArea1);
        launchSSH(threads,textArea1);

    }

    private void launchUFW(ArrayList<LogExe> threads, JTextArea textArea1){
        for (String ip : linux_IPS){
            LogExe p = new LogExe(conf.getLogScriptsPath()+"UFW.py "+ip, conf.getLogsPath());
            p.start();
            threads.add(p);
            textArea1.append("[Running] UFW.py Logs as "+ip+"\n");
        }
    }

    private void launchSSH(ArrayList<LogExe> threads, JTextArea textArea1){
        for (String ip : all_IPS){
            LogExe p = new LogExe(conf.getLogScriptsPath()+"ssh.py "+ip, conf.getLogsPath());
            p.start();
            threads.add(p);
            textArea1.append("[Running] ssh.py Logs as "+ip+"\n");
        }
    }

    private void launchASAFirewall(ArrayList<LogExe> threads, JTextArea textArea1){
        LogExe p = new LogExe(conf.getLogScriptsPath()+"asa_firewall.py", conf.getLogsPath());
        p.start();
        threads.add(p);
        textArea1.append("[Running] asa_firewall.py Logs\n");
    }

    private void launchRouter(ArrayList<LogExe> threads, JTextArea textArea1){
        LogExe p = new LogExe(conf.getLogScriptsPath()+"router.py", conf.getLogsPath());
        p.start();
        threads.add(p);
        textArea1.append("[Running] router.py Logs\n");
    }

    private void launchWindowsFirewall(ArrayList<LogExe> threads, JTextArea textArea1){
        for (String ip : windows_IPS){
            LogExe p = new LogExe(conf.getLogScriptsPath()+"windows_firewall.py "+ip, conf.getLogsPath());
            p.start();
            threads.add(p);
            textArea1.append("[Running] windows_firewall.py Logs as "+ip+"\n");
        }
    }

    private void launchSQL(ArrayList<LogExe> threads, JTextArea textArea1){
        LogExe p = new LogExe(conf.getLogScriptsPath()+"postgresql.py", conf.getLogsPath());
        p.start();
        threads.add(p);
        textArea1.append("[Running] postgresql.py Logs\n");
    }


}

