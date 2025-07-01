package org.robotcontrol.watchdog;

import org.robotcontrol.middleware.RpcValue;
import org.robotcontrol.middleware.udp.UdpClient;

/**
 * Lightweight helper for
 *
 *   interface WatchDog {
 *       void checkIn(String serviceName);
 *       void subscribe(String pattern, String cbHost, int cbPort);
 *   }
 *
 * The class does **no** timing logic: you call {@link #checkIn()} from
 * whatever scheduler you already use (e.g. `ScheduledExecutorService`).
 */
public class WatchDogClient {

    private final UdpClient udp;
    private final String    selfName;
    private final String    callbackHost;
    private final int       callbackPort;

    /**
     * @param host          watchdog-server host (e.g. "127.0.0.1")
     * @param port          watchdog-server UDP port (e.g. 45060)
     * @param selfName      identifier you want the watchdog to track
     * @param callbackHost  host/IP where *your* callback stub is listening
     * @param callbackPort  UDP port of that callback stub
     */
    public WatchDogClient(String host,
                          int    port,
                          String selfName,
                          String callbackHost,
                          int    callbackPort) {
        this.udp          = new UdpClient(host, port);
        this.selfName     = selfName;
        this.callbackHost = callbackHost;
        this.callbackPort = callbackPort;
    }

    /* -------------------------------------------------------------- */
    /*  IDL methods                                                   */
    /* -------------------------------------------------------------- */

    /** One heartbeat.  Call this on every timer tick. */
    public void checkIn() {
        udp.invoke("checkIn",
                   new RpcValue.StringValue(selfName));
    }

    /**
     * Ask the watchdog to push `reportHealthy` callbacks whenever the state of
     * components matching {@code pattern} changes (or on every heartbeat, per
     * your server’s behaviour).
     *
     * Example:  {@code subscribe("Controller")}  or  {@code subscribe("State.*")}
     */
    public void subscribe(String pattern) {
        udp.invoke("subscribe",
                   new RpcValue.StringValue(pattern),
                   new RpcValue.StringValue(callbackHost),
                   new RpcValue.IntValue   (callbackPort));
    }

    /** Optional tidy-up. */
    public void close() { udp.close(); }
}