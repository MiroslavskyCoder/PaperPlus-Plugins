// Example 4: Quest Reward System
function calculateQuestReward(config) {
    const {
        baseReward = 100,
        playerLevel = 1,
        questDifficulty = "EASY",
        bonusPercentage = 0,
        taxPercentage = 10
    } = config;
    
    // Difficulty multipliers
    const difficultyMap = {
        EASY: 1.0,
        MEDIUM: 1.5,
        HARD: 2.0,
        EXPERT: 3.0,
        LEGENDARY: 5.0
    };
    
    // Calculate components
    const difficulty = difficultyMap[questDifficulty] || 1.0;
    const levelBonus = 1 + (playerLevel / 100);
    const bonusMultiplier = 1 + (bonusPercentage / 100);
    
    // Calculate reward
    const beforeTax = baseReward * difficulty * levelBonus * bonusMultiplier;
    const tax = beforeTax * (taxPercentage / 100);
    const finalReward = beforeTax - tax;
    
    return {
        base: baseReward,
        beforeBonus: baseReward * difficulty * levelBonus,
        beforeTax: beforeTax,
        tax: Math.round(tax),
        final: Math.round(finalReward),
        breakdown: {
            difficultyMultiplier: difficulty,
            levelMultiplier: levelBonus,
            bonusMultiplier: bonusMultiplier,
            taxRate: taxPercentage + "%"
        }
    };
}

function calculateLevelUpXp(currentLevel) {
    // XP required = 100 * level^1.5
    return Math.floor(100 * Math.pow(currentLevel, 1.5));
}

function validateQuestCompletion(questProgress) {
    if (!questProgress.objectives || questProgress.objectives.length === 0) {
        return { valid: false, reason: "No objectives" };
    }
    
    const allComplete = questProgress.objectives.every(obj => 
        obj.progress >= obj.required
    );
    
    if (!allComplete) {
        return { valid: false, reason: "Not all objectives complete" };
    }
    
    if (questProgress.failed) {
        return { valid: false, reason: "Quest was marked as failed" };
    }
    
    return { valid: true, reason: "Quest is ready to complete" };
}
