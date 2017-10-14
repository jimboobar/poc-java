package javapoc.http.server.jersey.jdk;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ext.RuntimeDelegate;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public ContextBuilder map(HttpHandler handler) {
        return new ContextBuilder(handler);
    }

    public ContextBuilder map(ResourceConfig resource) {
        return map(RuntimeDelegate.getInstance().createEndpoint(resource, HttpHandler.class));
    }

    public ContextConfigure map(String path, HttpHandler handler) {
        return map(handler).to(path);
    }

    public ContextConfigure map(String path, ResourceConfig resource) {
        return map(resource).to(path);
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
        return Stream.of(
                "URI: " + uri.toString(),
                "Address: " + server.getAddress(),
                "Resources:",
                contextMap.entrySet().stream()
                        .map(entry -> entry.getValue().getPath())
                        .map(s -> "  " + s)
                        .collect(Collectors.joining("\n"))
        ).collect(Collectors.joining("\n"));
    }


    public class ContextBuilder {

        private final HttpHandler handler;

        private ContextBuilder(HttpHandler handler) {
            this.handler = handler;
        }

        public ContextConfigure to(String path) {
            HttpContext context = server.createContext(path, handler);

            contextMap.put(context.getPath(), context);

            return new ContextConfigure(context);
        }

    }


    public class ContextConfigure {

        private final HttpContext context;

        private ContextConfigure(HttpContext context) {
            this.context = context;
        }

        public HttpServerJerseyJdk with(Map<String, Object> configuration) {
            Map<String, Object> attributes = context.getAttributes();
            configuration.forEach(attributes::put);

            return HttpServerJerseyJdk.this;
        }

        public HttpServerJerseyJdk noConfiguration() {
            return HttpServerJerseyJdk.this;
        }

    }

}
