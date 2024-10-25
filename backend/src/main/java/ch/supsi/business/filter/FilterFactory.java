package ch.supsi.business.filter;

import ch.supsi.business.filter.strategy.NamedFilterStrategy;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class FilterFactory {
    private static final Map<String, NamedFilterStrategy> filters = new HashMap<>();

    static {
        load();
    }
    private static void load() {

        //Create a new reflections invalidating cache (used for tests)
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackages("ch.supsi.business.filter.strategy")
                .addScanners(Scanners.SubTypes)
                .setExpandSuperTypes(false));

        Set<Class<? extends NamedFilterStrategy>> classes = reflections.getSubTypesOf(NamedFilterStrategy.class);

        for(var c : classes){
            try {
                var constructor = c.getDeclaredConstructor();
                constructor.setAccessible(true); // Questo evita l'IllegalAccessException

                // Prova a istanziare la classe
                NamedFilterStrategy strategy = constructor.newInstance(); // Questo solleva InstantiationException se è astratta

                filters.put(strategy.getName(), strategy);

            } catch (NoSuchMethodException e) {
                //System.out.println("No default constructor found for class: " + c.getName()+ " this class wont be taken into consideration for filter processing");
            } catch (InstantiationException e) {
                //System.out.println("Abstract class with NamedFilterStrategy annotation detected:" + c.getName()+ " this class wont be taken into consideration for filter processing");
            } catch (InvocationTargetException | IllegalAccessException e ){
                //System.out.println("Constructor has thrown an exception in class: " + c.getName() + " this class wont be taken into consideration for filter processing");
            }
        }
    }

    public static void reload(){
        load();
    }
        public static Map<String, NamedFilterStrategy> getFilters () {
            return filters;
        }
    }
