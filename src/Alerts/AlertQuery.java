package Alerts;

import GUI.AlertsView;
import Utils.Configuration;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.cardinality.Cardinality;
import org.elasticsearch.search.aggregations.metrics.tophits.TopHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Observable;

public class AlertQuery extends Observable implements Runnable{
    private QUERY_TYPE query;
    private Configuration conf = Configuration.getInstance();
    public enum QUERY_TYPE {
        SSH_STRANGE_LOGIN, PORT_SCANNING, ARP_POISONING, CRITICAL_URLS, SSH_BRUTE_FORCE, SOFTWARE_FIREWALL_DROPS
    }

    public AlertQuery(AlertsView v){
        this.addObserver(v);
    }
    public AlertQuery(AlertsView v, QUERY_TYPE query){
        this.addObserver(v);
        this.query = query;
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
        ArrayList<Map<String,Object>> mappedLogs = new ArrayList<>();
        for (Terms.Bucket bucket : buckets) {

            if (bucket.getDocCount() != 0) {
                TopHits tophits = bucket.getAggregations().get("source_document");

                for (SearchHit b : tophits.getHits()) {
                    if (tophits.getHits().totalHits > 1 && tophits.getHits().totalHits < 5) {
                        mappedLogs.add(b.getSourceAsMap());
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
                                ips,
                    logs,
                    1,
                    mappedLogs
                    )
            );
        }
        client.close();

    }

    public void portScanning() throws UnknownHostException {

        String json =   "{\n" +
                "       \"properties\": {\n" +
                "           \"Destino\": {\n" +
                "               \"type\": \"text\",\n" +
                "               \"fielddata\": true\n" +
                "           },\n" +
                "           \"Origen\": {\n" +
                "               \"type\": \"text\",\n" +
                "               \"fielddata\": true\n" +
                "           },\n" +
                "           \"Dst_Port\": {\n" +
                "               \"type\": \"text\",\n" +
                "               \"fielddata\": true\n" +
                "           }\n" +
                "       }\n" +

                "}\n";

        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
        client.admin().indices().preparePutMapping("asa-*").setType("doc").setSource(json, XContentType.JSON).get();

        /**
         * {
         * 	"size" : 0,
         * 	"query": {
         *     	"bool": {
         *     		"must": [
         *                                {
         * 	        		"match": {
         * 	            		"Fuente": "asa"
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
         *   		"by_srcIP" : {
         *   			"terms": {
         *         		"field": "Origen"
         *   			},
         *   			"aggs" : {
         *   				"by_dstIP" :{
         *   					"terms" : {
         *   						"field" : "Destino"
         *                    },
         * 	  				"aggs": {
         * 		            	"unique_port_count": {
         * 		            		"cardinality": {
         * 		                		"field": "Dst_Port"
         *                            }
         *                        }
         *                    }*   				}
         *            }
         *        }*   	}
         * }
         */

        SearchRequestBuilder sr = client.prepareSearch().setSize(0).setQuery(QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("_index","asa-*")) //Index starts with asa-
                .must(QueryBuilders.matchQuery("Fuente","asa")) //Field "Fuente" equals asa
                .must(QueryBuilders.rangeQuery("@timestamp").gte("now-15s").includeUpper(false))) //From 15s before
                .addAggregation(AggregationBuilders.terms("by_srcIP").field("Origen") //Group by Origen
                        .subAggregation(AggregationBuilders.terms("by_dstIP").field("Destino")//Group by Destino
                                .subAggregation(AggregationBuilders.cardinality("unique_port_count").field("Dst_Port"))//Get port count
                                    .subAggregation(AggregationBuilders.topHits("source_document").size(100))));

        SearchResponse r = sr.execute().actionGet();


        Terms agg = r.getAggregations().get("by_srcIP");
        Collection<Terms.Bucket> buckets = (Collection<Terms.Bucket>) agg.getBuckets();

        String srcIP = "";
        String logs = "";
        ArrayList<Map<String,Object>> mappedLogs = new ArrayList<>();
        for (Terms.Bucket bucket : buckets) {
            srcIP = bucket.getKeyAsString();
            if (bucket.getDocCount() != 0) {
                Terms terms = bucket.getAggregations().get("by_dstIP");
                Collection<Terms.Bucket> bkts = (Collection<Terms.Bucket>) terms.getBuckets();

                for (Terms.Bucket b : bkts) {
                    Cardinality c = b.getAggregations().get("unique_port_count");
                    if (c.getValue() > 15){
                        TopHits tophits = b.getAggregations().get("source_document");

                        for (SearchHit sh : tophits.getHits()) {
                            mappedLogs.add(sh.getSourceAsMap());
                            logs = logs + sh.getSourceAsMap().get("message")+"\n";
                        }
                        setChanged();
                        notifyObservers(new AlertObject("Port Scanning from "+srcIP,
                                "The IP "+srcIP+" has tried to connect to "+ c.getValue() + " ports, could potentially be a port scanning",
                                logs,
                                2,
                                mappedLogs
                                )
                        );
                    }
                }
            }
        }

        client.close();
    }

