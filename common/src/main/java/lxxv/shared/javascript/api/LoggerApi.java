package lxxv.shared.javascript.api;

import java.util.logging.Logger;

/**
 * Simple logger facade for JS scripts.
 */
public class LoggerApi {
    private final Logger logger;

    public LoggerApi(Logger logger) {
        this.logger = logger;
    }

    public void info(String msg) {
        logger.info(msg);
    }

    public void warn(String msg) {
        logger.warning(msg);
    }

    public void error(String msg) {
        logger.severe(msg);
    }
}
