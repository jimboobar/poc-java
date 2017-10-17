package javapoc.coders.jsoncoder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import javapoc.framework.coder.Coder;
import javapoc.framework.coder.CoderException;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JsonCoderTest {


    private Path resourceDir;


    @Before
    public void setup() {
        resourceDir = Paths.get("src/test/resources");
    }


    @Test
    public void shouldReadJsonFromFileWithDefaultCoder() throws Exception {
        JsonNode json = JsonCoder.DEFAULT_CODER.decode(Files.newInputStream(
                resourceDir.resolve("test.json")));

        assertEquals(42, json.get("int").asInt());
        assertEquals("test", json.get("text").asText());
        assertTrue(json.get("boolean").asBoolean());
    }


    @Test(expected = CoderException.class)
    public void shouldThrowExceptionWhenMissingAsStringItem() {
        JsonCoder.DEFAULT_CODER.asString(null);
    }


    @Test
    public void shouldReturnStringForTestModel() {
        Coder<TestModel> coder = new JsonCoder<>(TestModel.class);

        assertEquals(
                "{\"key\":\"foo\",\"value\":\"bar\"}",
                coder.asString(new TestModel("foo", "bar")));
    }


    @Test
    public void shouldReadTestModelFromFile() throws Exception {
        Coder<TestModel> coder = new JsonCoder<>(TestModel.class);

        TestModel model = coder.decode(Files.newInputStream(
                resourceDir.resolve("testmodel.json")));

        assertEquals("foo", model.key);
        assertEquals("bar", model.value);
    }


    @Test
    public void shouldEncodeTestModelToOutputStream() {
        new JsonCoder<>(TestModel.class).encode(
                new ByteArrayOutputStream(1024),
                new TestModel("foo", "bar"));
    }


    @Test(expected = CoderException.class)
    public void shouldThrowExceptionWhenMissingClassType() {
        new JsonCoder<>(null);
    }


    @Test(expected = CoderException.class)
    public void shouldThrowExceptionWhenDecodingInvalidInputStream() {
        JsonCoder.DEFAULT_CODER.decode(null);
    }


    @Test(expected = CoderException.class)
    public void shouldThrowExceptionWhenEncodingInvalidOutputStream() {
        new JsonCoder<>(TestModel.class).encode(
                null,
                new TestModel("foo", "bar"));
    }


    @Test(expected = CoderException.class)
    public void shouldThrowExceptionWhenEncodingInvalidItem() {
        new JsonCoder<>(TestModel.class).encode(
                new ByteArrayOutputStream(128),
                null);
    }


    private static class TestModel {
        public final String key;
        public final String value;

        @JsonCreator
        public TestModel(
                @JsonProperty("key") String key,
                @JsonProperty("value") String value) {
            this.key = key;
            this.value = value;
        }
    }
}
