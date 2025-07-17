package org.robotcontrol.middleware.internal.dns;

public final class DnsCachedClientFactory {
    public static volatile DnsCachedClientImpl SHARED_DNS_CACHED_CLIENT;
    private static final Object LOCK = new Object(); // For synchronization

    public static DnsCachedClientImpl createDnsCachedClient() {
        if (SHARED_DNS_CACHED_CLIENT == null) {
            synchronized (LOCK) {
                if (SHARED_DNS_CACHED_CLIENT == null) {
                    SHARED_DNS_CACHED_CLIENT = new DnsCachedClientImpl();
                }
            }
        }
        
        return SHARED_DNS_CACHED_CLIENT;
    }

}
