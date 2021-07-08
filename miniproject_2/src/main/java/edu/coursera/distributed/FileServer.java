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
     * @throws IOException If an I/O error is detected on the server. This
     *                     should be a fatal error, your file server
     *                     implementation is not expected to ever throw
     *                     IOExceptions during normal operation.
     */
    public void run(final ServerSocket socket, final PCDPFilesystem fs)
            throws IOException {
        /*
         * Enter a spin loop for handling client requests to the provided
         * ServerSocket object.
         */
        while (true) {
            Socket s = socket.accept();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(s.getInputStream()));

            String line = bufferedReader.readLine();
            assert line != null;
            assert line.startsWith("GET");

            PCDPPath pcdpPath = new PCDPPath(line.split(" ")[1]);
            final String fileContent = fs.readFile(pcdpPath);

            OutputStream out = s.getOutputStream();
            PrintWriter printWriter = new PrintWriter(out);

            if (fileContent != null) {
                printWriter.write("HTTP/1.0 200 OK\r\n");
                printWriter.write("Server: FileServer\r\n");
                printWriter.write("\r\n");
                printWriter.write(fileContent + "\r\n");
                printWriter.flush();
            } else {
                printWriter.write("HTTP/1.0 404 Not Found\r\n");
                printWriter.write("Server: FileServer\r\n");
                printWriter.write("\r\n");
                printWriter.flush();
            }
            out.close();
        }
    }
}
