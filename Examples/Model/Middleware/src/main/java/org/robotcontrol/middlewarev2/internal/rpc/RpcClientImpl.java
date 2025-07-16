package org.robotcontrol.middlewarev2.internal.rpc;

import org.robotcontrol.middleware.utils.Logger;
import org.robotcontrol.middlewarev2.idl.types.RpcValue;
import org.robotcontrol.middlewarev2.internal.dns.Dns;
import org.robotcontrol.middlewarev2.internal.dns.DnsCachedClientFactory;
import org.robotcontrol.middlewarev2.internal.timestamp.TimestampServerImpl;
import org.robotcontrol.middlewarev2.rpc.Invokable;

public class RpcClientImpl implements Invokable {
    private static final Logger logger = new Logger("RpcClientImpl");
    
    private final String serviceName;
    private String socket;
    private final Dns dns;
    private Invokable rawClient;
    private final boolean isInternal;

    public RpcClientImpl(String serviceName, boolean isInternal) {
        this.serviceName = serviceName;
        this.isInternal = isInternal;
        this.dns = DnsCachedClientFactory.createDnsCachedClient();
        if (!isInternal) {
            TimestampServerImpl.getInstance();
        }
    }

    @Override
    public void invoke(String fnName, RpcValue... args) {
        // we should invoke first to prevent dns resolve delay. this has the downside of the first call allways failing.
        if (rawClient != null) {
            rawClient.invoke(fnName, args);
        }

        String resolvedSocket = dns.resolve(serviceName, fnName);

        if (resolvedSocket == null || resolvedSocket.isEmpty()) {
            logger.debug("failed to resolve %s.%s -> %s", serviceName, fnName, resolvedSocket);
            return;
        }

        if (!resolvedSocket.equals(socket)) {
            logger.info("resolved a new socket old: '%s' -> new: '%s' ", socket, resolvedSocket);
            socket = resolvedSocket;

            rawClient = isInternal 
                ? new RawRpcClientImpl(socket)
                : new InvokableWithTimestampAdapter(new RawRpcClientImpl(socket), serviceName);
        }
    }
}