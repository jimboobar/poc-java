package javapoc.framework.coder;


public interface Encoder<Output, T> {

    /**
     * Function to encode an item.
     *
     * @param output to write to.
     * @param item to encode.
     * @throws CoderException on error.
     */
    void encode(Output output, T item) throws CoderException;
}
