package org.robotcontrol.middlewarev2.internal.idl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.robotcontrol.middleware.utils.Logger;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class WatchdogServiceTest {

    // // Instance of the service under test
    // private WatchdogImpl.Service watchdogService;

    // // Mocked static classes
    // private MockedStatic<Instant> mockedInstant;
    // private MockedStatic<HealthReportConsumerImpl.Client> mockedClient;
    // private MockedStatic<Logger> mockedLogger; // Mocking the Logger class
    // private MockedStatic<Executors> mockedExecutors; // To mock the scheduler

    // // Mock objects
    // private Logger mockLogger;
    // private HealthReportConsumerImpl.Client mockHealthReportClientA;
    // private HealthReportConsumerImpl.Client mockHealthReportClientB;
    // private ScheduledExecutorService mockScheduler;


    // @Before
    // public void setUp() {
    //     // Mock Instant.now() to control time
    //     mockedInstant = mockStatic(Instant.class);
    //     mockedInstant.when(Instant::now).thenReturn(Instant.ofEpochMilli(100000)); // Start at a fixed time

    //     // Mock HealthReportConsumerImpl.Client constructor
    //     mockedClient = mockStatic(HealthReportConsumerImpl.Client.class);
    //     mockHealthReportClientA = mock(HealthReportConsumerImpl.Client.class);
    //     mockHealthReportClientB = mock(HealthReportConsumerImpl.Client.class);

    //     // When new Client("clientA") is called, return mockHealthReportClientA
    //     // When new Client("clientB") is called, return mockHealthReportClientB
    //     mockedClient.when(() -> new HealthReportConsumerImpl.Client("clientA"))
    //             .thenReturn(mockHealthReportClientA);
    //     mockedClient.when(() -> new HealthReportConsumerImpl.Client("clientB"))
    //             .thenReturn(mockHealthReportClientB);
    //     mockedClient.when(() -> new HealthReportConsumerImpl.Client(anyString()))
    //             .thenAnswer(invocation -> {
    //                 String consumerName = invocation.getArgument(0);
    //                 // This allows specific mocks for known names, and generic for others
    //                 if ("clientA".equals(consumerName)) return mockHealthReportClientA;
    //                 if ("clientB".equals(consumerName)) return mockHealthReportClientB;
    //                 // For any other name, return a new generic mock (if not explicitly mocked)
    //                 return mock(HealthReportConsumerImpl.Client.class);
    //             });


    //     // Mock Logger constructor and instance
    //     mockedLogger = mockStatic(Logger.class);
    //     mockLogger = mock(Logger.class);
    //     mockedLogger.when(() -> new Logger(anyString())).thenReturn(mockLogger);

    //     // Mock ScheduledExecutorService
    //     mockedExecutors = mockStatic(Executors.class);
    //     mockScheduler = mock(ScheduledExecutorService.class);
    //     mockedExecutors.when(Executors::newSingleThreadScheduledExecutor)
    //             .thenReturn(mockScheduler);

    //     // Initialize the service. The constructor will now use our mocks.
    //     watchdogService = new WatchdogImpl.Service();

    //     // Verify that scheduler.scheduleAtFixedRate was called once during construction
    //     // We capture the Runnable to manually trigger checkTimeouts
    //     verify(mockScheduler).scheduleAtFixedRate(
    //             any(Runnable.class), // The 'this::checkTimeouts' method reference
    //             eq(2000L),
    //             eq(2000L),
    //             eq(TimeUnit.MILLISECONDS)
    //     );
    // }

    // @After
    // public void tearDown() {
    //     // Ensure the scheduler is shut down after each test
    //     watchdogService.shutdown();
    //     verify(mockScheduler).shutdownNow(); // Verify shutdown was called

    //     // Close static mocks
    //     mockedInstant.close();
    //     mockedClient.close();
    //     mockedLogger.close();
    //     mockedExecutors.close();
    // }

    // // Helper to manually trigger checkTimeouts by capturing the runnable
    // private void triggerCheckTimeouts() throws Exception {
    //     // Use an ArgumentCaptor to get the Runnable passed to scheduleAtFixedRate
    //     var argCaptor = Mockito.argThat((Runnable r) -> true); // A generic captor for Runnable
    //     verify(mockScheduler, atLeastOnce()).scheduleAtFixedRate(
    //             argCaptor,
    //             anyLong(),
    //             anyLong(),
    //             any(TimeUnit.class)
    //     );
    //     Runnable checkTimeoutsRunnable = argCaptor.getValue(); // Get the last captured runnable
    //     checkTimeoutsRunnable.run();
    // }

    // @Test
    // public void testHeartbeat_firstTime_notifiesSubscribers() throws Exception {
    //     // 1. Subscribe a client
    //     watchdogService.subscribe("clientA", "Service*");

    //     // 2. Advance time (no effect yet, as checkTimeouts is not triggered)
    //     mockedInstant.when(Instant::now).thenReturn(Instant.ofEpochMilli(100100));

    //     // 3. Send first heartbeat for "Service1"
    //     watchdogService.heartbeat("Service1");

    //     // Verify logger message for service coming online
    //     verify(mockLogger).info("Heartbeat for %s: now healthy", "Service1");

    //     // Verify clientA was notified for Service1 as alive
    //     verify(mockHealthReportClientA).reportHealth("Service1", true);

    //     // Verify no other calls to clientB
    //     verifyNoInteractions(mockHealthReportClientB);
    // }

    // @Test
    // public void testHeartbeat_subsequentHeartbeat_noNotification() throws Exception {
    //     // 1. Subscribe a client
    //     watchdogService.subscribe("clientA", "Service*");

    //     // 2. First heartbeat
    //     mockedInstant.when(Instant::now).thenReturn(Instant.ofEpochMilli(100100));
    //     watchdogService.heartbeat("Service1");
    //     // Reset mocks after initial setup verification
    //     reset(mockLogger, mockHealthReportClientA);

    //     // 3. Advance time slightly (within timeout)
    //     mockedInstant.when(Instant::now).thenReturn(Instant.ofEpochMilli(100200));

    //     // 4. Send subsequent heartbeat for "Service1"
    //     watchdogService.heartbeat("Service1");

    //     // Verify no logger message for "now healthy" as it was already up
    //     verify(mockLogger, never()).info("Heartbeat for %s: now healthy", "Service1");

    //     // Verify clientA was NOT notified again (only on first heartbeat)
    //     verifyNoInteractions(mockHealthReportClientA);
    // }

    // @Test
    // public void testCheckTimeouts_serviceTimesOut_notifiesSubscribers() throws Exception {
    //     // 1. Subscribe clientA to "Service*"
    //     watchdogService.subscribe("clientA", "Service*");

    //     // 2. Send heartbeat for "Service1" and "Service2"
    //     mockedInstant.when(Instant::now).thenReturn(Instant.ofEpochMilli(100100));
    //     watchdogService.heartbeat("Service1");
    //     mockedInstant.when(Instant::now).thenReturn(Instant.ofEpochMilli(100200));
    //     watchdogService.heartbeat("Service2");
    //     reset(mockLogger, mockHealthReportClientA); // Clear interactions from heartbeats

    //     // 3. Advance time beyond heartbeatTimeout for Service1 (e.g., 2000ms + buffer)
    //     // Service1 last heartbeat: 100100
    //     // Timeout is 2000ms. So at 100100 + 2000 = 102100, it should time out.
    //     mockedInstant.when(Instant::now).thenReturn(Instant.ofEpochMilli(102200)); // Time is 102200

    //     // 4. Manually trigger checkTimeouts
    //     triggerCheckTimeouts();

    //     // Verify logger message for Service1 going unhealthy
    //     verify(mockLogger).info("Watchdog for %s: now unhealthy", "Service1");

    //     // Verify clientA was notified for Service1 as NOT alive
    //     verify(mockHealthReportClientA).reportHealth("Service1", false);

    //     // Verify Service2 is still healthy (its last heartbeat was at 100200, so 102200 is not beyond timeout)
    //     verify(mockHealthReportClientA, never()).reportHealth("Service2", false);
    // }

    // @Test
    // public void testCheckTimeouts_multipleSubscribers_allNotified() throws Exception {
    //     // 1. Subscribe clientA to "Service*" and clientB to "S*"
    //     watchdogService.subscribe("clientA", "Service*");
    //     watchdogService.subscribe("clientB", "S*");

    //     // 2. Send heartbeat for "ServiceX"
    //     mockedInstant.when(Instant::now).thenReturn(Instant.ofEpochMilli(100100));
    //     watchdogService.heartbeat("ServiceX");
    //     reset(mockLogger, mockHealthReportClientA, mockHealthReportClientB); // Clear initial heartbeat notifications

    //     // 3. Advance time beyond heartbeatTimeout
    //     mockedInstant.when(Instant::now).thenReturn(Instant.ofEpochMilli(102200));

    //     // 4. Manually trigger checkTimeouts
    //     triggerCheckTimeouts();

    //     // Verify logger message
    //     verify(mockLogger).info("Watchdog for %s: now unhealthy", "ServiceX");

    //     // Verify clientA was notified
    //     verify(mockHealthReportClientA).reportHealth("ServiceX", false);

    //     // Verify clientB was also notified
    //     verify(mockHealthReportClientB).reportHealth("ServiceX", false);
    // }

    // @Test
    // public void testHeartbeat_revivesTimedOutService_notifiesAsHealthy() throws Exception {
    //     // 1. Subscribe clientA
    //     watchdogService.subscribe("clientA", "Service*");

    //     // 2. Send initial heartbeat for "Service3"
    //     mockedInstant.when(Instant::now).thenReturn(Instant.ofEpochMilli(100100));
    //     watchdogService.heartbeat("Service3");
    //     reset(mockLogger, mockHealthReportClientA);

    //     // 3. Advance time and time out Service3
    //     mockedInstant.when(Instant::now).thenReturn(Instant.ofEpochMilli(102200));
    //     triggerCheckTimeouts();
    //     verify(mockHealthReportClientA).reportHealth("Service3", false); // Verify it went down
    //     reset(mockLogger, mockHealthReportClientA);

    //     // 4. Send a new heartbeat for the same service ("Service3")
    //     mockedInstant.when(Instant::now).thenReturn(Instant.ofEpochMilli(102300));
    //     watchdogService.heartbeat("Service3");

    //     // Verify logger message indicating it's now healthy (revived)
    //     verify(mockLogger).info("Heartbeat for %s: now healthy", "Service3");

    //     // Verify clientA was notified again as ALIVE (revival notification)
    //     verify(mockHealthReportClientA).reportHealth("Service3", true);
    // }

    // @Test
    // public void testSubscribe_invalidArgs_throwsException() {
    //     try {
    //         watchdogService.subscribe(null, "pattern");
    //         fail("IllegalArgumentException expected for null subscriberName");
    //     } catch (IllegalArgumentException e) {
    //         assertEquals("subscriberName, patternStr must not be null", e.getMessage());
    //     }

    //     try {
    //         watchdogService.subscribe("client", null);
    //         fail("IllegalArgumentException expected for null patternStr");
    //     } catch (IllegalArgumentException e) {
    //         assertEquals("subscriberName, patternStr must not be null", e.getMessage());
    //     }
    // }

    // @Test
    // public void testHeartbeat_invalidArgs_throwsException() {
    //     try {
    //         watchdogService.heartbeat(null);
    //         fail("IllegalArgumentException expected for null serviceName");
    //     } catch (IllegalArgumentException e) {
    //         assertEquals("serviceName must not be null", e.getMessage());
    //     }
    // }

    // @Test
    // public void testCall_subscribeMethod() throws Exception {
    //     // Test `call` method mapping to `subscribe`
    //     watchdogService.call("subscribe", RpcValue.wrap("clientA"), RpcValue.wrap("Host*"));
    //     // This is hard to directly verify without exposing internal maps.
    //     // A better approach would be to follow with a heartbeat and check if clientA is notified.
    //     mockedInstant.when(Instant::now).thenReturn(Instant.ofEpochMilli(100500));
    //     watchdogService.heartbeat("Host123");
    //     verify(mockHealthReportClientA).reportHealth("Host123", true);
    // }

    // @Test
    // public void testCall_heartbeatMethod() throws Exception {
    //     // Subscribe first to get a notification
    //     watchdogService.subscribe("clientA", "Service*");
    //     reset(mockHealthReportClientA); // Clear initial subscription notification

    //     // Test `call` method mapping to `heartbeat`
    //     mockedInstant.when(Instant::now).thenReturn(Instant.ofEpochMilli(100600));
    //     watchdogService.call("heartbeat", RpcValue.wrap("ServiceCalledByRpc"));
    //     verify(mockHealthReportClientA).reportHealth("ServiceCalledByRpc", true);
    // }

    // @Test
    // public void testCall_unknownFunction_throwsException() {
    //     try {
    //         watchdogService.call("unknownFunction", RpcValue.wrap("arg1"));
    //         fail("IllegalArgumentException expected for unknown function");
    //     } catch (IllegalArgumentException e) {
    //         assertEquals("Unknown function unknownFunction", e.getMessage());
    //     }
    // }

    // @Test
    // public void testSubscription_noMatch() throws Exception {
    //     watchdogService.subscribe("clientA", "NoMatch*");
    //     mockedInstant.when(Instant::now).thenReturn(Instant.ofEpochMilli(100700));
    //     watchdogService.heartbeat("ServiceFoo");
    //     verifyNoInteractions(mockHealthReportClientA); // No match, so no notification
    // }

    // @Test
    // public void testSubscription_exactMatch() throws Exception {
    //     watchdogService.subscribe("clientA", "ExactService");
    //     mockedInstant.when(Instant::now).thenReturn(Instant.ofEpochMilli(100800));
    //     watchdogService.heartbeat("ExactService");
    //     verify(mockHealthReportClientA).reportHealth("ExactService", true);
    // }

    // @Test
    // public void testNoSelfNotificationOnFirstHeartbeat() throws Exception {
    //     // A client named "MyService" subscribes to its own pattern "MyService"
    //     watchdogService.subscribe("MyService", "MyService");
    //     // Reset mocks after subscribe
    //     reset(mockLogger, mockHealthReportClientA); // assuming mockHealthReportClientA is used generically if MyService is not explicit

    //     // Perform heartbeat for "MyService"
    //     mockedInstant.when(Instant::now).thenReturn(Instant.ofEpochMilli(100900));
    //     watchdogService.heartbeat("MyService");

    //     // Verify that the "MyService" client does NOT report health for itself.
    //     // The filter `!e.getKey().equals(serviceName)` in `heartbeat` should prevent this.
    //     HealthReportConsumerImpl.Client selfClient = mock(HealthReportConsumerImpl.Client.class);
    //     mockedClient.when(() -> new HealthReportConsumerImpl.Client("MyService")).thenReturn(selfClient);
    //     verify(selfClient, never()).reportHealth(anyString(), anyBoolean());
    // }

    // @Test
    // public void testClientSubscriptionOrderIndependent() throws Exception {
    //     // Subscribe in different order
    //     watchdogService.subscribe("clientB", "Service*");
    //     watchdogService.subscribe("clientA", "ServiceX"); // A more specific one

    //     mockedInstant.when(Instant::now).thenReturn(Instant.ofEpochMilli(101000));
    //     watchdogService.heartbeat("ServiceX");

    //     // Both clients should be notified for ServiceX
    //     verify(mockHealthReportClientA).reportHealth("ServiceX", true);
    //     verify(mockHealthReportClientB).reportHealth("ServiceX", true);

    //     reset(mockHealthReportClientA, mockHealthReportClientB);
    //     // Timeout
    //     mockedInstant.when(Instant::now).thenReturn(Instant.ofEpochMilli(103100));
    //     triggerCheckTimeouts();

    //     verify(mockHealthReportClientA).reportHealth("ServiceX", false);
    //     verify(mockHealthReportClientB).reportHealth("ServiceX", false);
    // }
}