    public void arpPoisoning() throws UnknownHostException {
        /**
         * {
         * 	"query": {
         *     	"bool": {
         *     		"must": [
         *                                {
         *     				"match_phrase" : {
         *     					"Mensaje" : "Received ARP"
         *                    }
         *                },
         *                {
         * 	        		"match": {
         * 	            		"Fuente": "asa"
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
         *   	}
         * }
         */

        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));

        SearchRequestBuilder sr = client.prepareSearch().setSize(1).setQuery(QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("_index","asa-*")) //Index starts with asa-
                .must(QueryBuilders.matchQuery("Fuente","asa")) //Field "Fuente" equals asa
                .must(QueryBuilders.rangeQuery("@timestamp").gte("now-15s").includeUpper(false)) //From 15s before;
                .must(QueryBuilders.matchPhraseQuery("Mensaje","Received ARP")));

        SearchResponse r = sr.execute().actionGet();
        SearchHits hits = r.getHits();
        ArrayList<Map<String,Object>> mappedLogs = new ArrayList<>();
        if (hits.getTotalHits() > 0){
            SearchHit hit = hits.getAt(0);
            mappedLogs.add(hit.getSourceAsMap());
            setChanged();
            notifyObservers(new AlertObject("Possible ARP Poisoning from "+hit.getSourceAsMap().get("Origen")+" / "+ hit.getSourceAsMap().get("SRC_MAC"),
                            "The machine "+hit.getSourceAsMap().get("Origen")+" / "+ hit.getSourceAsMap().get("SRC_MAC") +" could be trying an ARP Poisoning to sniff the Network Traffic",
                                    (String)hit.getSourceAsMap().get("message"),
                            2,
                                    mappedLogs
                    )
            );
        }
        client.close();
    }

    public void criticalURLs() throws UnknownHostException {
        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));

        SearchRequestBuilder sr = client.prepareSearch().setQuery(QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("_index","asa-*")) //Index starts with asa-
                .must(QueryBuilders.matchQuery("Fuente","asa")) //Field "Fuente" equals asa
                .must(QueryBuilders.rangeQuery("@timestamp").gte("now-15s").includeUpper(false)) //From 15s before;
                .must(QueryBuilders.matchPhraseQuery("Mensaje","Accessed URL")));

        SearchResponse r = sr.execute().actionGet();
        SearchHits hits = r.getHits();

        ArrayList<Map<String,Object>> mappedLogs = new ArrayList<>();
        for(SearchHit hit : hits){
            if (conf.getBlacklist().contains((hit.getSourceAsMap().get("URL")))){
                mappedLogs.add(hit.getSourceAsMap());
                setChanged();
                notifyObservers(new AlertObject("Blacklisted URL accessed",
                                "The machine "+hit.getSourceAsMap().get("Origen")+" tried to access to a blacklisted URL: "+hit.getSourceAsMap().get("URL_IP")+" -> "+hit.getSourceAsMap().get("URL"),
                                (String)hit.getSourceAsMap().get("message"),
                                1,
                                        mappedLogs
                        )
                );
            }
        }

        client.close();
    }

    public void sshBruteForce() throws UnknownHostException {
        String json =   "{\n" +
                "       \"properties\": {\n" +
                "           \"Origen\": {\n" +
                "               \"type\": \"text\",\n" +
                "               \"fielddata\": true\n" +
                "           }\n" +
                "       }\n" +
                "}\n";

        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
        client.admin().indices().preparePutMapping("sshd-*").setType("doc").setSource(json, XContentType.JSON).get();


        SearchRequestBuilder sr = client.prepareSearch().setQuery(QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("_index","sshd-*")) //Index starts with asa-
                .must(QueryBuilders.matchQuery("Fuente","sshd")) //Field "Fuente" equals asa
                .must(QueryBuilders.rangeQuery("@timestamp").gte("now-15s").includeUpper(false))) //From 15s before
                .addAggregation(AggregationBuilders.terms("by_srcIP").field("Origen") //Group by Origen
                        .subAggregation(AggregationBuilders.topHits("source_document").size(100)));

        SearchResponse r = sr.execute().actionGet();


        Terms agg = r.getAggregations().get("by_srcIP");
        Collection<Terms.Bucket> buckets = (Collection<Terms.Bucket>) agg.getBuckets();

        String srcIP = "";
        String logs = "";
        ArrayList<Map<String,Object>> mappedLogs = new ArrayList<>();
        for (Terms.Bucket bucket : buckets) {
            srcIP = bucket.getKeyAsString();
            if (bucket.getDocCount() != 0) {
                int c = (int) bucket.getDocCount();
                if (c > 20) {
                    TopHits tophits = bucket.getAggregations().get("source_document");

                    for (SearchHit sh : tophits.getHits()) {
                        mappedLogs.add(sh.getSourceAsMap());
                        srcIP = (String) sh.getSourceAsMap().get("Origen");
                        logs = logs + sh.getSourceAsMap().get("message") + "\n";
                    }
                    setChanged();
                    notifyObservers(new AlertObject("Brute force attack from " + srcIP,
                                    "The IP " + srcIP + " has tfailed to login " + c + " times, a brute force attack could be in process",
                                    logs,
                                    2,
                                    mappedLogs
                            )
                    );
                }
            }
        }
        client.close();
    }

    public void softwareFirewallDrops() throws UnknownHostException{
        String json =   "{\n" +
                "       \"properties\": {\n" +
                "           \"Origen\": {\n" +
                "               \"type\": \"text\",\n" +
                "               \"fielddata\": true\n" +
                "           }\n" +
                "       }\n" +

                "}\n";

        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
        client.admin().indices().preparePutMapping("ufw-*").setType("doc").setSource(json, XContentType.JSON).get();
        client.admin().indices().preparePutMapping("windows-firewall-*").setType("doc").setSource(json, XContentType.JSON).get();

        SearchRequestBuilder sr = client.prepareSearch().setSize(0).setQuery(QueryBuilders.boolQuery()
                .must(QueryBuilders.boolQuery()
                    .should(QueryBuilders.matchQuery("_index","ufw-*"))
                    .should(QueryBuilders.matchQuery("_index","windows-firewall-*"))) //Index starts with asa-
                .must(QueryBuilders.rangeQuery("@timestamp").gte("now-15s").includeUpper(false)) //From 15s before;
                .must(QueryBuilders.boolQuery()
                    .should(QueryBuilders.matchQuery("Flag","DROP"))
                    .should(QueryBuilders.matchQuery("Flag", "BLOCK")))
                )
                .addAggregation(AggregationBuilders.terms("by_srcIP").field("Origen") //Group by username
                    .subAggregation(AggregationBuilders.topHits("source_document").size(100)).size(100)); //Get the source doc of each IP
        ;
        /**
         * {
         * 	"size" : 0,
         * 	"query": {
         *     	"bool": {
         *     		"must": [
         *                                {
         *                 	"bool": {
         *                         "should": [
         *                             {"match": {"Flag": "BLOCK"}},
         *                             {"match": {"Flag": "DROP"}}
         *                         ]
         *                     }
         *                 },
         *                 {
         *                     "bool": {
         *                         "should": [
         *                             {"match": {"Fuente": "UFW"}},
         *                             {"match": {"Fuente": "Windows-Firewall"}}
         *                         ]
         *                     }
         *                 },
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
         *   		"by_srcIP" : {
         *   			"terms": {
         *         		"field": "Origen"
         *   			},
         *   		"aggs" : {
         * 		    "source_document": {
         * 		      	"top_hits": {
         * 		      		"size" : 100,
         * 		          	"_source": {
         * 		          		"include": [
         * 		           		"Origen","message"
         * 		          		]
         *                   }
         *                }* 		    }
         *        }
         *        }*   	}
         * }
         */

        SearchResponse r = sr.execute().actionGet();
        Terms agg = r.getAggregations().get("by_srcIP");
        Collection<Terms.Bucket> buckets = (Collection<Terms.Bucket>) agg.getBuckets();
        String logs = "";
        ArrayList<Map<String,Object>> mappedLogs = new ArrayList<>();
        for (Terms.Bucket bucket : buckets) {

            if (bucket.getDocCount() != 0) {
                TopHits tophits = bucket.getAggregations().get("source_document");
                if (bucket.getDocCount() > 5) {
                    for (SearchHit b : tophits.getHits()) {

                        mappedLogs.add(b.getSourceAsMap());
                        logs = logs + b.getSourceAsMap().get("message") + "\n";


                    }
                    setChanged();
                    notifyObservers(new AlertObject("Blocked IP: " + bucket.getKeyAsString(),
                                    "The IP " + bucket.getKeyAsString() + " has been blocked " + bucket.getDocCount() + " times",
                                    logs,
                                    1,
                                    mappedLogs
                            )
                    );
                }
            }
        }
    }
    @Override
    public void run() {
        try {
            switch (query) {
                case SSH_STRANGE_LOGIN:
                    strangeLoginSSH();
                    break;
                case PORT_SCANNING:
                    portScanning();
                    break;
                case ARP_POISONING:
                    arpPoisoning();
                    break;
                case CRITICAL_URLS:
                    criticalURLs();
                    break;
                case SSH_BRUTE_FORCE:
                    sshBruteForce();
                    break;
                case SOFTWARE_FIREWALL_DROPS:
                    softwareFirewallDrops();
                    break;

            }
        }catch(UnknownHostException e){

        }
    }
}
