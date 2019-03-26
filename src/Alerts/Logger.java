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
import java.util.concurrent.TimeUnit;

public class Logger extends Observable implements Runnable{
    private int time = 5;
    ArrayList<Map<String,Object>> mappedLogs = new ArrayList<>();
    public Logger (LogsView v){
        this.addObserver(v);

    }

    public void getLogs() throws UnknownHostException {
        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));

        SearchRequestBuilder sr = client.prepareSearch().setSize(1000).setQuery(QueryBuilders.boolQuery()
                .must(QueryBuilders.rangeQuery("@timestamp").gte("now-"+time+"s").includeUpper(false))); //From 15s before;

        SearchResponse r = sr.execute().actionGet();
        System.out.println(r.toString());
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
    }

    @Override
    public void run() {
        while(true) {
            try {
                getLogs();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            setChanged();
            notifyObservers(mappedLogs);
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
