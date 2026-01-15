package com.webx.ranks.models;

import java.util.Date;

public class AuditLogEntry {
        // For GUI compatibility
    private String action;
    private String actor;
    private String target;
    private Date timestamp;
    private String details;

    public AuditLogEntry() {}
    public AuditLogEntry(String action, String actor, String target, Date timestamp, String details) {
        this.action = action;
        this.actor = actor;
        this.target = target;
        this.timestamp = timestamp;
        this.details = details;
    }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getActor() { return actor; }
    public void setActor(String actor) { this.actor = actor; }
    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
