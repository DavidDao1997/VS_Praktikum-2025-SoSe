package org.robotcontrol.middlewarev2.internal.dns;

import org.robotcontrol.middleware.utils.Environment;
import org.robotcontrol.middlewarev2.internal.rpc.RawRpcServerImpl;
import org.robotcontrol.middlewarev2.rpc.RpcServer;

public final class DnsCallbackServiceFactory {
    private static volatile DnsCallbackServiceImpl SHARED_DNS_CALLBACK_SERVICE;
    private static volatile RpcServer SHARED_DNS_CALLBACK_SERVER;
    private static final Object LOCK = new Object(); // For synchronization

    private static final int DNS_CALLBACK_PORT = Environment.getEnvIntOrExit("DNS_CALLBACK_PORT");

    public static DnsCallbackServiceImpl createDnsCallbackService() {
        if (SHARED_DNS_CALLBACK_SERVICE == null || SHARED_DNS_CALLBACK_SERVER == null) {
            synchronized (LOCK) {
                if (SHARED_DNS_CALLBACK_SERVICE == null || SHARED_DNS_CALLBACK_SERVER == null) {
                    SHARED_DNS_CALLBACK_SERVICE = new DnsCallbackServiceImpl();
                    SHARED_DNS_CALLBACK_SERVER = new RawRpcServerImpl(
                        SHARED_DNS_CALLBACK_SERVICE,
                        DNS_CALLBACK_PORT
                    );
                    SHARED_DNS_CALLBACK_SERVER.start(); // Start the server
                    // Add a shutdown hook to stop the server when the app exits
                    Runtime.getRuntime().addShutdownHook(
                        new Thread(SHARED_DNS_CALLBACK_SERVER::stop)
                    );
                }
            }
        }

        return SHARED_DNS_CALLBACK_SERVICE;
    }
}
