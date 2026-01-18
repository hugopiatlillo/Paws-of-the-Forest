package org.warriorcats.pawsOfTheForest.utils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;

/**
 * Utility class for HTTP server operations and file serving.
 * 
 * <p>This class provides functionality to create and manage a lightweight
 * HTTP server for serving files, particularly resource packs to Minecraft
 * clients. It includes methods for starting and stopping the server, and
 * handles file serving with proper HTTP responses.</p>
 * 
 * <p>The server is designed to serve ZIP files (typically resource packs)
 * with appropriate content types and handles file not found scenarios
 * gracefully.</p>
 * 
 * <p>The class is designed as a utility class with static methods only and
 * cannot be instantiated.</p>
 * 
 * @author PawsOfTheForest Team
 * @since 1.0
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class HttpServerUtils {

    /**
     * Default port number for serving resource packs via HTTP.
     */
    public static final int RESOURCES_PACK_PORT = 8175;

    /**
     * The currently running HTTP server instance, or null if no server is running.
     */
    private static HttpServer httpServer;

    /**
     * Starts an HTTP server on the specified port to serve a file at the given route.
     * 
     * <p>If a server is already running, it will be stopped before starting the new one.
     * The server binds to all available interfaces (0.0.0.0) and uses a default
     * executor for handling requests.</p>
     * 
     * @param port the port number to bind the server to
     * @param file the path to the file to serve
     * @param route the HTTP route path where the file will be accessible
     */
    public static void start(int port, Path file, String route) {
        try {
            // Stop existing server if running
            if (httpServer != null) {
                stop();
            }

            // Create and configure HTTP server
            httpServer = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
            httpServer.createContext(route, new FileHandler(file));
            httpServer.setExecutor(null); // Use default executor
            httpServer.start();

            Bukkit.getLogger().info("HTTP server started on port " + port + " serving: " + route);

        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to start HTTP server", e);
        }
    }

    /**
     * Stops the currently running HTTP server if one exists.
     * 
     * <p>This method gracefully shuts down the server and sets the server
     * instance to null. If no server is running, this method does nothing.</p>
     */
    public static void stop() {
        if (httpServer != null) {
            httpServer.stop(0);
            httpServer = null;
            Bukkit.getLogger().info("HTTP server stopped.");
        }
    }

    /**
     * HTTP handler for serving files with appropriate content types and status codes.
     * 
     * <p>This handler serves the specified file as application/zip content type
     * and returns a 404 status if the file doesn't exist.</p>
     */
    private static class FileHandler implements HttpHandler {
        /** The file to serve via HTTP. */
        private final Path file;

        /**
         * Creates a new FileHandler for the specified file.
         * 
         * @param file the file to serve
         */
        public FileHandler(Path file) {
            this.file = file;
        }

        /**
         * Handles HTTP requests by serving the file or returning 404 if not found.
         * 
         * @param exchange the HTTP exchange containing request and response
         * @throws IOException if an I/O error occurs during request handling
         */
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Return 404 if file doesn't exist
            if (!Files.exists(file)) {
                exchange.sendResponseHeaders(404, -1);
                return;
            }

            // Read file content and set response headers
            byte[] content = Files.readAllBytes(file);
            exchange.getResponseHeaders().add("Content-Type", "application/zip");
            exchange.sendResponseHeaders(200, content.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(content);
            }
        }
    }
}
