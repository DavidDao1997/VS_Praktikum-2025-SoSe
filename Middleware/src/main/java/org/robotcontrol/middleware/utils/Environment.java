package org.robotcontrol.middleware.utils;

public class Environment {
    private static final Logger logger = new Logger("Environment");

    public static String getEnvStringOrExit(String name) {
        String envVar = System.getenv(name);
        if (envVar != null && !envVar.isEmpty()) {
            return envVar;
        } else {
            logger.error("%s undefined, exiting...", name);
            System.exit(1);
        }
        return "";
    }

    public static Integer getEnvIntOrExit(String name) {
        String envVar = System.getenv(name);
        if (envVar != null && !envVar.isEmpty()) {
            return Integer.parseInt(envVar);
        } else {
            logger.error("%s undefined, exiting...", name);
            System.exit(1);
        }
        return 0;
    }
}
