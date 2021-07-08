package edu.coursera.distributed;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A basic and very limited implementation of a file server that responds to GET
 * requests from HTTP clients.
 */
public final class FileServer {
    /**
     * Main entrypoint for the basic file server.
     *
     * @param socket Provided socket to accept connections on.
     * @param fs     A proxy filesystem to serve files from. See the PCDPFilesystem
     *               class for more detailed documentation of its usage.
     * @param ncores The number of cores that are available to your
     *               multi-threaded file server. Using this argument is entirely
     *               optional. You are free to use this information to change
     *               how you create your threads, or ignore it.
     * @throws IOException If an I/O error is detected on the server. This
     *                     should be a fatal error, your file server
     *                     implementation is not expected to ever throw
     *                     IOExceptions during normal operation.
     */
    public void run(final ServerSocket socket, final PCDPFilesystem fs,
                    final int ncores) throws IOException {
        /*
         * Enter a spin loop for handling client requests to the provided
         * ServerSocket object.
         */
        while (true) {
            Socket s = socket.accept();
            Thread thread = new Thread(() -> {

                BufferedReader bufferedReader = null;
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String line = null;
                try {
                    line = bufferedReader.readLine();
                } catch (IOException e) {

                }
                assert line != null;
                assert line.startsWith("GET");


                PCDPPath path = new PCDPPath(line.split(" ")[1]);

                String file = fs.readFile(path);
                PrintWriter printWriter = null;
                try {
                    printWriter = new PrintWriter(s.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (file != null) {
                    printWriter.write("HTTP/1.0 200 OK\r\n");
                    printWriter.write("Server: FileServer\r\n");
                    printWriter.write("\r\n");
                    printWriter.write(file);
                } else {
                    printWriter.write("HTTP/1.0 404 Not Found\r\n");
                    printWriter.write("Server: FileServer\r\n");
                    printWriter.write("\r\n");
                }
                printWriter.close();
            });
            thread.start();
        }
    }
}
