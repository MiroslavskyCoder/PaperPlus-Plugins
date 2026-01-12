// Example 1: Math Operations
function basicMath() {
    return {
        addition: 5 + 3,
        subtraction: 10 - 4,
        multiplication: 6 * 7,
        division: 20 / 4,
        power: Math.pow(2, 8),
        sqrt: Math.sqrt(16),
        abs: Math.abs(-42),
        random: Math.random(),
        max: Math.max(1, 5, 3, 9, 2),
        min: Math.min(1, 5, 3, 9, 2)
    };
}

function calculatePercentage(value, total) {
    if (total === 0) return 0;
    return (value / total) * 100;
}

function calculateDamage(baseDamage, weaponMultiplier, resistanceReduction) {
    const withWeapon = baseDamage * weaponMultiplier;
    const afterResistance = withWeapon * (1 - resistanceReduction);
    return Math.ceil(afterResistance);
}
