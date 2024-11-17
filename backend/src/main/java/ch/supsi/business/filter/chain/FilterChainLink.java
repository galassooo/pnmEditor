package ch.supsi.business.filter.chain;

import ch.supsi.application.image.WritableImage;
import ch.supsi.business.filter.chain.command.FilterCommand;
import java.util.Objects;

public class FilterChainLink {
    private final FilterCommand command;
    private FilterChainLink next;

    public FilterChainLink(FilterCommand command) {
        this.command = command;
    }

    public void execute(WritableImage image) {
        command.execute(image);
        if (next != null) {
            next.execute(image);
            next = null; // Reset dopo l'esecuzione
        }
    }

    public void setNext(FilterChainLink next) {
        this.next = next;
    }

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