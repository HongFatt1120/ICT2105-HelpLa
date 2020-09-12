
package edu.singaporetech.helpla.models;

import java.util.Map;

public class Event {

    private String event_name;
    private String date_time;
    private String event_id;
    private String location;
    private String start_time;
    private String end_time;

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    private String roomName;
    private String organizer_id;
    private Map<String, Participants> participants;
    private String status;



    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public java.lang.String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public java.lang.String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public java.lang.String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(java.lang.String event_name) {
        this.event_name = event_name;
    }

    public java.lang.String getLocation() {
        return location;
    }

    public void setLocation(java.lang.String location) {
        this.location = location;
    }

    public java.lang.String getOrganizer_id() {
        return organizer_id;
    }

    public void setOrganizer_id(java.lang.String organizer_id) {
        this.organizer_id = organizer_id;
    }

    public Map<String, Participants> getParticipants() {
        return participants;
    }

    public void setParticipants(Map<String, Participants> participants) {
        this.participants = participants;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class Participants{
        private java.lang.String id;
        public java.lang.String getId() {
            return id;
        }


    }
}