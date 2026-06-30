package com.cardiogenerator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;


/**
 * Implements the output strategy contract by acting as a TCP server to stream real-time data.
 * This strategy instantiates a network server socket on a designated port and transmits
 * comma-separated data packets to connected network clients asynchronously.
 */
public class TcpOutputStrategy implements OutputStrategy {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;


    /**
     * Constructs the TCP output strategy and initializes the network server port.
     * Spawns an isolated background thread task to listen for incoming client socket
     * connections, preventing the application's primary thread pool from blocking.
     *
     * @param port the network port number on which the TCP server will listen for connections
     */
    public TcpOutputStrategy(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("TCP Server started on port " + port);

            // Accept clients in a new thread to not block the main thread
            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    clientSocket = serverSocket.accept();
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Serializes a single simulation record into a compact comma-separated string and
     * broadcasts it across the active network socket connection.
     *
     * @param patientId the unique numerical identifier representing the simulated patient
     * @param timestamp the precise epoch time in milliseconds when the event occurred
     * @param label the descriptive category name of the health metric
     * @param data the raw calculated value or status message generated for the metric
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        if (out != null) {
            String message = String.format("%d,%d,%s,%s", patientId, timestamp, label, data);
            out.println(message);
        }
    }
}
