package javapoc.httpserverwrapper;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ext.RuntimeDelegate;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Created by Jim Sverkmo
 */
public class HttpServerJerseyJdk implements Closeable {

    private final URI uri;
    private final HttpServer server;
    private final Map<String, HttpContext> contextMap;

    public HttpServerJerseyJdk(URI uri) throws IOException {
        this.uri = uri;
        this.server = HttpServer.create(new InetSocketAddress(
                uri.getHost(),
                uri.getPort()
        ), 0);
        this.contextMap = new HashMap<>();
    }

    public URI getURI() {
        return uri;
    }

    public HttpServerJerseyJdk start(Executor executor) {
        server.setExecutor(executor);
        server.start();

        return this;
    }

    public HttpServerJerseyJdk start() {
        final int threads = Runtime.getRuntime().availableProcessors() * 4;
        final ExecutorService executor = Executors.newFixedThreadPool(threads);
        return start(executor);
    }

    public HttpServerJerseyJdk register(String path, HttpHandler handler) {
        HttpContext context = server.createContext(path, handler);
        contextMap.put(path, context);
        return this;
    }

    public HttpServerJerseyJdk register(String path, ResourceConfig resource) {
        return register(path, RuntimeDelegate.getInstance().createEndpoint(resource, HttpHandler.class));
    }

    public void stop() {
        stop(0);
    }

    public void stop(int i) {
        server.stop(i);
    }

    @Override
    public void close() throws IOException {
        stop();
    }

    @Override
    public String toString() {
        return Arrays.asList(
                "URI: " + uri.toString(),
                "Address: " + server.getAddress(),
                "Resources:",
                contextMap.entrySet().stream()
                        .map(entry -> String.format("%s: %s", entry.getKey(), entry.getValue().getPath()))
                        .map(s -> "  " + s)
                        .collect(Collectors.joining("\n"))
        ).stream().collect(Collectors.joining("\n"));
    }

}
