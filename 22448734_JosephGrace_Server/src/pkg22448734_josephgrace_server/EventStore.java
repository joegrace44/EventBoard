/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pkg22448734_josephgrace_server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author joegr
 */


/**
 * EventStore
 *
 * This class holds ALL events for ALL clients.
 * It is shared between every ClientHandler thread.
 *
 * The data structure:
 *    Map<date, List<Event>>
 *
 * All methods are synchronized to prevent race conditions
 * when multiple clients connect at once.
 */
public class EventStore {

    private final Map<String, List<Event>> eventsByDate = new HashMap<>();

    /**
     * Add an event to the store.
     * If a date doesn't exist, create a new list for it.
     */
    public synchronized String addEvent(Event event) {
        List<Event> eventsForDate = eventsByDate.get(event.getDate());

        if (eventsForDate == null) {
            eventsForDate = new ArrayList<>();
            eventsByDate.put(event.getDate(), eventsForDate);
        }

        eventsForDate.add(event);
        Collections.sort(eventsForDate); // Sort by time (using Comparable)

        return formatEventList(event.getDate(), eventsForDate);
    }

    /**
     * Remove an event matching all fields (date, time, description).
     */
    public synchronized String removeEvent(Event target) throws InvalidCommandException {
        List<Event> events = eventsByDate.get(target.getDate());

        if (events == null || events.isEmpty()) {
            throw new InvalidCommandException("event does not exist for given date/time/description");
        }

        Event toRemove = null;

        for (Event e : events) {
            if (e.matches(target)) {
                toRemove = e;
                break;
            }
        }

        if (toRemove == null) {
            throw new InvalidCommandException("event does not exist for given date/time/description");
        }

        events.remove(toRemove);

        if (events.isEmpty()) {
            eventsByDate.remove(target.getDate());
            return "No events for " + target.getDate();
        }

        Collections.sort(events);

        return formatEventList(target.getDate(), events);
    }

    /**
     * List all events for a date.
     */
    public synchronized String listEventsByDate(String date) {
        List<Event> events = eventsByDate.get(date);

        if (events == null || events.isEmpty()) {
            return "No events for " + date;
        }

        Collections.sort(events);

        return formatEventList(date, events);
    }

    /**
     * Formats the event list exactly as required:
     *
     * date; time, description; time, description; ...
     */
    private String formatEventList(String date, List<Event> events) {
        StringBuilder sb = new StringBuilder();
        sb.append(date);

        for (Event e : events) {
            sb.append("; ")
              .append(e.getTime())
              .append(", ")
              .append(e.getDescription());
        }

        return sb.toString();
    }
}
