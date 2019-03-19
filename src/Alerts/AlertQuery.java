package Alerts;

import GUI.DashboardView;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.tophits.TopHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Observable;

public class AlertQuery extends Observable {
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


        /**
         * {
         * 	"size" : 0,
         * 	"query": {
         *     	"bool": {
         *     		"must": [
         *                {
         * 	        		"match": {
         * 	            		"Fuente": "sshd"
         *                    }
         *                },
         *                {
         * 		        	"range": {
         * 		            	"@timestamp": {
         * 		            		"gte": "now-15s"
         *                        }
         *                    }
         *                }
         *     		]
         *     	}
         * 	},
         * 	"aggs" : {
         *   		"by_username" : {
         *   			"terms": {
         *         		    "field": "username"
         *   			},
         *   			"aggs" : {
         * 	  			    "source_document": {
         * 	                	"top_hits": {
         * 	                    	"_source": {
         * 	                    		"include": [
         * 	                     		"Origen","message"
         * 	                    		]
         *                          }
         *                      }
         *                  }
         *              }
         *          }
         *      }
         * }
         */
        SearchRequestBuilder sr = client.prepareSearch().setSize(0).setQuery(QueryBuilders.boolQuery()
                                                    .must(QueryBuilders.termQuery("_index","sshd-*")) //Index starts with sshd-
                                                    .must(QueryBuilders.matchQuery("Fuente","sshd")) //Field "Fuente" equals sshd
                                                    .must(QueryBuilders.rangeQuery("@timestamp").gte("now-15s").includeUpper(false))) //From 15s before
                                                    .addAggregation(AggregationBuilders.terms("by_username").field("username") //Group by username
                                                            .subAggregation(AggregationBuilders.topHits("source_document"))); //Get the source doc of each IP

        SearchResponse r = sr.execute().actionGet();
        Terms agg = r.getAggregations().get("by_username");
        Collection<Terms.Bucket> buckets = (Collection<Terms.Bucket>) agg.getBuckets();
        String ips = "";
        String usr = "";
        String logs = "";
        int number = 0;

        for (Terms.Bucket bucket : buckets) {

            if (bucket.getDocCount() != 0) {
                TopHits tophits = bucket.getAggregations().get("source_document");

                for (SearchHit b : tophits.getHits()) {
                    if (tophits.getHits().totalHits > 1) {
                        ips = ips+((String) b.getSourceAsMap().get("Origen"))+"\n";
                        usr = (String) bucket.getKey();
                        number = (int)tophits.getHits().totalHits;
                        logs = logs + b.getSourceAsMap().get("message") + "\n";
                    }
                }
            }
        }

        if (!ips.equals("")) {
            setChanged();
            notifyObservers(new AlertObject(
                    "Strange login alert",
                    "The user " + usr + " logged in from " + number + " different IPs in a short ammount of time\n" +
                                "IPs:\n" +
                                ips+"\n\n" +
                                "Logs:\n" +
                                logs,
                    1));
        }

    }

}
