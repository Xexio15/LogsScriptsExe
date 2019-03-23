package Alerts;

import GUI.AlertsView;

import java.util.concurrent.TimeUnit;

public class AlertChecker extends Thread {
    private AlertsView v;
    public AlertChecker(AlertsView v){
        this.v = v;
    }
    public void run() {
        while (true){
            try {
                long startTime = System.currentTimeMillis();
                new Thread(new AlertQuery(v, AlertQuery.QUERY_TYPE.SSH_STRANGE_LOGIN)).start();
                TimeUnit.MILLISECONDS.sleep(500);

                new Thread(new AlertQuery(v, AlertQuery.QUERY_TYPE.PORT_SCANNING)).start();
                TimeUnit.MILLISECONDS.sleep(500);

                new Thread(new AlertQuery(v, AlertQuery.QUERY_TYPE.ARP_POISONING)).start();
                TimeUnit.MILLISECONDS.sleep(500);

                new Thread(new AlertQuery(v, AlertQuery.QUERY_TYPE.CRITICAL_URLS)).start();
                TimeUnit.MILLISECONDS.sleep(500);

                new Thread(new AlertQuery(v, AlertQuery.QUERY_TYPE.SSH_BRUTE_FORCE)).start();
                TimeUnit.MILLISECONDS.sleep(500);

                new Thread(new AlertQuery(v, AlertQuery.QUERY_TYPE.SOFTWARE_FIREWALL_DROPS)).start();
                long stopTime = System.currentTimeMillis();
                long elapsedTime = stopTime - startTime;
                System.out.println(elapsedTime);

                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
