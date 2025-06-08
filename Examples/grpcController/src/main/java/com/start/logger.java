package com.start;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class logger {

    private static final Logger logger = LoggerFactory.getLogger(ControllerServer.class);
    // ANSI-Farb-Codes
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RESET = "\u001B[0m";

    public void logInfo(String msg){
        logger.info(ANSI_GREEN + msg + ANSI_RESET);
    }



}
