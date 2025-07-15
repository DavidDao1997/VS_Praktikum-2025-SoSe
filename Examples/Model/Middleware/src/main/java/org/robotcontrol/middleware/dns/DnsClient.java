package org.robotcontrol.middleware.dns;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.robotcontrol.middleware.ServerStub_I;
import org.robotcontrol.middleware.idl.DnsClientCallbackService;
import org.robotcontrol.middleware.rpc.RpcClient;
import org.robotcontrol.middleware.rpc.RpcServer;
import org.robotcontrol.middleware.rpc.RpcUtils;
import org.robotcontrol.middleware.rpc.RpcValue;
import org.robotcontrol.middleware.utils.Logger;

public class DnsClient implements Dns {
    private final Logger logger = new Logger("DnsService");
    private RpcClient client;

    /** Simple cache entry for a resolved name + timestamp. */
    private static class CacheEntry {
        final String value;
        final long timestamp;
        CacheEntry(String value, long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }
    }


    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    // Cache TTL of 60 seconds
    private static final long CACHE_TTL_MS = 60_000L;

    public DnsClient() {
        // FIXME hardcode DNS server socket in a constant somewhere
        this("localhost:9000");
        //this("172.16.1.87:9000");
    }

    public DnsClient(String socket) {
        String[] socketParts = socket.split(":", 2);
        client = new RpcClient(socketParts[0], Integer.parseInt(socketParts[1]));
    }

    public void register(
        String serviceName, 
        String functionName, 
        String socket
    ) {
        client.invoke(
            "register", 
            new RpcValue.StringValue(serviceName), 
            new RpcValue.StringValue(functionName), 
            new RpcValue.StringValue(socket)
        );
    }

    public String resolve(
        String serviceName, 
        String functionName
    ) { 
        // Check cache first
        String key = serviceName + "." + functionName;
        // Print cache for debugging
 
        CacheEntry cached = cache.get(key);
        if (cached != null){ //System.currentTimeMillis() - cached.timestamp < CACHE_TTL_MS) {
            
            logger.debug("Cache hit for %s -> %s", key, cached.value);
            return cached.value;
        }

        CompletableFuture<String> resolutionFuture = new CompletableFuture<>();
        RpcServer cbServer = new RpcServer();
        String callbackHostport = "";
        try {
            DatagramSocket socket = new DatagramSocket();
            callbackHostport = getIp() + ":" + Integer.toString(socket.getLocalPort());
            // FIXME requires new feature that lets us pass a full socket definition too the server
            cbServer.addService(new DnsClientCallbackServiceImpl(resolutionFuture), socket);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        cbServer.Listen();
        // wait as the server setup is not instant
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        client.invoke(
            "resolve",
            new RpcValue.StringValue(serviceName),
            new RpcValue.StringValue(functionName),
            new RpcValue.StringValue(callbackHostport)
        );

        try {
            
            String resolved = resolutionFuture.get(5, TimeUnit.SECONDS);
            // Store in cache before returning
            if (!"".equals(resolved) && (resolved != null)) {
                logger.debug("Cache store for %s -> %s", key, resolved);
            
                cache.put(key, new CacheEntry(resolved, System.currentTimeMillis()));
                // Print cache after update
            
            }
            cbServer.stop();
            return resolved;
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            System.err.println("DnsClient: Resolution failed for " + key);
        }
        return "";
    }

    private class DnsClientCallbackServiceImpl implements DnsClientCallbackService, ServerStub_I {
        private final CompletableFuture<String> resolutionFuture;

        private DnsClientCallbackServiceImpl(CompletableFuture<String> resolutionFuture) {
            this.resolutionFuture = resolutionFuture;
        }

        @Override
        public void call(String fnName, RpcValue... args) {
            receiveResolution((String) RpcUtils.unwrap(args[0]), (String) RpcUtils.unwrap(args[1]), (String) RpcUtils.unwrap(args[2]));
        }

        @Override
        public void receiveResolution(String _serviceName, String _fnName, String resolvedSocket) {
            resolutionFuture.complete(resolvedSocket);
        }
    }

    // TODO move to utils
    private String getIp() {
        String envVar = System.getenv("IP_ADDR");
        if (envVar != null && !envVar.isEmpty()) {
            return envVar;
        } else {
            logger.trace("IP_ADDR undefined, defaulting to 127.0.0.1");
            return "127.0.0.1"; // fallback
        }
    }
}