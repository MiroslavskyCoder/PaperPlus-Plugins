package com.webx.clans.models;

import java.util.*;

public class Clan {
        public ClanBank getBank() {
            return bank;
        }

        public void setBank(ClanBank bank) {
            this.bank = bank;
        }

        public Map<UUID, ClanRole> getRoleMap() {
            return roleMap;
        }

        public void setRoleMap(Map<UUID, ClanRole> roleMap) {
            this.roleMap = roleMap;
        }

        public ClanLevel getClanLevel() {
            return clanLevel;
        }

        public void setClanLevel(ClanLevel clanLevel) {
            this.clanLevel = clanLevel;
        }

        public List<ClanHistoryEvent> getHistory() {
            return history;
        }

        public void setHistory(List<ClanHistoryEvent> history) {
            this.history = history;
        }
    private final String id;
    private String name;
    private String tag;
    private final UUID leader;
    private final Set<UUID> deputies;
    private final Map<UUID, String> members; // UUID to Rank
    private int level;
    private long experience;
    private double power; // Clan power based on total member coins
    private String description;
    private long createdAt;

    // --- New fields for bank, roles, level, history ---
    private ClanBank bank;
    private Map<UUID, ClanRole> roleMap; // uuid -> role
    private ClanLevel clanLevel;
    private List<ClanHistoryEvent> history;

    public Clan(String name, UUID leader) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.tag = name.substring(0, Math.min(4, name.length())).toUpperCase();
        this.leader = leader;
        this.deputies = new HashSet<>();
        this.members = new HashMap<>();
        this.level = 1;
        this.experience = 0;
        this.power = 0;
        this.description = "A clan";
        this.createdAt = System.currentTimeMillis();
        this.members.put(leader, "LEADER");
        this.bank = new ClanBank();
        this.roleMap = new HashMap<>();
        this.clanLevel = new ClanLevel();
        this.history = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public UUID getLeader() {
        return leader;
    }

    public Set<UUID> getDeputies() {
        return deputies;
    }

    public void addDeputy(UUID uuid) {
        deputies.add(uuid);
        members.put(uuid, "DEPUTY");
    }

    public void removeDeputy(UUID uuid) {
        deputies.remove(uuid);
        if (members.containsKey(uuid)) {
            members.put(uuid, "MEMBER");
        }
    }

    public boolean isDeputy(UUID uuid) {
        return deputies.contains(uuid);
    }

    public boolean isLeader(UUID uuid) {
        return leader.equals(uuid);
    }

    public boolean hasPermission(UUID uuid) {
        return isLeader(uuid) || isDeputy(uuid);
    }

    public void addMember(UUID uuid, String rank) {
        members.put(uuid, rank);
        if ("DEPUTY".equals(rank)) {
            deputies.add(uuid);
        }
    }

    public void removeMember(UUID uuid) {
        members.remove(uuid);
        deputies.remove(uuid);
    }

    public Collection<UUID> getMembers() {
        return members.keySet();
    }

    public String getMemberRank(UUID uuid) {
        return members.getOrDefault(uuid, null);
    }

    public int getMemberCount() {
        return members.size();
    }

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = Math.min(level, 5);
    }

    public long getExperience() {
        return experience;
    }

    public void addExperience(long amount) {
        this.experience += amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("name", name);
        data.put("tag", tag);
        data.put("leader", leader.toString());
        data.put("deputies", deputies.stream().map(UUID::toString).toList());
        data.put("members", members.entrySet().stream()
                .collect(HashMap::new, (m, e) -> m.put(e.getKey().toString(), e.getValue()), HashMap::putAll));
        data.put("level", level);
        data.put("experience", experience);
        data.put("power", power);
        data.put("description", description);
        data.put("createdAt", createdAt);
        return data;
    }

    @SuppressWarnings("unchecked")
    public static Clan fromMap(Map<String, Object> data) {
        String name = (String) data.get("name");
        UUID leader = UUID.fromString((String) data.get("leader"));
        Clan clan = new Clan(name, leader);
        
        if (data.containsKey("tag")) {
            clan.setTag((String) data.get("tag"));
        }
        if (data.containsKey("deputies")) {
            List<String> deputyList = (List<String>) data.get("deputies");
            deputyList.forEach(d -> clan.addDeputy(UUID.fromString(d)));
        }
        if (data.containsKey("members")) {
            Map<String, String> memberMap = (Map<String, String>) data.get("members");
            memberMap.forEach((uuid, rank) -> {
                if (!uuid.equals(leader.toString())) {
                    clan.addMember(UUID.fromString(uuid), rank);
                }
            });
        }
        if (data.containsKey("level")) {
            clan.setLevel(((Number) data.get("level")).intValue());
        }
        if (data.containsKey("experience")) {
            clan.addExperience(((Number) data.get("experience")).longValue());
        }
        if (data.containsKey("power")) {
            clan.setPower(((Number) data.get("power")).doubleValue());
        }
        if (data.containsKey("description")) {
            clan.setDescription((String) data.get("description"));
        }
        
        return clan;
    }
}
