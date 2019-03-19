package Alerts;

import GUI.AlertsView;

import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class AlertChecker extends Thread {
    private AlertQuery aq;

    public AlertChecker(AlertsView v){
        this.aq = new AlertQuery(v);
    }
    public void run() {
        while (true){
            try {
                aq.strangeLoginSSH();
                aq.portScanning();
                aq.arpPoisoning();
                aq.criticalURLs();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
