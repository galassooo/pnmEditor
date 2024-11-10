package ch.supsi.business.filter;

import ch.supsi.business.filter.chain.FilterChainLink;
import ch.supsi.business.filter.chain.FilterCommand;
import ch.supsi.dataaccess.translations.TranslationsDataAccess;
import com.sun.jdi.ArrayReference;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class FilterFactory {
    private static final List<String> filters = new ArrayList<>();
    private static final Map<String, Constructor<? extends FilterChainLink>> constructors = new HashMap<>();
    static {
        load();
    }

    private static void load() {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackages("ch.supsi.business.filter.chain")
                .addScanners(Scanners.SubTypes)
                .setExpandSuperTypes(false));

        Set<Class<? extends FilterChainLink>> classes = reflections.getSubTypesOf(FilterChainLink.class);

        for(var c : classes){
            try {
                if(Modifier.isAbstract(c.getModifiers())){
                    continue;
                }

                //verify the filter is buildable
                var constructor = c.getDeclaredConstructor();
                constructor.setAccessible(true);

                FilterChainLink command = constructor.newInstance();

                constructors.put(command.getName(), constructor);

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
                    filters.add(command.getName());
                }

            } catch (Exception e) {
                System.err.println("Error loading filter command: " + c.getName());
            }
        }
    }

    public static List<String> getFilters() {
        return filters;
    }

    //FATTO PER EVITARE IL SINGLETON!!!!!!!!!!!!!!!!!!!!!!!!!!!
    //sono oggetti NON - stateless -> aggiungere due filtri uguali causa dipendenza circolare
    public static FilterChainLink getFilter(String name){
        try {
            return constructors.get(name).newInstance();
        }catch (Exception e){
            System.out.println(e.getMessage());
        } //are checked inside the load method
        return null;
    }

    public static void reload(){
        load();
    }
}