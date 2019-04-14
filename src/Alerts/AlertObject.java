package Alerts;

import Utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

public class AlertObject implements Serializable {
    public String message;
    public String fullMessage;
    public int severity;
    public String logs = "";
    public ArrayList<Map<String,Object>> mappedLogs;
    public String[] logsMessages;

    public AlertObject(String message, String fullMessage, String logs, int severity){
        this.message = message;
        this.fullMessage = fullMessage;
        this.severity = severity;
        this.logs = logs;
    }

    public AlertObject(String message, String fullMessage, String logs, int severity, ArrayList<Map<String,Object>> mappedLogs){
        this.message = message;
        this.fullMessage = fullMessage;
        this.severity = severity;
        this.logs = logs;
        this.mappedLogs = mappedLogs;
        this.logsMessages = new String[mappedLogs.size()];
        int i = 0;
        for(Map<String, Object> m : this.mappedLogs){
            this.logsMessages[i] = ((String) m.get("message"));
            m.remove("message");
            i++;
        }

    }

    public String toString(){
        return this.message;
    }

    public ArrayList<Map<String,Object>> getMap(){
        for(int i = 0; i < this.mappedLogs.size(); i++){
            for(Iterator<String> iterator = this.mappedLogs.get(i).keySet().iterator(); iterator.hasNext(); ) {
                String key = iterator.next();
                if(Arrays.asList(Utils.IGNORED_FIELDS).contains(key)) {
                    iterator.remove();
                }
            }
        }
        return this.mappedLogs;
    }
}
