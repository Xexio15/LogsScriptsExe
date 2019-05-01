package Alerts;

import GUI.AlertsView;

import java.util.Timer;
import java.util.TimerTask;

public class AlertChecker extends Thread {
    private AlertsView v;
    public AlertChecker(AlertsView v){
        this.v = v;
    }
    public void run() {

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                new Thread(new AlertQuery(v, AlertQuery.QUERY_TYPE.SSH_STRANGE_LOGIN)).start();

                new Thread(new AlertQuery(v, AlertQuery.QUERY_TYPE.PORT_SCANNING)).start();

                new Thread(new AlertQuery(v, AlertQuery.QUERY_TYPE.ARP_POISONING)).start();

                new Thread(new AlertQuery(v, AlertQuery.QUERY_TYPE.CRITICAL_URLS)).start();

                new Thread(new AlertQuery(v, AlertQuery.QUERY_TYPE.SSH_BRUTE_FORCE)).start();

                new Thread(new AlertQuery(v, AlertQuery.QUERY_TYPE.SOFTWARE_FIREWALL_DROPS)).start();
                long stopTime = System.currentTimeMillis();
                long elapsedTime = stopTime - startTime;
                System.out.println(elapsedTime);
            }
        };
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task,0,15000);

    }
}
