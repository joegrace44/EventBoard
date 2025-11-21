/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pkg22448734_josephgrace_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author joegr
 */

 //Handles one connected client in its own thread.

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final EventStore store;

    public ClientHandler(Socket socket, EventStore store) {
        this.socket = socket;
        this.store = store;
    }

    @Override
    public void run() {
        System.out.println("Handler started for " + socket.getRemoteSocketAddress());

        try (
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(
                    socket.getOutputStream(), true)
        ) {
            String line;

            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                // STOP closes this client connection
                if (line.equalsIgnoreCase("STOP")) {
                    out.println("TERMINATE");
                    System.out.println("Client " + socket.getRemoteSocketAddress()
                            + " sent STOP. Closing connection.");
                    break;
                }

                try {
                    String reply = processCommand(line);
                    out.println(reply);
                } catch (InvalidCommandException e) {
                    out.println("InvalidCommandException: " + e.getMessage());
                    System.out.println("Invalid command from "
                            + socket.getRemoteSocketAddress() + ": " + e.getMessage());
                } catch (Exception e) {
                    out.println("Server error: " + e.getMessage());
                    System.out.println("Unexpected error for "
                            + socket.getRemoteSocketAddress() + ": " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.out.println("I/O error with client "
                    + socket.getRemoteSocketAddress() + ": " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error closing client socket: " + e.getMessage());
            }
            System.out.println("Connection closed for " + socket.getRemoteSocketAddress());
        }
    }

    private String processCommand(String msg) throws InvalidCommandException {
        // Split into 4 parts: action ; date ; time ; description
        String[] parts = msg.split(";");
        if (parts.length != 4) {
            throw new InvalidCommandException(
                    "Command must be: action; date; time; description");
        }

        String action = parts[0].trim().toLowerCase();
        String date   = parts[1].trim();
        String time   = parts[2].trim();
        String desc   = parts[3].trim();

        // basic check to see if any empty parts
        if (action.isEmpty() || date.isEmpty()) {
            throw new InvalidCommandException("Action and date cannot be empty");
        }

        switch (action) {
            case "add":
                // add; date; time; description
                if (time.isEmpty() || desc.isEmpty()) {
                    throw new InvalidCommandException("Time and description are required for add");
                }
                Event addEvent = new Event(date, time, desc);
                return store.addEvent(addEvent);

            case "remove":
                // remove; date; time; description
                if (time.isEmpty() || desc.isEmpty()) {
                    throw new InvalidCommandException("Time and description are required for remove");
                }
                Event removeEvent = new Event(date, time, desc);
                return store.removeEvent(removeEvent);

            case "list":
                // list; date; -; -
                if (!time.equals("-") || !desc.equals("-")) {
                    throw new InvalidCommandException("List command must be: list; date; -; -");
                }
                return store.listEventsByDate(date);

            default:
                throw new InvalidCommandException("Unknown action: " + action);
        }
    }
}

