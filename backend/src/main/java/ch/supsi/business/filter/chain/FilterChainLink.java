package ch.supsi.business.filter.chain;

import ch.supsi.application.image.WritableImage;
import ch.supsi.business.filter.chain.command.FilterCommand;

import java.util.Objects;

/**
 * Represents a single link in a chain of filters to be applied sequentially to an image.
 * Each link contains a {@link FilterCommand} and may point to the next link in the chain.
 */
public class FilterChainLink {

    private final FilterCommand command;
    private FilterChainLink next;

    /**
     * Constructs a {@link FilterChainLink} with the specified {@link FilterCommand}.
     *
     * @param command the filter command to associate with this chain link
     */
    public FilterChainLink(FilterCommand command) {
        this.command = command;
    }

    /**
     * Executes the filter command on the provided image and, if a next link exists, delegates execution to it.
     * Resets the next link after execution to avoid re-execution.
     *
     * @param image the {@link WritableImage} to process
     */
    public void execute(WritableImage image) {
        command.execute(image);
        if (next != null) {
            next.execute(image);
            next = null; // Reset dopo l'esecuzione
        }
    }

    /**
     * Sets the next link in the chain.
     *
     * @param next the next {@link FilterChainLink}
     */
    public void setNext(FilterChainLink next) {
        this.next = next;
    }

    /**
     * Retrieves the next link in the chain.
     *
     * @return the next {@link FilterChainLink}, or {@code null} if this is the last link
     */
    public FilterChainLink getNext() {
        return next;
    }

    public String getName() {
        return command.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FilterChainLink that)) return false;
        return Objects.equals(command, that.command) && Objects.equals(getNext(), that.getNext());
    }

    @Override
    public int hashCode() {
        return Objects.hash(command, getNext());
    }
}