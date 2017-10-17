package javapoc.framework.modifier;

import java.util.function.Function;

public interface Modifier<T> extends Function<T, T> {

    /**
     * Function to modify an item.
     *
     * @param item to be modified.
     * @return modified object.
     */
    @Override
    T apply(T item);
}
