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

public class FilterFactory {
    private static final List<String> filters = new ArrayList<>();
    private static final Map<String, FilterCommand> filterInstances = new ConcurrentHashMap<>();

    static {
        load();
    }

    private static void load() {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackages("ch.supsi.business.filter.chain.command")
                .addScanners(Scanners.SubTypes)
                .setExpandSuperTypes(false));

        Set<Class<? extends FilterCommand>> classes = reflections.getSubTypesOf(FilterCommand.class);

        for(var c : classes) {
            try {
                if(Modifier.isAbstract(c.getModifiers())) {
                    continue;
                }

                // Creiamo una singola istanza per ogni tipo di filtro
                FilterCommand command = c.getDeclaredConstructor().newInstance();

                // Verifica delle traduzioni
                AtomicBoolean process = new AtomicBoolean(true);
                TranslationsDataAccess tac = TranslationsDataAccess.getInstance();
                tac.getSupportedLanguageTags().forEach(tag -> {
                    Properties p = tac.getTranslations(Locale.forLanguageTag(tag));
                    if(p.get(command.getName()) == null) {
                        System.out.println("\u001B[33m[WARNING] \u001B[0m" +
                                "filter " + command.getName() +
                                " should have a translation associated. Update language bundle(" +
                                tag + ") to get the filter processed");
                        process.set(false);
                    }
                });

                if(process.get()) {
                    filters.add(command.getName());
                    filterInstances.put(command.getName(), command);
                }

            } catch (Exception e) {
                System.err.println("Error loading filter command: " + c.getName());
            }
        }
    }

    public static List<String> getFilters() {
        return filters;
    }

    public static FilterCommand getFilter(String name) {
        return filterInstances.get(name);
    }

    public static void reload() {
        filters.clear();
        filterInstances.clear();
        load();
    }
}