package garuntimeenv.utils;

import garuntimeenv.envcomponents.EnvConfig;
import org.apache.log4j.Logger;
import org.apache.log4j.lf5.LogLevel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class MyLogger {

    private static MyLogger INSTANCE = null;
    private HashSet<String> loggable = new HashSet<>();
    private HashMap<String, Logger> loggerMap = new HashMap<>();
    private boolean allLogs = false;

    /**
     * Load the log levels from the MyLog.properties file
     */
    private MyLogger() {
        for (String level : EnvConfig.getInstance().getLogLevel()) {
            loggable.add(level);
            if (level.equals("*")) allLogs = true;
        }
    }

    /**
     * Constructor of my custom logger
     *
     * @param levels The custom levels of the logger
     */
    private MyLogger(String... levels) {
        loggable.addAll(Arrays.asList(levels));
    }

    /**
     * Sets the level of the logger
     *
     * @param levels The to be set levels
     */
    public static void setLevels(String... levels) {
        if (INSTANCE == null) {
            INSTANCE = new MyLogger(levels);
        } else {
            INSTANCE.loggable.addAll(Arrays.asList(levels));
        }
    }

    /**
     * Get the logger instance
     *
     * @param newClass the class of the object that requested the logger instance
     * @return The logger instance
     */
    public static MyLogger getLogger(Class newClass) {
        if (INSTANCE == null) {
            INSTANCE = new MyLogger();
        }
        INSTANCE.loggerMap.put(newClass.getName(), Logger.getLogger(newClass));
        return INSTANCE;
    }

    /**
     * Get the logger instance and automatically sets the {@code levels} to the
     *
     * @param newClass the class of the object that requested the logger instance
     * @param levels   To be added custom levels
     * @return The logger instance
     */
    public static MyLogger getLogger(Class newClass, String... levels) {
        if (INSTANCE == null) {
            INSTANCE = new MyLogger(levels);
        } else {
            INSTANCE.loggable.addAll(Arrays.asList(levels));
        }
        INSTANCE.loggerMap.put(newClass.getName(), Logger.getLogger(newClass));
        return INSTANCE;
    }

    /**
     * Log's the {@code message} through the logfj logger of the class if the {@code customlevel} is set
     * Uses the {@code logLevel} as output level
     *
     * @param logLevel    The loglevel
     * @param customLevel The custom level that needs to be set
     * @param message     The to be logged massage
     */
    public void log(LogLevel logLevel, String customLevel, String message) {
        // Check if the custom level is in the current loggable
        if (this.loggable.contains(customLevel) || allLogs) {
            Logger log = this.loggerMap.get(Thread.currentThread().getStackTrace()[1].getClass());
            switch (logLevel.getLabel()) {
                case "DEBUG":
                    log.debug(message);
                case "FATAL":
                    log.fatal(message);
                case "ERROR":
                    log.error(message);
                case "WARN":
                    log.warn(message);
                case "INFO":
                    log.info(message);
                case "WARNING":
                    log.warn(message);
            }
        }
    }

    /**
     * Log's the {@code message} through the logfj logger of the class if the {@code customlevel} is set
     * Uses the debug level output
     *
     * @param customLevel The custom level that must be set in the env config
     * @param message     The message to be logged
     */
    public void debug(String customLevel, String message) {
        if (this.loggable.contains(customLevel) || allLogs) {
            Logger log = this.loggerMap.get(Thread.currentThread().getStackTrace()[2].getClassName());
            if (log != null)
                log.debug(message);
        }
    }

    /**
     * Log's the {@code message} through the logfj logger of the class if the {@code customlevel} is set
     * Uses the warn level output
     *
     * @param customLevel The custom level that must be set in the env config
     * @param message     The message to be logged
     */
    public void warning(String customLevel, String message) {
        if (this.loggable.contains(customLevel) || allLogs) {
            Logger log = this.loggerMap.get(Thread.currentThread().getStackTrace()[2].getClassName());
            if (log != null)
                log.warn(message);
        }
    }

    /**
     * Log's the {@code message} through the logfj logger of the class if the {@code customlevel} is set
     * Uses the info level output
     *
     * @param customLevel The custom level that must be set in the env config
     * @param message     The message to be logged
     */
    public void info(String customLevel, String message) {
        if (this.loggable.contains(customLevel) || allLogs) {
            Logger log = this.loggerMap.get(Thread.currentThread().getStackTrace()[2].getClassName());
            if (log != null)
                log.info(message);
        }
    }

    /**
     * Log's the {@code message} through the logfj logger of the class if the {@code customlevel} is set
     * Uses the error level output
     *
     * @param customLevel The custom level that must be set in the env config
     * @param message     The message to be logged
     */
    public void error(String customLevel, String message) {
        if (this.loggable.contains(customLevel) || allLogs) {
            Logger log = this.loggerMap.get(Thread.currentThread().getStackTrace()[2].getClassName());
            if (log != null)
                log.error(message);
        }
    }
}
