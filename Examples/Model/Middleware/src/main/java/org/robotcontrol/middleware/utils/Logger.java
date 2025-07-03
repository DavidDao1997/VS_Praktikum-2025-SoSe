package org.robotcontrol.middleware.utils;

import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class Logger {
    private final org.slf4j.Logger logger;
    private final String context;

    public Logger(String context) {
        this.context = context;
        this.logger = LoggerFactory.getLogger(context);
    }

    private void withContext(Runnable logAction) {
        MDC.put("ctx", context);
        try {
            logAction.run();
        } finally {
            MDC.remove("ctx");
        }
    }

    public void error(String message) {
        withContext(() -> logger.error(message));
    }

    public void error(String format, Object... args) {
        withContext(() -> logger.error(format, args));
    }
    
    public void warn(String message) {
        withContext(() -> logger.warn(message));
    }

    public void warn(String format, Object... args) {
        withContext(() -> logger.warn(format, args));
    }
    
    public void info(String message) {
        withContext(() -> logger.info(message));
    }

    public void info(String format, Object... args) {
        withContext(() -> logger.info(format, args));
    }

    public void debug(String message) {
        withContext(() -> logger.debug(message));
    }

    public void debug(String format, Object... args) {
        withContext(() -> logger.debug(format, args));
    }

    public void trace(String message) {
        withContext(() -> logger.trace(message));
    }

    public void trace(String format, Object... args) {
        withContext(() -> logger.trace(format, args));
    }
}
