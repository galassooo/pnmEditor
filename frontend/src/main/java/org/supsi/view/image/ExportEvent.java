package org.supsi.view.image;

public sealed interface ExportEvent {
    record ExportRequested(String extension) implements ExportEvent {}
}
