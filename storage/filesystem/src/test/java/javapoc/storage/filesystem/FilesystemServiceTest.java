package javapoc.storage.filesystem;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javapoc.coders.jsoncoder.JsonCoder;
import javapoc.framework.Model;
import javapoc.framework.storage.StorageException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FilesystemServiceTest {


    private Path resourcePath;
    private Path tempPath;
    private FilesystemService<TestModel> service;


    @Before
    public void setup() throws Exception {
        resourcePath = Paths.get("src/test/resources/");
        tempPath = resourcePath.resolve("temp/");

        if (!Files.isDirectory(tempPath))
            Files.createDirectories(tempPath);

        service = new FilesystemService<>(
                tempPath,
                new JsonCoder<>(TestModel.class),
                ".json");
    }


    @After
    public void tearDown() throws Exception {
        Collection<String> keys = service.keys();

        assertTrue(keys.stream()
                .map(k -> service.delete(k))
                .allMatch(Objects::nonNull));

        Files.deleteIfExists(tempPath);
    }


    @Test(expected = StorageException.class)
    public void shouldNotCreateServiceIfPathInvalid() {
        new FilesystemService<TestModel>(null, null);
    }


    @Test(expected = StorageException.class)
    public void shouldNotCreateServiceIfPathNotDirectory() {
        new FilesystemService<TestModel>(resourcePath.resolve("test.json"), null);
    }


    @Test(expected = StorageException.class)
    public void shouldNotCreateServiceIfCoderInvalid() {
        new FilesystemService<TestModel>(tempPath, null);
    }


    @Test
    public void shouldReturnPath() {
        assertEquals(tempPath, service.getPath());
    }


    @Test
    public void shouldReturnClassType() {
        assertEquals(TestModel.class, service.getClassType());
    }


    @Test
    public void shouldCreateFiles() {
        assertTrue(Stream.of("test1", "test2")
                .map(TestModel::new)
                .map(model -> service.create(model))
                .allMatch(Objects::nonNull));
    }

    @Test
    public void shouldCreateThenReadAndUpdate() {
        assertTrue(Stream.of("test1", "test2")
                .map(TestModel::new)
                .map(model -> service.create(model))
                .map(model -> service.read(model.getIdentifier()))
                .map(model -> {
                    model.status = "updated";
                    return service.update(model);
                })
                .map(model -> service.read(model.getIdentifier()))
                .map(model -> model.status)
                .allMatch("updated"::equals));
    }

    @Test
    public void shouldCreateAndDeleteThenVerifyWithKeys() {
        assertTrue(Stream.of("test1", "test2")
                .map(TestModel::new)
                .map(model -> service.create(model))
                .map(model -> service.delete(model.getIdentifier()))
                .allMatch(Objects::nonNull));

        assertTrue(service.keys().isEmpty());
    }


    @Test(expected = StorageException.class)
    public void shouldNotResolveInvalidIdentifier() {
        // force StorageException to be thrown on create
        service.read(null);
    }


    @Test(expected = StorageException.class)
    public void shouldNotResolveEmptyIdentifier() {
        // force StorageException to be thrown on create
        service.read("");
    }


    @Test(expected = StorageException.class)
    public void shouldNotCreateIfFileExists() {
        // create file
        service.create(new TestModel("create"));
        // force StorageException to be thrown on create
        service.create(new TestModel("create"));
    }


    @Test(expected = StorageException.class)
    public void shouldNotReadIfFileDoesNotExists() {
        // force StorageException to be thrown on create
        service.read("test");
    }


    @Test(expected = StorageException.class)
    public void shouldNotUpdateIfFileDoesNotExists() {
        // force StorageException to be thrown on update
        service.update(new TestModel("update"));
    }


    @Test(expected = StorageException.class)
    public void shouldNotDeleteIfFileDoesNotExists() {
        // force StorageException to be thrown on update
        service.delete("delete");
    }


    private static class TestModel implements Model {

        public final String key;
        public String status;

        @JsonCreator
        public TestModel(
                @JsonProperty("key") String key,
                @JsonProperty("status") String status
        ) {
            this.key = key;
            this.status = status;
        }

        public TestModel(String key) {
            this(key, "created");
        }

        @Override
        @JsonIgnore
        public String getIdentifier() {
            return key;
        }
    }

}
