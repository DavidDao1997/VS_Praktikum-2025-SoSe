package org.robotcontrol.middlewarev2.internal.dns;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DnsCachedClientImpl implements Dns {
    // private static final Logger logger = new Logger("DnsCachedClientImpl");
    private final Dns dnsClient;

    private static final Map<String, CacheEntry> SHARED_CACHE = new ConcurrentHashMap<>();
    private static final long CACHE_TTL_MS = 60_000L;

    public DnsCachedClientImpl() {
        dnsClient = new DnsClientImpl();
    }

    @Override
    public void register(String serviceName, String functionName, String socket) {
        dnsClient.register(serviceName, functionName, socket);
    }

    @Override
    public String resolve(String serviceName, String functionName) {
        CacheEntry cached = SHARED_CACHE.get(toKey(serviceName, functionName));
        if (cached != null && System.currentTimeMillis() - cached.timestamp < CACHE_TTL_MS) { 
            return cached.value;
        }

        String resolved = dnsClient.resolve(serviceName, functionName);

        if (!resolved.isEmpty() && resolved != null) {
            SHARED_CACHE.put(toKey(serviceName, functionName), new CacheEntry(resolved, System.currentTimeMillis()));
        }
        return resolved;
    }
    
    public static class CacheEntry {
        final String value;
        final long timestamp;
        CacheEntry(String value, long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }
    }

    private String toKey(String serviceName, String functionName) { return serviceName + "." + functionName; }
}
