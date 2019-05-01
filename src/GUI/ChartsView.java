package GUI;

import Alerts.AlertObject;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

public class ChartsView implements Tab{
    private JPanel mainPanel;
    private JScrollPane js;
    private CategoryChart alertsByIP;
    private CategoryChart alertsByDestinyIP;
    private CategoryChart alertsByLocalIPs;
    private PieChart criticalURLs;
    public ChartsView(){
        //js.setLayout(new GridLayout(0,2));
        this.mainPanel.setLayout(new GridLayout(1,1));
        ArrayList initX = new ArrayList();
        initX.add("-");
        ArrayList initY = new ArrayList();
        initY.add(0);

        alertsByIP = new CategoryChartBuilder().width(800).height(600).title("Number of alerts by origin IP").xAxisTitle("IPs").yAxisTitle("Alerts").build();
        alertsByIP.addSeries("Number of alerts by IP", initX, initY);
        JPanel pnlChart = new XChartPanel(alertsByIP);
        alertsByIP.getStyler().setXAxisLabelRotation(45);
        alertsByIP.getStyler().setLegendVisible(false);

        alertsByDestinyIP = new CategoryChartBuilder().width(800).height(600).title("Number of alerts by destiny  IP").xAxisTitle("IPs").yAxisTitle("Alerts").build();
        alertsByDestinyIP.addSeries("Number of alerts by IP", initX, initY);
        JPanel pnlChart2 = new XChartPanel(alertsByDestinyIP);
        alertsByDestinyIP.getStyler().setXAxisLabelRotation(45);
        alertsByDestinyIP.getStyler().setLegendVisible(false);

        alertsByLocalIPs = new CategoryChartBuilder().width(800).height(600).title("Number of alerts by local IPs").xAxisTitle("IPs").yAxisTitle("Alerts").build();
        alertsByLocalIPs.addSeries("Number of alerts by IP", initX, initY);
        JPanel pnlChart3 = new XChartPanel(alertsByLocalIPs);
        alertsByLocalIPs.getStyler().setXAxisLabelRotation(45);
        alertsByLocalIPs.getStyler().setLegendVisible(false);

        criticalURLs = new PieChartBuilder().width(800).height(600).title("Most visited critical URLs").build();
        criticalURLs.getStyler().setLegendPosition(Styler.LegendPosition.InsideSE);
        JPanel pnlChart4 = new XChartPanel(criticalURLs);

        JPanel content = new JPanel();

        content.setLayout(new GridLayout(0,1));
        content.add(pnlChart);
        content.add(pnlChart2);
        content.add(pnlChart3);
        content.add(pnlChart4);

        js.setViewportView(content);
        //js.updateUI();
        mainPanel.validate();
    }

    @Override
    public String getName() {
        return "Charts";
    }

    @Override
    public void close() {

    }

    @Override
    public JPanel getPanel() {
        return mainPanel;
    }

    public void updateCharts(JList alerts){
        alertsByIP(alerts);
        alertsByDestinyIP(alerts);
        alertsByLocalIPs(alerts);
        criticalURLs(alerts);
    }

    private void alertsByDestinyIP(JList alerts){
        ArrayList ips = new ArrayList();
        ArrayList<Integer> count = new ArrayList();
        ArrayList err = new ArrayList();
        for (int i = 0; i < alerts.getModel().getSize(); i++){
            AlertObject a = (AlertObject) alerts.getModel().getElementAt(i);
            ArrayList<Map<String, Object>> m = a.getMap();
            if(m.get(0).containsKey("Destino")) {
                for (Map<String, Object> log : m){
                    if(!ips.contains(log.get("Destino"))) {
                        ips.add(log.get("Destino"));
                        count.add(1);
                        err.add(0);
                    }else{
                        int in = ips.indexOf(log.get("Destino"));
                        count.set(in, count.get(in)+1);
                    }

                }
            }

        }
        if(ips.size() == 0) ips.add("-");
        if(count.size() == 0) count.add(0);
        if(err.size() == 0) err.add(0);
        alertsByDestinyIP.updateCategorySeries("Number of alerts by IP", ips, count,err);
    }

