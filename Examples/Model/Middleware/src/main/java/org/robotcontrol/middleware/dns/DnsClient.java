package org.robotcontrol.middleware.dns;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.robotcontrol.middleware.ServerStub_I;
import org.robotcontrol.middleware.idl.DnsClientCallbackService;
import org.robotcontrol.middleware.idl.DnsService;
import org.robotcontrol.middleware.rpc.RpcClient;
import org.robotcontrol.middleware.rpc.RpcServer;
import org.robotcontrol.middleware.rpc.RpcUtils;
import org.robotcontrol.middleware.rpc.RpcValue;
import org.robotcontrol.middleware.utils.Environment;
import org.robotcontrol.middleware.utils.Logger;

import lombok.Setter;

public class DnsClient implements Dns {
    private static final Logger logger = new Logger("DnsClient");
    private RpcClient client;
    
    private static DnsClientCallbackServiceImpl dnsClientCallbackService;
    private static RpcServer callbackServer;
    private static String callbackHostPort;
    
    /** Simple cache entry for a resolved name + timestamp. */
    private static class CacheEntry {
        final String value;
        final long timestamp;
        CacheEntry(String value, long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }
    }
    
    // In-memory cache: key = "serviceName.functionName"
    private final boolean enableCache; 
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    // Cache TTL of 60 seconds
    private static final long CACHE_TTL_MS = 60_000L;

    public DnsClient() {
        this(Environment.getEnvStringOrExit("DNS_SOCKET"), true);
    }

    public DnsClient(boolean enableCache) {
        this(Environment.getEnvStringOrExit("DNS_SOCKET"), enableCache);
    }

    // constructor seems to be used locally only, so its made private
    private DnsClient(String socket, boolean enableCache) {
        this.enableCache = enableCache;
        String[] socketParts = socket.split(":", 2);
        client = new RpcClient(socketParts[0], Integer.parseInt(socketParts[1]));
        initCallbackServer();
    }

    private static void initCallbackServer() {
        if (dnsClientCallbackService != null) { return; }
        Integer PORT_DNSCALLBACK = Environment.getEnvIntOrExit("PORT_DNSCALLBACK");
        String IP_ADDR = Environment.getEnvStringOrExit("IP_ADDR");

        dnsClientCallbackService = new DnsClientCallbackServiceImpl();
        callbackServer = new RpcServer();
        callbackHostPort = IP_ADDR + ":"+ PORT_DNSCALLBACK;

        callbackServer.addService(PORT_DNSCALLBACK, dnsClientCallbackService);
        callbackServer.Listen();
        logger.info("DnsClientCallbackService listening on %s", PORT_DNSCALLBACK);
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
        if (enableCache) {
            String key = serviceName + "." + functionName;
            CacheEntry cached = cache.get(key);
            if (cached != null && System.currentTimeMillis() - cached.timestamp < CACHE_TTL_MS) {
                logger.debug("Cache hit for %s -> %s", key, cached.value);
                return cached.value;
            }
        }

        CompletableFuture<String> resolutionFuture = new CompletableFuture<>();
        dnsClientCallbackService.registerFuture(serviceName, functionName, resolutionFuture);
        
        client.invoke(
            "resolve",
            new RpcValue.StringValue(serviceName),
            new RpcValue.StringValue(functionName),
            new RpcValue.StringValue(callbackHostPort)
        );
        client.invoke(
            "resolve",
            new RpcValue.StringValue(serviceName),
            new RpcValue.StringValue(functionName),
            new RpcValue.StringValue(callbackHostPort)
        );
        client.invoke(
            "resolve",
            new RpcValue.StringValue(serviceName),
            new RpcValue.StringValue(functionName),
            new RpcValue.StringValue(callbackHostPort)
        );

        try {
            String resolved = resolutionFuture.get(5, TimeUnit.SECONDS);
            // Store in cache before returning
            if (enableCache && !resolved.isEmpty() && resolved != null) {
                String key = serviceName + "." + functionName;
                logger.debug("Cache store for %s -> %s", key, resolved);
            
                cache.put(key, new CacheEntry(resolved, System.currentTimeMillis()));
            }
            return resolved;
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            logger.error("%s", e);
        }
        return "";
    }

    @Setter
    private static class DnsClientCallbackServiceImpl implements DnsClientCallbackService, ServerStub_I {
        private final ConcurrentMap<String, CompletableFuture<String>> futureMap = new ConcurrentHashMap<>();

        private String genKey(String serviceName, String fnName) {
            return serviceName + "." + fnName;
        }

        public void registerFuture(String serviceName, String fnName, CompletableFuture<String> future) {
            futureMap.put(genKey(serviceName, fnName), future);
        }

        @Override
        public void call(String fnName, RpcValue... args) {
            receiveResolution(
                (String) RpcUtils.unwrap(args[0]), 
                (String) RpcUtils.unwrap(args[1]), 
                (String) RpcUtils.unwrap(args[2])
            );
        }

        @Override
        public void receiveResolution(String serviceName, String fnName, String resolvedSocket) {
            CompletableFuture<String> future = futureMap.remove(genKey(serviceName, fnName));
            if (future != null) {
                // logger.debug("YAAAAAAAAAAAAAAAAAAAAAAAAAAY, resolved %s", resolvedSocket);
                future.complete(resolvedSocket);
            } else {
                logger.error("Warning: No future registered for service=%s, fn = %s",serviceName, fnName);
            }
        }
    }

    // TODO move to utils
    private static String getIp() {
        String envVar = System.getenv("IP_ADDR");
        if (envVar != null && !envVar.isEmpty()) {
            return envVar;
        } else {
            logger.debug("IP_ADDR undefined, defaulting to 127.0.0.1");
            return "127.0.0.1"; // fallback
        }
    }

    private static String getDnsIp() {
        String envVar = System.getenv("DNS_SOCKET");
        if (envVar != null && !envVar.isEmpty()) {
            return envVar;
        } else {
            logger.debug("DNS_SOCKET undefined, defaulting to 127.0.0.1:9000");
            return "127.0.0.1:9000"; // fallback
        }
    }
}