// Example 3: String Manipulation
function processQuestText(questName, difficulty, level) {
    const difficultyIcons = {
        EASY: "✓",
        MEDIUM: "✓✓",
        HARD: "✓✓✓",
        EXPERT: "★",
        LEGENDARY: "★★"
    };
    
    const icon = difficultyIcons[difficulty] || "?";
    const formattedName = questName.charAt(0).toUpperCase() + questName.slice(1).toLowerCase();
    
    return `[Lvl ${level}] ${icon} ${formattedName}`;
}

function formatCurrency(amount) {
    return amount.toLocaleString('en-US', {
        style: 'currency',
        currency: 'USD'
    });
}

function sanitizePlayerName(name) {
    return name.replace(/[^a-zA-Z0-9_]/g, '').substring(0, 16);
}

function createMessageTemplate(template, variables) {
    let message = template;
    for (let key in variables) {
        message = message.replace(`{${key}}`, variables[key]);
    }
    return message;
}
