package ch.supsi.business.filter;

import ch.supsi.business.filter.chain.command.FilterCommand;
import ch.supsi.dataaccess.translations.TranslationsDataAccess;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Factory class for managing and providing filter commands in the application.
 * Dynamically loads available filters using reflection and verifies their translations.
 * Provides access to loaded filters and supports runtime reload of filters.
 */
public class FilterFactory {

    private static final List<String> filters;
    private static final Map<String, FilterCommand> filterInstances;

    static {
        filters = new ArrayList<>();
        filterInstances = new ConcurrentHashMap<>();
        load();
    }

    /**
     * Dynamically loads filter commands from the specified package and verifies their translations.
     * Filters without translations are logged with a warning and excluded from the list.
     */
    private static void load() {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackages("ch.supsi.business.filter.chain.command")
                .addScanners(Scanners.SubTypes)
                .setExpandSuperTypes(false));

        Set<Class<? extends FilterCommand>> classes = reflections.getSubTypesOf(FilterCommand.class);

        for (var c : classes) {
            try {
                if (Modifier.isAbstract(c.getModifiers())) {
                    continue; // Skip abstract classes
                }

                // Create a single instance for each filter command
                FilterCommand command = c.getDeclaredConstructor().newInstance();

                // Verify translations for the filter
                AtomicBoolean process = new AtomicBoolean(true);
                TranslationsDataAccess tac = TranslationsDataAccess.getInstance();
                tac.getSupportedLanguageTags().forEach(tag -> {
                    Properties p = tac.getTranslations(Locale.forLanguageTag(tag));
                    if (p.get(command.getName()) == null) {
                        System.out.println("\u001B[33m[WARNING] \u001B[0m" +
                                "Filter " + command.getName() +
                                " should have a translation associated. Update language bundle (" +
                                tag + ") to process the filter.");
                        process.set(false);
                    }
                });

                // Add the filter to the list and map if it passes translation checks
                if (process.get()) {
                    filters.add(command.getName());
                    filterInstances.put(command.getName(), command);
                }

            } catch (Exception e) {
                System.err.println("Error loading filter command: " + c.getName());
            }
        }
    }

    /**
     * Retrieves the list of loaded filter names.
     *
     * @return a {@link List} of filter names
     */
    public static List<String> getFilters() {
        return filters;
    }

    /**
     * Retrieves the {@link FilterCommand} associated with the given filter name.
     *
     * @param name the name of the filter
     * @return the corresponding {@link FilterCommand}, or {@code null} if not found
     */
    public static FilterCommand getFilter(String name) {
        return filterInstances.get(name);
    }

    /**
     * Reloads the filters at runtime, clearing the current filters and reloading them from the specified package.
     * Useful for adding new filters without restarting the application.
     */
    public static void reload() {
        filters.clear();
        filterInstances.clear();
        load();
    }
}
