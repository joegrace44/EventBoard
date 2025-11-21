/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pkg22448734_josephgrace_server;

/**
 *
 * @author joegr
 */
public class Event {

    private final String date;
    private final String time;
    private final String description;

    public Event(String date, String time, String description) {
        this.date = date;
        this.time = time;
        this.description = description;
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

    public boolean matches(Event other) {
        return this.date.equals(other.date)
                && this.time.equalsIgnoreCase(other.time)
                && this.description.equalsIgnoreCase(other.description);
    }

    @Override
    public String toString() {
        return date + "; " + time + ", " + description;
    }
}
