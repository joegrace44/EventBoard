
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
/**
 *
 * @author joegr
 */
public class EventBoardClient {

    public static void main(String[] args) {

        String serverHost = "localhost";
        int serverPort = 1510;

        //socket and streams close automatically
        try (Socket socket = new Socket(serverHost, serverPort); BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); PrintWriter out = new PrintWriter(socket.getOutputStream(), true); Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to EventBoard Server at " + serverHost + ":" + serverPort);
            System.out.println("Use the following Commands:");
            System.out.println("-- add; date; time; description");
            System.out.println("-- remove; date; time; description");
            System.out.println("-- list; date; -; -");
            System.out.println("-- import; <public-URL-to-events.txt>");
            System.out.println("-- STOP");
            System.out.println("----------------------------------");

            //reads user input until STOP is sent
            while (true) {

                System.out.print("> ");
                String message = scanner.nextLine().trim();

                //STOP Command
                if (message.equalsIgnoreCase("STOP")) {
                    out.println("STOP");     // send stop request
                    String response = in.readLine();  // expect "TERMINATE"
                    System.out.println("< " + (response != null ? response : "Server disconnected"));
                    break;  // Exit loop and close client
                }

                //Import Command (HTTP GET)
                if (message.toLowerCase().startsWith("import;")) {
                    // Extract URL after "import;"
                    String urlString = message.substring(7).trim();
                    performImport(urlString, out, in);
                    continue;  // Skip normal command processing
                }

                // Normal ADD / REMOVE / LIST
                out.println(message);         // send to server
                String response = in.readLine();   // read server reply
                System.out.println("< " + (response != null ? response : "Server disconnected"));
            }

        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        }
    }

    //performImport takes public URL that points to a text file and will turn lines that are in
    //the date; time; description format and will send them to the server while counting valid and invalid lines
    private static void performImport(String urlString, PrintWriter out, BufferedReader in) {

        int imported = 0;
        int skipped = 0;

        try {
            URL url = new URL(urlString);   // Builds a URL object
            URLConnection conn = url.openConnection();  // Open HTTP connection

            // Reader for the remote file
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()))) {

                String line;
                int lineNumber = 1;

                // Read each line of the remote file
                while ((line = reader.readLine()) != null) {

                    line = line.trim();
                    if (line.isEmpty()) {  // Ignores the blank lines
                        lineNumber++;
                        continue;
                    }

                    // Expect exactly: date; time; description
                    String[] parts = line.split(";", 3);

                    if (parts.length != 3
                            || parts[0].trim().isEmpty()
                            || parts[1].trim().isEmpty()
                            || parts[2].trim().isEmpty()) {
                        
                        //if the lines are not in add; date; time; description format then they are skipped and added to skip counter
                        System.out.println("Skipped improperly formatted line " + lineNumber + ": \"" + line + "\"");
                        skipped++;
                        lineNumber++;
                        continue;
                    }

                    // Extract the fields
                    String date = parts[0].trim();
                    String time = parts[1].trim();
                    String desc = parts[2].trim();

                    // Build an ADD command to send to server
                    String addCmd = "add; " + date + "; " + time + "; " + desc;
                    out.println(addCmd);

                    // Read server reply
                    String response = in.readLine();
                    if (response == null) {
                        // Server disconnected in the middle of import
                        System.out.println("Server disconnected during import.");
                        break;
                    }

                    System.out.println("< " + response);

                    // Track import/skipped counts
                    if (response.startsWith("InvalidCommandException")) {
                        skipped++;
                    } else {
                        imported++;
                    }

                    lineNumber++;
                }
            }

        } catch (IOException e) {
            System.out.println("Failed to fetch file: " + e.getMessage());
            return;
        }

        // Summary after import completes
        System.out.println("Imported: " + imported + "; Skipped: " + skipped);
    }
}
