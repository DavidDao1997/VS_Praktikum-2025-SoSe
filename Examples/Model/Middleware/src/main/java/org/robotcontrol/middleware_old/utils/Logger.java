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

    private String getCaller() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        String loggerClassName = this.getClass().getName();

        for (int i = 2; i < stack.length; i++) {
            StackTraceElement ste = stack[i];
            String className = ste.getClassName();
            if (!className.equals(loggerClassName)) {
                // Format: ClassName.methodName(FileName:LineNumber)
                return String.format("%s.%s(%s:%d)",
                        className,
                        ste.getMethodName(),
                        ste.getFileName(),
                        ste.getLineNumber());
            }
        }
        return "unknown";
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
        withContext(() -> logger.error("{} at {}", message, getCaller()));
    }

    public void error(String format, Object... args) {
        withContext(() -> logger.error("{} at {}", String.format(format, args), getCaller()));
    }
    
    public void warn(String message) {
        withContext(() -> logger.warn("{} at {}", message, getCaller()));
    }

    public void warn(String format, Object... args) {
        withContext(() -> logger.warn("{} at {}", String.format(format, args), getCaller()));
    }
    
    public void info(String message) {
        withContext(() -> logger.info("{} at {}", message, getCaller()));
    }

    public void info(String format, Object... args) {
        withContext(() -> logger.info("{} at {}", String.format(format, args), getCaller()));
    }

    public void debug(String message) {
        withContext(() -> logger.debug("{} at {}", message, getCaller()));
    }

    public void debug(String format, Object... args) {
        withContext(() -> logger.debug("{} at {}", String.format(format, args), getCaller()));
    }

    public void trace(String message) {
        withContext(() -> logger.trace("{} at {}", message, getCaller()));
    }

    public void trace(String format, Object... args) {
        withContext(() -> logger.trace("{} at {}", String.format(format, args), getCaller()));
    }
}