    private void alertsByIP(JList alerts){
        ArrayList ips = new ArrayList();
        ArrayList<Integer> count = new ArrayList();
        ArrayList err = new ArrayList();
        for (int i = 0; i < alerts.getModel().getSize(); i++){
            AlertObject a = (AlertObject) alerts.getModel().getElementAt(i);
            ArrayList<Map<String, Object>> m = a.getMap();
            if(m.get(0).containsKey("Origen")) {
                for (Map<String, Object> log : m){
                    if(!ips.contains(log.get("Origen"))) {
                        ips.add(log.get("Origen"));
                        count.add(1);
                        err.add(0);
                    }else{
                        int in = ips.indexOf(log.get("Origen"));
                        count.set(in, count.get(in)+1);
                    }

                }
            }

        }
        if(ips.size() == 0) ips.add("-");
        if(count.size() == 0) count.add(0);
        if(err.size() == 0) err.add(0);
        alertsByIP.updateCategorySeries("Number of alerts by IP", ips, count,err);
    }

    private void alertsByLocalIPs(JList alerts){
        ArrayList ips = new ArrayList();
        ArrayList<Integer> count = new ArrayList();
        ArrayList err = new ArrayList();
        for (int i = 0; i < alerts.getModel().getSize(); i++){
            AlertObject a = (AlertObject) alerts.getModel().getElementAt(i);
            ArrayList<Map<String, Object>> m = a.getMap();
            if(m.get(0).containsKey("Origen") || m.get(0).containsKey("Destino")) {
                for (Map<String, Object> log : m){
                    if(log.containsKey("Origen")) {
                        if (((String)log.get("Origen")).startsWith("192.")) {
                            if (!ips.contains(log.get("Origen"))) {
                                ips.add(log.get("Origen"));
                                count.add(1);
                                err.add(0);
                            } else {
                                int in = ips.indexOf(log.get("Origen"));
                                count.set(in, count.get(in) + 1);
                            }
                        }
                    }

                    if(log.containsKey("Destino")) {
                        if (((String)log.get("Destino")).startsWith("192.")) {
                            if (!ips.contains(log.get("Destino"))) {
                                ips.add(log.get("Destino"));
                                count.add(1);
                                err.add(0);
                            } else {
                                int in = ips.indexOf(log.get("Destino"));
                                count.set(in, count.get(in) + 1);
                            }
                        }
                    }

                }
            }

        }
        if(ips.size() == 0) ips.add("-");
        if(count.size() == 0) count.add(0);
        if(err.size() == 0) err.add(0);
        alertsByLocalIPs.updateCategorySeries("Number of alerts by IP", ips, count,err);
    }

    private void criticalURLs(JList alerts){
        ArrayList urls = new ArrayList();
        ArrayList<Integer> count = new ArrayList();
        ArrayList err = new ArrayList();
        for (int i = 0; i < alerts.getModel().getSize(); i++){
            AlertObject a = (AlertObject) alerts.getModel().getElementAt(i);
            ArrayList<Map<String, Object>> m = a.getMap();
            if(m.get(0).containsKey("URL")) {
                for (Map<String, Object> log : m){
                    if(!urls.contains(log.get("URL"))) {
                        urls.add(log.get("URL"));
                        count.add(1);
                        err.add(0);
                    }else{
                        int in = urls.indexOf(log.get("URL"));
                        count.set(in, count.get(in)+1);
                    }

                }
            }

        }
        if(urls.size() == 0) urls.add("-");
        if(count.size() == 0) count.add(0);
        if(err.size() == 0) err.add(0);
        for(int i = 0; i < urls.size(); i++){
            if(!criticalURLs.getSeriesMap().keySet().contains(urls.get(i))) {
                criticalURLs.addSeries((String) urls.get(i), count.get(i));
            }else{
                criticalURLs.updatePieSeries((String) urls.get(i), count.get(i));
            }
        }
        //criticalURLs.addSeries("Number of alerts by IP", ips, count,err);
    }
}
