package org.robotcontrol.middlewarev2.internal.dns;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.robotcontrol.middleware.utils.Environment;
import org.robotcontrol.middleware.utils.Logger;
import org.robotcontrol.middlewarev2.idl.types.RpcValue;
import org.robotcontrol.middlewarev2.internal.rpc.RawRpcClientImpl;
import org.robotcontrol.middlewarev2.rpc.Invokable;

public class DnsClientImpl implements Dns {
    private static final Logger logger = new Logger("DnsClientImpl");

    private final Invokable client = new RawRpcClientImpl(Environment.getEnvStringOrExit("DNS_SOCKET"));
    private final DnsCallbackServiceImpl callbackService = DnsCallbackServiceFactory.createDnsCallbackService();
    private static final String DNS_CALLBACK_SOCKET = Environment.getEnvStringOrExit("IP_ADDR") + ":" + Environment.getEnvIntOrExit("DNS_CALLBACK_PORT");

    @Override
    public void register(String serviceName, String functionName, String socket) {
        client.invoke(
            "register",
            new RpcValue.StringValue(serviceName),
            new RpcValue.StringValue(functionName),
            new RpcValue.StringValue(socket)
        );
    }

    @Override
    public String resolve(String serviceName, String functionName) {
        CompletableFuture<String> resolutionFuture = new CompletableFuture<>();
        callbackService.registerFuture(serviceName, functionName, resolutionFuture);
        client.invoke(
            "resolve",
            new RpcValue.StringValue(serviceName),
            new RpcValue.StringValue(functionName),
            new RpcValue.StringValue(DNS_CALLBACK_SOCKET)
        );
        
        try {
            return resolutionFuture.get(100, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            logger.debug("Timeout in resolve: %s", e);
            // throw new RuntimeException(e);
        } catch (InterruptedException | ExecutionException e) {
            logger.debug("Exception in resolve, rethrowing as RuntimeException: %s", e);
            throw new RuntimeException(e);
        }
        return "";
    }
}
