package ch.supsi.business.filter;

import ch.supsi.business.filter.strategy.NamedFilterStrategy;
import ch.supsi.dataaccess.translations.TranslationsDataAccess;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

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

                AtomicBoolean process = new AtomicBoolean(true);
                TranslationsDataAccess tac = TranslationsDataAccess.getInstance();
                tac.getSupportedLanguageTags().forEach(tag ->{
                    Properties p = tac.getTranslations(Locale.forLanguageTag(tag));
                    if(p.get(strategy.getCode()) == null){
                        String yellow = "\u001B[33m";
                        String reset = "\u001B[0m";
                        System.out.println(yellow+"[WARNING] "+reset+"filter "+strategy.getCode()+" is annotated with NamedFilterStrategy and should have a translation associated. Update language bundle("+tag+") to get the filter processed"+reset);
                        process.set(false);
                    }
                });

                if(process.get()){
                    filters.put(strategy.getCode(), strategy);
                }

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
