package org.robotcontrol.middleware.WatchDog;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class WatchdogTest {

    @Test
    public void testFirstHeartbeatNotification() {
        WatchDog wd = new WatchDog();
        SubscriberMock subscriber = new SubscriberMock();
        wd.subscribe("sub1", "A", subscriber);
        // No initial reports before any heartbeat
        assertTrue(subscriber.getReports().isEmpty(), "No initial reports should be sent before any heartbeat");
        wd.heartbeat("A");
        List<SubscriberMock.Report> reports = subscriber.getReports();
        assertEquals(1, reports.size(), "Subscriber should receive one report for first heartbeat");
        assertEquals("A", reports.get(0).getServiceName());
        assertTrue(reports.get(0).isAlive());
    }

    @Test
    public void testNoNotificationOnSecondHeartbeat() {
        WatchDog wd = new WatchDog();
        SubscriberMock subscriber = new SubscriberMock();
        wd.subscribe("sub1", "A", subscriber);
        wd.heartbeat("A");
        subscriber.getReports().clear();
        wd.heartbeat("A");
        assertTrue(subscriber.getReports().isEmpty(), "No reports should be sent on subsequent heartbeats");
    }

    @Test
    public void testSubscribeAfterHeartbeatGetsInitialStatus() {
        WatchDog wd = new WatchDog();
        wd.heartbeat("B");
        SubscriberMock subscriber = new SubscriberMock();
        wd.subscribe("sub2", "B", subscriber);
        List<SubscriberMock.Report> reports = subscriber.getReports();
        assertEquals(1, reports.size(), "Subscriber should receive initial status after subscribing post-heartbeat");
        assertEquals("B", reports.get(0).getServiceName());
        assertTrue(reports.get(0).isAlive());
    }

    @Test
    public void testWildcardSubscription() {
        WatchDog wd = new WatchDog();
        SubscriberMock subscriber = new SubscriberMock();
        wd.subscribe("subAll", "*", subscriber);
        wd.heartbeat("X");
        wd.heartbeat("Y");
        List<SubscriberMock.Report> reports = subscriber.getReports();
        assertEquals(2, reports.size(), "Wildcard subscriber should receive events for both X and Y");
        assertEquals("X", reports.get(0).getServiceName());
        assertTrue(reports.get(0).isAlive());
        assertEquals("Y", reports.get(1).getServiceName());
        assertTrue(reports.get(1).isAlive());
    }
}
