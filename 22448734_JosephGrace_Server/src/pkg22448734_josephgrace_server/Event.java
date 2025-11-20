/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pkg22448734_josephgrace_server;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 *
 * @author joegr
 */
/**
 * Represents a single Event in the system.
 * Stores:
 *   - date (String)
 *   - time (String, e.g. "6 pm" or "7.30 pm")
 *   - description (String)
 *
 * For sorting, we also store the time as "minutes since midnight".
 */
public class Event implements Comparable<Event> {

    private final String date;
    private final String time;
    private final String description;

    // Used only for sorting events by time
    private final int sortMinutes;

    // Formatter for times like "6:00 pm", "7:30 pm"
    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("h:mm a");

    public Event(String date, String time, String description) throws InvalidCommandException {
        this.date = date;
        this.time = time;
        this.description = description;
        this.sortMinutes = parseTimeToMinutes(time);
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Sort events by time (earlier events come first).
     */
    @Override
    public int compareTo(Event other) {
        return Integer.compare(this.sortMinutes, other.sortMinutes);
    }

    /**
     * Used when removing an event: events match if all fields match.
     */
    public boolean matches(Event other) {
        return this.date.equals(other.date)
                && this.time.equalsIgnoreCase(other.time)
                && this.description.equalsIgnoreCase(other.description);
    }

    /**
     * Parse a time string into "minutes since midnight".
     *
     * Accepted examples:
     *   "6 pm"
     *   "7.30 pm"
     *   "7:30 pm"
     *
     * Steps:
     *   1. Trim spaces, replace '.' with ':' → "7.30 pm" -> "7:30 pm"
     *   2. If there is no ":", assume ":00" minutes → "6 pm" -> "6:00 pm"
     *   3. Use DateTimeFormatter "h:mm a" to parse.
     */
    private int parseTimeToMinutes(String timeStr) throws InvalidCommandException {
        if (timeStr == null) {
            throw new InvalidCommandException("time cannot be null");
        }

        String normalised = timeStr.trim();

        // Replace dot with colon, so "7.30 pm" -> "7:30 pm"
        normalised = normalised.replace(".", ":");

        // If there is no ":" at all (e.g. "6 pm"), add ":00"
        if (!normalised.contains(":")) {
            int spaceIndex = normalised.lastIndexOf(' ');
            if (spaceIndex == -1) {
                throw new InvalidCommandException("time must be like '6 pm' or '7.30 pm'");
            }

            String hourPart = normalised.substring(0, spaceIndex).trim();   // "6"
            String ampmPart = normalised.substring(spaceIndex + 1).trim(); // "pm"

            if (hourPart.isEmpty() || ampmPart.isEmpty()) {
                throw new InvalidCommandException("time must be like '6 pm' or '7.30 pm'");
            }

            normalised = hourPart + ":00 " + ampmPart; // "6:00 pm"
        }

        LocalTime time;
        try {
            // This expects something like "6:00 pm" or "7:30 pm"
            time = LocalTime.parse(normalised, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new InvalidCommandException("time must be like '6 pm' or '7.30 pm'");
        }

        // Convert to minutes past midnight
        return time.getHour() * 60 + time.getMinute();
    }
}