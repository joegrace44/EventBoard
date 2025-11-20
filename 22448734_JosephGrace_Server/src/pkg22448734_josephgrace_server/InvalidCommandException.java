/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pkg22448734_josephgrace_server;

/**
 *
 * @author joegr
 */


/**
 * A custom exception used when the client sends an invalid command
 * or when event data fails validation.
 *
 * Using a custom exception makes error-handling more readable.
 */
public class InvalidCommandException extends Exception {

    public InvalidCommandException(String message) {
        super(message);
    }
}
