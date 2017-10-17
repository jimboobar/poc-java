package javapoc.framework.filter;

import java.util.function.Function;

public interface Filter<T> extends Function<T, Boolean> {

    /**
     * Function to filter an item.
     *
     * @param item to be filtered.
     * @return filter result.
     */
    @Override
    Boolean apply(T item);
}
