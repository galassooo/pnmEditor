package org.supsi.model.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventManagerTest {

    private EventManager eventManager;
    private List<String> receivedEvents;

    record TestEvent(String message) {}
    record AnotherTestEvent(int value) {}

    @BeforeEach
    void setUp() throws Exception {
        // Reset singleton instance using reflection
        Field instanceField = EventManager.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        eventManager = new EventManager();
        instanceField.set(null, eventManager);
        receivedEvents = new ArrayList<>();
    }

    @Test
    void testGetPublisherReturnsSingletonInstance() {
        EventPublisher publisher1 = EventManager.getPublisher();
        EventPublisher publisher2 = EventManager.getPublisher();

        assertNotNull(publisher1);
        assertSame(publisher1, publisher2);
        assertSame(eventManager, publisher1);
    }

    @Test
    void testGetSubscriberReturnsSingletonInstance() {
        EventSubscriber subscriber1 = EventManager.getSubscriber();
        EventSubscriber subscriber2 = EventManager.getSubscriber();

        assertNotNull(subscriber1);
        assertSame(subscriber1, subscriber2);
        assertSame(eventManager, subscriber1);
    }

    @Test
    void testBasicPublishSubscribe() {
        String expectedMessage = "test message";
        TestEvent testEvent = new TestEvent(expectedMessage);

        eventManager.subscribe(TestEvent.class, event -> {
            assertEquals(expectedMessage, event.message());
            receivedEvents.add(event.message());
        });

        eventManager.publish(testEvent);
        assertEquals(1, receivedEvents.size());
        assertEquals(expectedMessage, receivedEvents.get(0));
    }

    @Test
    void testMultipleSubscribers() {
        TestEvent testEvent = new TestEvent("test");

        eventManager.subscribe(TestEvent.class, event -> receivedEvents.add("handler1"));
        eventManager.subscribe(TestEvent.class, event -> receivedEvents.add("handler2"));

        eventManager.publish(testEvent);

        assertEquals(2, receivedEvents.size());
        assertTrue(receivedEvents.contains("handler1"));
        assertTrue(receivedEvents.contains("handler2"));
    }

    @Test
    void testDifferentEventTypes() {
        eventManager.subscribe(TestEvent.class, event -> receivedEvents.add("TestEvent"));
        eventManager.subscribe(AnotherTestEvent.class, event -> receivedEvents.add("AnotherTestEvent"));

        eventManager.publish(new TestEvent("test"));
        eventManager.publish(new AnotherTestEvent(42));

        assertEquals(2, receivedEvents.size());
        assertEquals("TestEvent", receivedEvents.get(0));
        assertEquals("AnotherTestEvent", receivedEvents.get(1));
    }

    @Test
    void testUnsubscribe() {
        EventHandler<TestEvent> handler = event -> receivedEvents.add(event.message());

        eventManager.subscribe(TestEvent.class, handler);
        eventManager.publish(new TestEvent("first"));

        eventManager.unsubscribe(TestEvent.class, handler);
        eventManager.publish(new TestEvent("second"));

        assertEquals(1, receivedEvents.size());
        assertEquals("first", receivedEvents.get(0));
    }

    @Test
    void testExceptionHandling() {
        eventManager.subscribe(TestEvent.class, event -> {
            throw new RuntimeException("Test exception");
        });

        eventManager.subscribe(TestEvent.class, event -> receivedEvents.add("success"));

        assertDoesNotThrow(() -> eventManager.publish(new TestEvent("test")));
        assertEquals(1, receivedEvents.size());
        assertEquals("success", receivedEvents.get(0));
    }

    @Test
    void testNoHandlersForEvent() {
        assertDoesNotThrow(() -> eventManager.publish(new TestEvent("test")));
        assertTrue(receivedEvents.isEmpty());
    }
}