package javapoc.framework.coder;


public interface Decoder<Input, T> {

    /**
     * Function to decode input into an item.
     *
     * @param input to be decoded.
     * @return decoded item.
     */
    T decode(Input input) throws CoderException;
}
