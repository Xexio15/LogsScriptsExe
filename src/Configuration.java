public class Configuration {
    private static final Configuration INSTANCE = new Configuration();
    private String logsPath;
    private String logScriptsPath;
    private String alertScriptsPath;

    private Configuration() {}

    public static Configuration getInstance() {
        return INSTANCE;
    }

    public String getLogsPath(){
        return this.logsPath;
    }

    public void setLogsPath(String logsPath){
        this.logsPath = logsPath;
    }

    public String getLogScriptsPath(){
        return logScriptsPath;
    }

    public void setLogScriptsPath(String logScriptsPath){
        this.logScriptsPath = logScriptsPath;
    }

    public String getAlertScriptsPath(){
        return alertScriptsPath;
    }

    public void setAlertScriptsPath(String alertScriptsPath){
        this.alertScriptsPath = alertScriptsPath;
    }

}
