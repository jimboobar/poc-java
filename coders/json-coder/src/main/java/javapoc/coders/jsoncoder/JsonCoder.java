package javapoc.coders.jsoncoder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javapoc.framework.coder.Coder;
import javapoc.framework.coder.CoderException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class JsonCoder<T> implements Coder<T> {

    private static final ObjectMapper DEFAULT_MAPPER;
    public static final Coder<JsonNode> DEFAULT_CODER;

    static {
        DEFAULT_MAPPER = new ObjectMapper();
        DEFAULT_CODER = new JsonCoder<>(JsonNode.class);
    }


    private final ObjectMapper mapper;
    private final Class<T> coderClass;


    /**
     * Constructor to create a {@link JsonCoder}.
     *
     * @param mapper used for encoding/decoding.
     * @param coderClass to encode from or decode to.
     *
     * @throws CoderException on error.
     */
    public JsonCoder(ObjectMapper mapper, Class<T> coderClass) throws CoderException {
        if (coderClass == null)
            throw new CoderException("Missing <coderClass>");

        this.mapper = mapper;
        this.coderClass = coderClass;
    }


    /**
     * Constructor to create a {@link JsonCoder}.
     *
     * @param coderClass to encode from or decode to.
     *
     * @throws CoderException
     */
    public JsonCoder(Class<T> coderClass) throws CoderException {
        this(DEFAULT_MAPPER, coderClass);
    }


    @Override
    public Class<T> getClassType() {
        return coderClass;
    }


    @Override
    public void encode(OutputStream output, T item) throws CoderException {
        try {
            if (output == null)
                throw new CoderException("Invalid <output>");

            if (item == null)
                throw new CoderException("Invalid <item>");

            mapper.writeValue(output, item);
        } catch (IOException e) {
            throw new CoderException(e);
        }
    }


    @Override
    public T decode(InputStream input) throws CoderException {
        try {
            if (input == null)
                throw new CoderException("Invalid <input>");

            return mapper.readValue(input, coderClass);
        } catch (IOException e) {
            throw new CoderException(e);
        }
    }


    @Override
    public String asString(T item) throws CoderException {
        try {
            if (item == null)
                throw new CoderException("Invalid <item>");

            return mapper.writeValueAsString(item);
        } catch (JsonProcessingException e) {
            throw new CoderException(e);
        }
    }
}
