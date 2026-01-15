package com.webx.ranks.models;

import java.util.Date;

public class RankHistoryEntry {
        // For GUI compatibility
        public long getTimestamp() {
            return assignedAt != null ? assignedAt.getTime() : 0L;
        }
        public String getAction() {
            return "Assigned"; // Or customize as needed
        }
        public String getActor() {
            return "Console"; // Or customize as needed
        }
        public String getTarget() {
            return rankId;
        }
    private String rankId;
    private Date assignedAt;
    private Date removedAt;

    public RankHistoryEntry() {}
    public RankHistoryEntry(String rankId, Date assignedAt, Date removedAt) {
        this.rankId = rankId;
        this.assignedAt = assignedAt;
        this.removedAt = removedAt;
    }
    public String getRankId() { return rankId; }
    public void setRankId(String rankId) { this.rankId = rankId; }
    public Date getAssignedAt() { return assignedAt; }
    public void setAssignedAt(Date assignedAt) { this.assignedAt = assignedAt; }
    public Date getRemovedAt() { return removedAt; }
    public void setRemovedAt(Date removedAt) { this.removedAt = removedAt; }
}
