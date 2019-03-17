package Alerts;

import GUI.DashboardView;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Observable;

public class AlertQuery extends Observable {
    private ArrayList<AlertObject> alerts = new ArrayList<>();

    public AlertQuery(DashboardView v){
        this.addObserver(v);
    }

    public void strangeLoginSSH() throws UnknownHostException {

        String json =   "{\n" +

                        "       \"properties\": {\n" +
                        "           \"username\": {\n" +
                        "               \"type\": \"text\",\n" +
                        "               \"fielddata\": true\n" +
                        "           },\n" +
                        "           \"Origen\": {\n" +
                        "               \"type\": \"text\",\n" +
                        "               \"fielddata\": true\n" +
                        "           }\n" +
                        "       }\n" +

                        "}\n";

        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
        client.admin().indices().preparePutMapping("sshd-*").setType("doc").setSource(json, XContentType.JSON).get();

        SearchRequestBuilder sr = client.prepareSearch().setSize(1).setQuery(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("_index","sshd-*"))
                                                    .must(QueryBuilders.matchQuery("Fuente","sshd"))
                                                    .must(QueryBuilders.rangeQuery("@timestamp").gte("now-15s").includeUpper(false)))
                                                    .addAggregation(AggregationBuilders.terms("by_username").field("username")
                                                            .subAggregation(AggregationBuilders.terms("by_ip").field("Origen")));

        SearchResponse r = sr.execute().actionGet();
        Terms agg = r.getAggregations().get("by_username");
        Collection<Terms.Bucket> buckets = (Collection<Terms.Bucket>) agg.getBuckets();
        String ips = "";
        String usr = "";
        int number = 0;
        for (Terms.Bucket bucket : buckets) {

            if (bucket.getDocCount() != 0) {
                Terms terms = bucket.getAggregations().get("by_ip");
                //System.out.println(terms.getBuckets().size());
                Collection<Terms.Bucket> bkts = (Collection<Terms.Bucket>) terms.getBuckets();
                for (Terms.Bucket b : bkts) {
                    if (terms.getBuckets().size() > 1) {
                        ips = ips+((String) b.getKey())+"\n";
                        usr = (String) bucket.getKey();
                        number = terms.getBuckets().size();
                    }


                }

            }
        }
        if (!ips.equals("")) {
            alerts.add(new AlertObject("Strange login alert", "The user " + usr + " logged in from " + number + " different IPs in a short ammount of time\nIPs:\n" + ips, 1));
            setChanged();
            notifyObservers(alerts);
        }

    }

}
