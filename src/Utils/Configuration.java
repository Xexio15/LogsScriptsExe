package Utils;

public class Configuration {
    private static final Configuration INSTANCE = new Configuration();
    private String logsPath, logScriptsPath, alertScriptsPath;

    private Configuration() {}

    /**
     *
     * @return Returns Singleton Instance
     */
    public static Configuration getInstance() {
        return INSTANCE;
    }

    /**
     *
     * @return  String URI where logs will be written
     */
    public String getLogsPath(){
        return this.logsPath;
    }

    /**
     *
     * @param logsPath String URI where logs will be written
     */
    public void setLogsPath(String logsPath){
        this.logsPath = logsPath;
    }

    /**
     *
     * @return String URI where log scripts are located
     */
    public String getLogScriptsPath(){
        return logScriptsPath;
    }

    /**
     *
     * @param logScriptsPath String URI where log scripts are located
     */
    public void setLogScriptsPath(String logScriptsPath){
        this.logScriptsPath = logScriptsPath;
    }

    /**
     *
     * @return String URI where alert scripts are located
     */
    public String getAlertScriptsPath(){
        return alertScriptsPath;
    }

    /**
     *
     * @param alertScriptsPath String URI where alert scripts are located
     */
    public void setAlertScriptsPath(String alertScriptsPath){
        this.alertScriptsPath = alertScriptsPath;
    }

}
