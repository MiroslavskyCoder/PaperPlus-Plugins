// Example 7: Game Mechanics Calculations
function calculateInventorySpace(items, maxSlots = 36) {
    const itemTypes = {};
    let usedSlots = 0;
    
    items.forEach(item => {
        itemTypes[item.type] = (itemTypes[item.type] || 0) + item.quantity;
    });
    
    for (let type in itemTypes) {
        const quantity = itemTypes[type];
        const stackSize = 64; // default Minecraft stack size
        usedSlots += Math.ceil(quantity / stackSize);
    }
    
    return {
        used: usedSlots,
        max: maxSlots,
        available: maxSlots - usedSlots,
        isFull: usedSlots >= maxSlots,
        percentage: Math.round((usedSlots / maxSlots) * 100)
    };
}

function calculateCombinedStats(baseStats, equipment) {
    const combined = { ...baseStats };
    
    if (equipment.armor) {
        combined.defense = (combined.defense || 0) + equipment.armor.defense;
        combined.health = (combined.health || 0) + equipment.armor.health;
    }
    
    if (equipment.weapon) {
        combined.attack = (combined.attack || 0) + equipment.weapon.damage;
        combined.critChance = (combined.critChance || 0) + (equipment.weapon.crit || 0);
    }
    
    if (equipment.accessories) {
        equipment.accessories.forEach(acc => {
            for (let stat in acc.bonuses) {
                combined[stat] = (combined[stat] || 0) + acc.bonuses[stat];
            }
        });
    }
    
    return combined;
}

function calculateEnchantmentCost(currentLevel) {
    // Cost increases exponentially
    // Level 1: 1 XP, Level 2: 3 XP, Level 3: 6 XP, etc.
    return (currentLevel * (currentLevel + 1)) / 2;
}

function predictNextUpgrade(currentStats, upgradeBonus) {
    return {
        health: currentStats.health + upgradeBonus.health,
        attack: currentStats.attack + upgradeBonus.attack,
        defense: currentStats.defense + upgradeBonus.defense,
        speed: currentStats.speed + (upgradeBonus.speed || 0)
    };
}
