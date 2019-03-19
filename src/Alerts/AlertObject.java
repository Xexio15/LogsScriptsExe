package Alerts;

public class AlertObject {
    public String message;
    public String fullMessage;
    public int severity;
    public String logs = "";


    public AlertObject(String message, String fullMessage, String logs, int severity){
        this.message = message;
        this.fullMessage = fullMessage;
        this.severity = severity;
        this.logs = logs;
    }

    public String toString(){
        return this.message;
    }
}
