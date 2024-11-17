package org.supsi.view.image;

/**
 * Represents an event related to exporting images in the application.
 * This is a sealed interface to define all possible export-related events.
 */
public sealed interface ExportEvent {

    /**
     * Represents an event indicating that an export has been requested.
     *
     * @param extension the file extension for the export (e.g., "png", "jpg")
     */
    record ExportRequested(String extension) implements ExportEvent {}
}
