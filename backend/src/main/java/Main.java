import ch.supsi.business.filter.FilterFactory;
import ch.supsi.business.filter.filterStrategy.NamedFilterStrategy;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<String, NamedFilterStrategy> list = FilterFactory.getFilters();


        list.entrySet().stream().forEach(System.out::println);
    }
}
