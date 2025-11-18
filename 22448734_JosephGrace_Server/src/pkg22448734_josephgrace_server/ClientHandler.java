/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pkg22448734_josephgrace_server;

/**
 *
 * @author joegr
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    // The socket for communicating with this client only.
    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    //run() acts as the clients session
    @Override
    public void run() {

        System.out.println("ClientHandler thread started for: " + socket.getRemoteSocketAddress());


        try (
            BufferedReader in = new BufferedReader(  //reads messages set by the client
                    new InputStreamReader(socket.getInputStream()));

            PrintWriter out = new PrintWriter(  //sends messages back to client
                    socket.getOutputStream(), true) //autoflushes
        ) {

            String messageFromClient;

            //readLine() sends a string if reading a message and null if connection is closed
             
            while ((messageFromClient = in.readLine()) != null) {

                messageFromClient = messageFromClient.trim();

                // Ignores empty lines
                if (messageFromClient.isEmpty()) {
                    continue;
                }

                //If the client sends STOP then the server must reply with TERMINATE and end the connection.
                if (messageFromClient.equalsIgnoreCase("STOP")) {

                    out.println("TERMINATE");

                    System.out.println("Client " + socket.getRemoteSocketAddress()
                            + " sent STOP. Closing connection.");

                    break; // Exits the loop which ends the thread
                }

                //Echoes message
                String response = "Received: " + messageFromClient;

                // Send reply to client
                out.println(response);

                // Log activity
                System.out.println("Echoed to client: " + response);
            }

        } catch (IOException e) {
            System.out.println("Error communicating with client "
                    + socket.getRemoteSocketAddress() + ": " + e.getMessage());
        } finally {

            //closes socket to save resources
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Could not close client socket: " + e.getMessage());
            }

            System.out.println("Connection closed for " + socket.getRemoteSocketAddress());
        }
    }
}
