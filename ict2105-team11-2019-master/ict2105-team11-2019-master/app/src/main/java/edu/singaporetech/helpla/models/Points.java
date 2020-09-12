package edu.singaporetech.helpla.models;

import java.io.Serializable;
import java.util.Map;

public class Points implements Serializable {
    private String id;

    public Map<String, claim> claim;
    public Map<String, owe> owe;

    public Map<String, Points.owe> getOwe() {
        return owe;
    }

    public void setOwe(Map<String, Points.owe> owe) {
        this.owe = owe;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, claim> getClaim() {
        return claim;
    }

    public void setClaim(Map<String, claim> claim) {
        this.claim = claim;
    }

    public static class claim implements Serializable {

        private int points;
        private String id;

        public claim(){

        }
        public String getid() {
            return id;
        }

        public void setid(String id) {
            this.id = id;
        }

        public int getPoints() {
            return points;
        }

        public void setPoints(int points) {
            this.points = points;
        }
    }
    public static class owe implements Serializable{

        int points;
        String id;

        public String getid() {
            return id;
        }

        public void setid(String uid) {
            this.id = uid;
        }

        public int getPoints() {
            return points;
        }

        public void setPoints(int points) {
            this.points = points;
        }



    }
}