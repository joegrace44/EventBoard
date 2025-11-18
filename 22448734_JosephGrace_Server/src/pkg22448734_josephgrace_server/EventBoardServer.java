/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pkg22448734_josephgrace_server;

/**
 *
 * @author joegr
 */
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class EventBoardServer {

    // Sets port to 5000
    private static final int PORT = 5000;

    public static void main(String[] args) {

        System.out.println("EventBoardServer starting on port " + PORT + "...");

        //Server socket listens for connections
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            System.out.println("Server is now running and waiting for clients...");

            //Loops so server will always be running and accepting new clients
            while (true) {

                //Code blocks until a client connects and a new socket gets made
                Socket clientSocket = serverSocket.accept();

                System.out.println("Client connected from: " + clientSocket.getRemoteSocketAddress());

                //Connects a new client handler to the socket and then creates a new thread so the server can continue looking for new clients
                ClientHandler handler = new ClientHandler(clientSocket);
                Thread thread = new Thread(handler);   
                thread.start();                        
            }

        } catch (IOException e) {
            // Exception for networking error
            System.err.println("Server failed: " + e.getMessage());
            e.printStackTrace();
        }

        // Prints when server is shut down manaually
        System.out.println("EventBoardServer has stopped.");
    }
}
