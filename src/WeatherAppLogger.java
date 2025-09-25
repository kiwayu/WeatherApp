import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Simple logging utility for the Weather Application.
 * Provides different log levels and formatted output.
 */
public final class WeatherAppLogger {
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static boolean debugEnabled = false;
    
    // Prevent instantiation
    private WeatherAppLogger() {}
    
    public enum Level {
        DEBUG, INFO, WARN, ERROR
    }
    
    /**
     * Enable or disable debug logging.
     * @param enabled true to enable debug logs
     */
    public static void setDebugEnabled(boolean enabled) {
        debugEnabled = enabled;
    }
    
    /**
     * Log a debug message (only if debug is enabled).
     * @param message the message to log
     */
    public static void debug(String message) {
        if (debugEnabled) {
            log(Level.DEBUG, message);
        }
    }
    
    /**
     * Log an info message.
     * @param message the message to log
     */
    public static void info(String message) {
        log(Level.INFO, message);
    }
    
    /**
     * Log a warning message.
     * @param message the message to log
     */
    public static void warn(String message) {
        log(Level.WARN, message);
    }
    
    /**
     * Log an error message.
     * @param message the message to log
     */
    public static void error(String message) {
        log(Level.ERROR, message);
    }
    
    /**
     * Log an error message with exception.
     * @param message the message to log
     * @param throwable the exception that occurred
     */
    public static void error(String message, Throwable throwable) {
        log(Level.ERROR, message + " - Exception: " + throwable.getMessage());
        if (debugEnabled) {
            throwable.printStackTrace();
        }
    }
    
    /**
     * Internal logging method.
     * @param level the log level
     * @param message the message to log
     */
    private static void log(Level level, String message) {
        String timestamp = DATE_FORMAT.format(new Date());
        String logEntry = String.format("[%s] %s: %s", timestamp, level, message);
        
        if (level == Level.ERROR) {
            System.err.println(logEntry);
        } else {
            System.out.println(logEntry);
        }
    }
}
