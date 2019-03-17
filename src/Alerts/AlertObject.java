package Alerts;

public class AlertObject {
    public String message;
    public String fullMessage;
    public int severity;

    public AlertObject(String message, String fullMessage, int severity){
        this.message = message;
        this.fullMessage = fullMessage;
        this.severity = severity;
    }

    public String toString(){
        return this.message;
    }
}
