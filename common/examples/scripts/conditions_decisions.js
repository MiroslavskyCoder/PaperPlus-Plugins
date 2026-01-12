// Example 5: Conditional Logic and Decision Trees
function determineDamageType(weaponType, enchantments) {
    const weaponDamageMap = {
        SWORD: { base: 8, type: "slash" },
        AXE: { base: 10, type: "chop" },
        BOW: { base: 5, type: "pierce" },
        WAND: { base: 6, type: "magic" }
    };
    
    let damage = weaponDamageMap[weaponType] || { base: 5, type: "normal" };
    
    if (enchantments) {
        if (enchantments.SHARPNESS) damage.base += enchantments.SHARPNESS * 0.5;
        if (enchantments.FIRE_ASPECT) damage.element = "fire";
        if (enchantments.KNOCKBACK) damage.knockback = enchantments.KNOCKBACK;
    }
    
    return damage;
}

function getQuestNextStep(currentProgress, totalSteps) {
    const progressPercent = (currentProgress / totalSteps) * 100;
    
    if (progressPercent === 0) {
        return "START";
    } else if (progressPercent < 25) {
        return "EARLY";
    } else if (progressPercent < 50) {
        return "MIDWAY";
    } else if (progressPercent < 75) {
        return "LATE";
    } else if (progressPercent < 100) {
        return "ALMOST_DONE";
    } else {
        return "COMPLETE";
    }
}

function evaluatePlayerRank(totalQuests, totalKills, totalDeaths, totalPlaytime) {
    let points = 0;
    
    // Quest completion - max 50 points
    points += Math.min(totalQuests * 2, 50);
    
    // Combat - max 30 points
    const killDeathRatio = totalDeaths > 0 ? totalKills / totalDeaths : totalKills;
    points += Math.min(killDeathRatio * 3, 30);
    
    // Playtime - max 20 points
    const hoursPlayed = totalPlaytime / 3600;
    points += Math.min(hoursPlayed * 0.5, 20);
    
    // Determine rank
    if (points >= 90) return "LEGENDARY";
    if (points >= 75) return "MASTER";
    if (points >= 60) return "EXPERT";
    if (points >= 45) return "ADVANCED";
    if (points >= 30) return "INTERMEDIATE";
    return "BEGINNER";
}
