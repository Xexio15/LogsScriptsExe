package Alerts;

import GUI.DashboardView;

import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class AlertChecker extends Thread {
    private AlertQuery aq;

    public AlertChecker(DashboardView v){
        this.aq = new AlertQuery(v);
    }
    public void run() {
        while (true){
            try {
                aq.strangeLoginSSH();
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
