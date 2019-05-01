package Alerts;

import GUI.LogsView;
import Utils.Utils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class Logger extends Observable implements Runnable{
    private int time;
    private boolean stop = false;
    public Logger (LogsView v, int time ){
        this.addObserver(v);
        this.time = time;

    }

    public ArrayList<Map<String, Object>> getLogs() throws UnknownHostException {
        ArrayList<Map<String,Object>> mappedLogs = new ArrayList<>();
        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));

        SearchRequestBuilder sr = client.prepareSearch().setSize(1000).setQuery(QueryBuilders.boolQuery()
                .must(QueryBuilders.rangeQuery("@timestamp").gte("now-"+time+"s").includeUpper(false))); //From 15s before;

        SearchResponse r = sr.execute().actionGet();
        for (SearchHit h : r.getHits()){
            Map<String, Object> hmap = h.getSourceAsMap();
            for(Iterator<String> iterator = hmap.keySet().iterator(); iterator.hasNext(); ) {
                String key = iterator.next();

                if(Arrays.asList(Utils.IGNORED_FIELDS).contains(key)) {
                    iterator.remove();
                }
            }
            hmap.put("index", h.getIndex());
            mappedLogs.add(hmap);
        }
        return mappedLogs;
    }

    @Override
    public void run() {

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    ArrayList<Map<String, Object>> logs = getLogs();
                    setChanged();
                    notifyObservers(logs);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                if(stop){
                    timer.cancel();
                    timer.purge();
                }
            }
        };
        if(!stop)  timer.scheduleAtFixedRate(task,0,time*1000);

    }

    public void stopTask(){
        this.stop = true;
    }
}
