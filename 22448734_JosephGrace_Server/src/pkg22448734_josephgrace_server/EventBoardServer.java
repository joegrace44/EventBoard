/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pkg22448734_josephgrace_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author joegr
 */
//Main server entry point for the EventBoard application.
public class EventBoardServer {

    private static final int PORT = 1510;

    public static void main(String[] args) {

        // Shared in-memory storage across all clients
        EventStore store = new EventStore();

        System.out.println("EventBoardServer starting on port " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running. Waiting for clients...");

            // Infinite loop to accept multiple clients
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getRemoteSocketAddress());

                // Create a handler for this client and start a new thread
                ClientHandler handler = new ClientHandler(clientSocket, store);
                new Thread(handler).start();
            }

        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }

        System.out.println("EventBoardServer stopped.");
    }
}
