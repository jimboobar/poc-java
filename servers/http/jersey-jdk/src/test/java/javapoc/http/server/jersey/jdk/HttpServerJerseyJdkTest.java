package javapoc.http.server.jersey.jdk;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

/**
 * Created by Jim Sverkmo
 */
public class HttpServerJerseyJdkTest {


    private Client client;


    @Before
    public void setUp() {
        this.client = ClientBuilder.newClient();
        this.client.property(ClientProperties.READ_TIMEOUT, 500);
    }

    @After
    public void tearDown() {
        this.client.close();
        this.client = null;
    }

    @Test
    public void shouldBeTree() throws IOException {
        try (HttpServerJerseyJdk server = new HttpServerJerseyJdk(URI.create("http://localhost:6789"))
                .map("/tree", new ResourceConfig(TreeResource.class)).noConfiguration()) {
            System.out.println(server.toString());
            server.start();
            assertEquals("Tree!", client.target(server.getURI())
                    .path("/tree")
                    .request()
                    .get(String.class));
        }
    }

    @Test
    public void shouldBeTreeBranch() throws IOException {
        try (HttpServerJerseyJdk server = new HttpServerJerseyJdk(URI.create("http://localhost:6789"))
                .map(new ResourceConfig(TreeBranchResource.class))
                .to("/tree")
                .with(Collections.emptyMap())) {
            System.out.println(server.toString());
            server.start();
            assertEquals("Tree branch!", client.target(server.getURI())
                    .path("/tree/branch")
                    .request()
                    .get(String.class));
        }
    }


    @Path("/")
    public static class TreeResource {
        @GET
        public String get() {
            return "Tree!";
        }
    }

    @Path("/branch")
    public static class TreeBranchResource {
        @GET
        public String get() {
            return "Tree branch!";
        }
    }
}
