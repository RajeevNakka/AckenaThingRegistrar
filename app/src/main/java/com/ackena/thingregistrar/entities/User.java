package com.ackena.thingregistrar.entities;

/**
 * Created by Light on 11-10-2016.
 */

/**
 * A dummy item representing a piece of status.
 */
public class User {
    public final String id;
    public String status;
    public final String details;
    public Boolean isAvailable;

    public User(String id, String status, String details,Boolean isAvailable) {
        this.id = id;
        this.status = status;
        this.details = details;
        this.isAvailable = isAvailable;
    }

    public User(String id) {
        this.id = id;
        this.status = "";
        this.details = "";
        this.isAvailable = false;
    }

    @Override
    public String toString() {
        return status;
    }
}