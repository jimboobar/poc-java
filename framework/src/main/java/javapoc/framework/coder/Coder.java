package javapoc.framework.coder;

import java.io.InputStream;
import java.io.OutputStream;

public interface Coder<T> extends Encoder<OutputStream, T>, Decoder<InputStream, T> {


    /**
     * Method returning the class type.
     *
     * @return the class type
     */
    Class<T> getClassType();


    /**
     * Encodes an item to the given {@link OutputStream}.
     *
     * @param output the stream to write to.
     * @param item the object to encode.
     * @throws CoderException when operation can't be performed.
     */
    @Override
    void encode(OutputStream output, T item) throws CoderException;


    /**
     * Decodes an item from the given {@link InputStream}.
     *
     * @param input the stream to read from.
     * @return encoded item.
     * @throws CoderException when operation can't be performed.
     */
    @Override
    T decode(InputStream input) throws CoderException;


    /**
     * Takes an item and encodes it to a {@link String}.
     *
     * @param item the object to encode.
     * @return object encoded as a string.
     * @throws CoderException when operation can't be performed.
     */
    String asString(T item) throws CoderException;

}
