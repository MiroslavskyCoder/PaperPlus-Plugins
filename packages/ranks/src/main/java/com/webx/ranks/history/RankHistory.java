package com.webx.ranks.history;

import java.util.UUID;
import java.util.Date;

public class RankHistory {
    private UUID player;
    private String rankId;
    private Date assignedAt;
    private Date removedAt;
    public String getRankId() { return rankId; }
    public void setRankId(String rankId) { this.rankId = rankId; }
    public Date getAssignedAt() { return assignedAt; }
    public void setAssignedAt(Date assignedAt) { this.assignedAt = assignedAt; }
    public Date getRemovedAt() { return removedAt; }
    public void setRemovedAt(Date removedAt) { this.removedAt = removedAt; }
}
