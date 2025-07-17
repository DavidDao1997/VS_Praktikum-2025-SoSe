package org.robotcontrol.middleware.internal.rpc;

import org.robotcontrol.middleware.idl.types.RpcValue;
import org.robotcontrol.middleware.internal.dns.Dns;
import org.robotcontrol.middleware.internal.dns.DnsCachedClientFactory;
import org.robotcontrol.middleware.internal.timestamp.TimestampServerImpl;
import org.robotcontrol.middleware.rpc.Invokable;
import org.robotcontrol.middleware.utils.Logger;

public class RpcClientImpl implements Invokable {
    private static final Logger logger = new Logger("RpcClientImpl");
    
    private final String serviceName;
    private String socket;
    private final Dns dns;
    private RawRpcClientImpl rawClient;
    private final boolean withTimestamp;
    private final TimestampServerImpl timestampServer;

    public RpcClientImpl(String serviceName, boolean isInternal) {
        this.serviceName = serviceName;
        this.withTimestamp = !isInternal;
        this.dns = DnsCachedClientFactory.createDnsCachedClient();
        if (withTimestamp) {
            timestampServer = TimestampServerImpl.getInstance();
        } else {
            timestampServer = null;
        }
    }

    @Override
    public void invoke(String fnName, RpcValue... args) {
        Long timestamp = null;
        if (withTimestamp) {
            timestamp = timestampServer.getTimestamp(serviceName, fnName);
            if (timestamp == null || timestamp == 0) return;
        }

        String resolvedSocket = dns.resolve(serviceName, fnName);

        if (resolvedSocket == null || resolvedSocket.isEmpty()) {
            logger.debug("failed to resolve %s.%s -> %s", serviceName, fnName, resolvedSocket);
            return;
        }

        if (!resolvedSocket.equals(socket)) {
            logger.debug("resolved a new socket old: '%s' -> new: '%s' ", socket, resolvedSocket);
            socket = resolvedSocket;

            rawClient = new RawRpcClientImpl(socket); 
        }
        if (withTimestamp) {
            rawClient.invoke(timestamp, fnName, args);
        } else {
            rawClient.invoke(fnName, args);
        }
    }
}