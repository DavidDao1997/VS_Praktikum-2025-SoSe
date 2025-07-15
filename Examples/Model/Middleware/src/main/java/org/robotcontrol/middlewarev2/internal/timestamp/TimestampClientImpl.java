package org.robotcontrol.middlewarev2.internal.timestamp;

import org.robotcontrol.middlewarev2.idl.types.RpcValue;
import org.robotcontrol.middlewarev2.internal.dns.Dns;
import org.robotcontrol.middlewarev2.internal.dns.DnsCachedClientFactory;
import org.robotcontrol.middlewarev2.internal.rpc.RawRpcClientImpl;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TimestampClientImpl {

    private final Dns dnsClient = DnsCachedClientFactory.createDnsCachedClient();

    public void setTimestamps(String clientName, String serviceName, String functionName) {
        String resolvedSocket = dnsClient.resolve(clientName, "setTimestamp");
        if (resolvedSocket != null && !resolvedSocket.isEmpty()) {
            new RawRpcClientImpl(resolvedSocket).invoke(
                "setTimestamp", 
                new RpcValue.StringValue(serviceName),
                new RpcValue.StringValue(functionName),
                new RpcValue.LongValue((System.currentTimeMillis() & 0xFFFFFFFFL)) // FIXME
            );
        }
    }
}
