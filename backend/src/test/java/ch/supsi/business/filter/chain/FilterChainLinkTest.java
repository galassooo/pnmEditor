package ch.supsi.business.filter.chain;

import ch.supsi.application.image.WritableImage;
import ch.supsi.business.filter.chain.command.FilterCommand;
import ch.supsi.business.filter.chain.command.HorizontalMirrorCommand;
import ch.supsi.business.filter.chain.command.NegativeCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FilterChainLinkTest {

    private FilterCommand mockCommand;
    private FilterChainLink chainLink;

    @BeforeEach
    void setUp() {
        mockCommand = mock(FilterCommand.class);
        chainLink = new FilterChainLink(mockCommand);
    }

    @Test
    void testExecuteSingleLink() {
        WritableImage mockImage = mock(WritableImage.class);

        chainLink.execute(mockImage);

        verify(mockCommand).execute(mockImage);
    }

    @Test
    void testExecuteChainOfLinks() {
        WritableImage mockImage = mock(WritableImage.class);
        FilterCommand secondMockCommand = mock(FilterCommand.class);
        FilterChainLink secondLink = new FilterChainLink(secondMockCommand);

        chainLink.setNext(secondLink);
        chainLink.execute(mockImage);

        verify(mockCommand).execute(mockImage);
        verify(secondMockCommand).execute(mockImage);
    }

    @Test
    void testResetNextAfterExecution() {
        WritableImage mockImage = mock(WritableImage.class);
        FilterCommand secondMockCommand = mock(FilterCommand.class);
        FilterChainLink secondLink = new FilterChainLink(secondMockCommand);

        chainLink.setNext(secondLink);
        chainLink.execute(mockImage);

        // Execute the chain again to ensure the next link is reset
        chainLink.execute(mockImage);

        verify(mockCommand, times(2)).execute(mockImage);
        verify(secondMockCommand, times(1)).execute(mockImage); // Should only be executed once
    }

    @Test
    void testGetName() {
        when(mockCommand.getName()).thenReturn("MockCommand");

        String name = chainLink.getName();

        assertEquals("MockCommand", name, "The name of the command should match");
    }

    @Test
    void testSetNextAndGetNext() {
        FilterChainLink nextLink = new FilterChainLink(mock(FilterCommand.class));

        chainLink.setNext(nextLink);

        assertSame(nextLink, chainLink.getNext(), "The next link should be the same as set");
    }

    @Test
    void testEqualsAndHashCode() {
        FilterCommand anotherMockCommand = mock(FilterCommand.class);
        FilterChainLink anotherChainLink = new FilterChainLink(mockCommand);

        assertEquals(chainLink, anotherChainLink, "Links with the same command should be equal");
        assertEquals(chainLink.hashCode(), anotherChainLink.hashCode(), "Equal links should have the same hash code");

        chainLink.setNext(new FilterChainLink(anotherMockCommand));
        assertNotEquals(chainLink, anotherChainLink, "Links with different next links should not be equal");
    }

    @Test
    void testEqualsSameObject() {
        FilterChainLink link = new FilterChainLink(mockCommand);
        assertEquals(link, link, "Links with the same command should be equal");
    }

    @Test
    void testDifferentCommand() {
        FilterChainLink link = new FilterChainLink(new NegativeCommand());
        FilterChainLink link2 = new FilterChainLink(new HorizontalMirrorCommand());
        assertNotEquals(link2, link);
    }
}
