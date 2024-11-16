package ch.supsi.business.filter.chain;

import ch.supsi.application.image.ImageBusinessInterface;
import ch.supsi.business.filter.chain.command.FilterCommand;

public class FilterChainLink {
    private final FilterCommand command;
    private FilterChainLink next;

    public FilterChainLink(FilterCommand command) {
        this.command = command;
    }

    public void execute(ImageBusinessInterface image) {
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
}