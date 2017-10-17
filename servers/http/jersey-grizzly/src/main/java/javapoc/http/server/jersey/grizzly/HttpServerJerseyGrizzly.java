package javapoc.http.server.jersey.grizzly;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpHandlerRegistration;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpContainer;
import org.glassfish.jersey.server.ContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Jim Sverkmo
 */
public class HttpServerJerseyGrizzly implements Closeable {

    private final URI uri;
    private final HttpServer server;

    public HttpServerJerseyGrizzly(URI uri) throws IOException {
        this.uri = uri;
        this.server = HttpServer.createSimpleServer(null, new InetSocketAddress(uri.getHost(), uri.getPort()));
    }

    public URI getURI() {
        return uri;
    }

    public void start() throws IOException {
        if (server.isStarted())
            throw new RuntimeException("Server already started!");

        server.start();
    }

    public ContextBuilder map(HttpHandler handler) {
        return new ContextBuilder(handler);
    }

    public ContextBuilder map(ResourceConfig resource) {
        return map(ContainerFactory.createContainer(GrizzlyHttpContainer.class, resource));
    }

    public HttpServerJerseyGrizzly map(String path, HttpHandler handler) {
        return map(handler).to(path);
    }

    public HttpServerJerseyGrizzly map(String path, ResourceConfig resource) {
        return map(resource).to(path);
    }

    public void stop() {
        server.shutdownNow();
    }

    @Override
    public void close() throws IOException {
        stop();
    }

    @Override
    public String toString() {
        return Stream.of(
                "URI:     " + uri.toString(),
                "Address: " + server.getListeners().stream()
                        .map(NetworkListener::getHost)
                        .collect(Collectors.joining(", ")),
                "Resources:",
                server.getServerConfiguration().getHttpHandlersWithMapping().entrySet().stream()
                        .map(entry -> Arrays.stream(entry.getValue())
                                        .map(HttpHandlerRegistration::getContextPath)
                                        .collect(Collectors.joining(", ")))
                        .map(s -> "  " + s)
                        .collect(Collectors.joining("\n"))
        ).collect(Collectors.joining("\n"));
    }


    public class ContextBuilder {

        private final HttpHandler handler;

        private ContextBuilder(HttpHandler handler) {
            this.handler = handler;
        }

        public HttpServerJerseyGrizzly to(String path) {
            server.getServerConfiguration().addHttpHandler(handler, path);

            return HttpServerJerseyGrizzly.this;
        }

    }

}
