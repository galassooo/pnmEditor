package ch.supsi.business.filter;

import ch.supsi.business.filter.command.FilterCommand;
import ch.supsi.dataaccess.translations.TranslationsDataAccess;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class FilterFactory {
    private static final Map<String, FilterCommand> filters = new HashMap<>();

    static {
        load();
    }

    private static void load() {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackages("ch.supsi.business.filter.command")
                .addScanners(Scanners.SubTypes)
                .setExpandSuperTypes(false));

        Set<Class<? extends FilterCommand>> classes = reflections.getSubTypesOf(FilterCommand.class);

        for(var c : classes){
            try {
                if(Modifier.isAbstract(c.getModifiers())){
                    continue;
                }
                var constructor = c.getDeclaredConstructor();
                constructor.setAccessible(true);

                FilterCommand command = constructor.newInstance();

                AtomicBoolean process = new AtomicBoolean(true);
                TranslationsDataAccess tac = TranslationsDataAccess.getInstance();
                tac.getSupportedLanguageTags().forEach(tag -> {
                    Properties p = tac.getTranslations(Locale.forLanguageTag(tag));
                    if(p.get(command.getName()) == null){
                        System.out.println("\u001B[33m[WARNING] \u001B[0m" +
                                "filter " + command.getName() +
                                " should have a translation associated. Update language bundle(" +
                                tag + ") to get the filter processed");
                        process.set(false);
                    }
                });

                if(process.get()){
                    filters.put(command.getName(), command);
                }

            } catch (Exception e) {
                System.err.println("Error loading filter command: " + c.getName());
            }
        }
    }

    public static Map<String, FilterCommand> getFilters() {
        return filters;
    }

    public static void reload(){
        load();
    }
}