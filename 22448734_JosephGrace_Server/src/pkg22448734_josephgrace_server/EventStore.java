/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pkg22448734_josephgrace_server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author joegr
 */
// Shared in-memory storage for all events.
public class EventStore {

    // CopyOnWriteArrayList allows for safe storage of multiple client threads.
    // Allows thread-safe reads without extra locking while avoiding concurrency issues
    private final List<Event> events = new CopyOnWriteArrayList<>();

    //Adds an event to the store and return the updated list of events for that event's date.
    public synchronized String addEvent(Event event) {
        if (event == null) {
            return "Invalid event";
        }

        events.add(event);

        // Return updated list for this date
        return listEventsByDate(event.getDate());
    }

    //Remove an event and returns the updated list of events for that date,
    public synchronized String removeEvent(Event target) throws InvalidCommandException {
        if (target == null) {
            throw new InvalidCommandException("event cannot be null");
        }

        String date = target.getDate();
        String time = target.getTime();
        String description = target.getDescription();

        if (isBlank(date) || isBlank(time) || isBlank(description)) {
            throw new InvalidCommandException("date/time/description must not be blank");
        }

        boolean removed = events.removeIf(e
                -> e.getDate().equals(date.trim())
                && e.getTime().equals(time.trim())
                && e.getDescription().equals(description.trim())
        );

        if (!removed) {
            throw new InvalidCommandException("event does not exist for given date/time/description");
        }

        // Return updated list for this date
        return listEventsByDate(date);
    }

    //Lists events on a given date
    public synchronized String listEventsByDate(String date) {
        if (isBlank(date)) {
            return "No events for " + date;
        }

        String trimmedDate = date.trim();

        // Filter events for this date
        List<Event> filtered = new ArrayList<>();
        for (Event e : events) {
            if (e.getDate().equals(trimmedDate)) {
                filtered.add(e);
            }
        }

        if (filtered.isEmpty()) {
            return "No events for " + trimmedDate;
        }

        // String builer that builds in this format: date; time, desc; time, desc; and so on
        StringBuilder sb = new StringBuilder();
        sb.append(trimmedDate);
        for (Event e : filtered) {
            sb.append("; ")
                    .append(e.getTime())
                    .append(", ")
                    .append(e.getDescription());
        }

        return sb.toString();
    }

    // To help clean up validations
    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
