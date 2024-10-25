package ch.supsi.business.filter;

import ch.supsi.business.filter.filterStrategy.NamedFilterStrategy;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class FilterFactory {
    private static final Map<String, NamedFilterStrategy> filters = new HashMap<>();

    static {

        //Create a new reflections invalidating cache (used for tests)
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackages("ch.supsi.business.filter.filterStrategy")
                .addScanners(Scanners.SubTypes)
                .setExpandSuperTypes(false));

        Set<Class<? extends NamedFilterStrategy>> classes = reflections.getSubTypesOf(NamedFilterStrategy.class);

        classes.forEach(c -> {

            try {
                NamedFilterStrategy strategy = c.getDeclaredConstructor().newInstance();

                filters.put(strategy.getName(), strategy);

            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                System.out.println("pollo");
            }

        });
    }

        public static Map<String, NamedFilterStrategy> getFilters () {
            return filters;
        }
    }
