import java.util.Set;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Überwacht Komponenten auf regelmäßige Check-Ins und benachrichtigt zu-gewiesene Observer
 */
public class WatchDog extends ServerStub{

    private final long timeoutMillis;
    private final ScheduledExecutorService scheduler;
    
    // Karte von Komponente -> aktueller Timeout-Task
    private final ConcurrentMap<String, ScheduledFuture<?>> timeoutTasks = new ConcurrentHashMap<>();
    
    // Karte von Komponente -> Menge registrierter Observer
    private final ConcurrentMap<String, CopyOnWriteArraySet<HealthObserver>> observers = new ConcurrentHashMap<>();

    /**
     * Erzeugt einen WatchDog mit Timeout und Scheduler.
     * @param timeoutMillis Timeout in Millisekunden
     * @param scheduler Executor-Service zum Planen der Timeouts
     */
    public WatchDog(long timeoutMillis, ScheduledExecutorService scheduler) {
        this.timeoutMillis = timeoutMillis;
        this.scheduler = scheduler;
    }

    /**
     * Meldet einen Observer für eine bestimmte Komponente an.
     */
    public void subscribe(String componentId, HealthObserver observer) {
        observers
            .computeIfAbsent(componentId, id -> new CopyOnWriteArraySet<>())
            .add(observer);
    }

    /**
     * Entfernt einen Observer für eine bestimmte Komponente.
     */
    public void unsubscribe(String componentId, HealthObserver observer) {
        CopyOnWriteArraySet<HealthObserver> set = observers.get(componentId);
        if (set != null) {
            set.remove(observer);
            if (set.isEmpty()) {
                observers.remove(componentId);
            }
        }
    }

    /**
     * Muss von einer Komponente periodisch aufgerufen werden, um sie als gesund zu markieren.
     */
    public void checkIn(String componentId) {
        resetWatchdogTimeout(componentId);
        notifyObservers(componentId, true);
    }

    /**
     * Setzt oder erneuert den Timeout-Task für die Komponente.
     */
    private void resetWatchdogTimeout(String componentId) {
        // Alten Task abbrechen
        ScheduledFuture<?> existing = timeoutTasks.get(componentId);
        if (existing != null) {
            existing.cancel(false);
        }
        
        // Neuen Timeout-Task planen
        Runnable expire = () -> {
            timeoutTasks.remove(componentId);
            notifyObservers(componentId, false);
        };
        ScheduledFuture<?> future = scheduler.schedule(expire, timeoutMillis, TimeUnit.MILLISECONDS);
        timeoutTasks.put(componentId, future);
    }

    /**
     * Benachrichtigt alle Observer einer Komponente über deren Status.
     */
    private void notifyObservers(String componentId, boolean healthy) {
        CopyOnWriteArraySet<HealthObserver> set = observers.get(componentId);
        if (set != null) {
            for (HealthObserver obs : set) {
                if (healthy) {
                    obs.onHealthy(componentId);
                } else {
                    obs.onUnhealthy(componentId);
                }
            }
        }
    }

    /**
     * Liefert die aktuell als gesund überwachten Komponenten (d.h. mit laufendem Timeout).
     */
    public Set<String> getHealthyComponents() {
        return Collections.unmodifiableSet(timeoutTasks.keySet());
    }
}

/**
 * Interface für Health-Observer.
 */
interface HealthObserver {
    void onHealthy(String componentId);
    void onUnhealthy(String componentId);
}