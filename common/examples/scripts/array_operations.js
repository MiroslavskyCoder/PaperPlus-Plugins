// Example 2: Object and Array Operations
function processPlayerData(player) {
    return {
        name: player.name.toUpperCase(),
        level: player.level + 1,
        exp: player.exp * 1.1,
        isHighLevel: player.level > 50,
        status: player.health > 0 ? "ALIVE" : "DEAD"
    };
}

function filterHighLevelPlayers(players) {
    return players.filter(p => p.level >= 30);
}

function mapPlayersToNames(players) {
    return players.map(p => ({
        name: p.name,
        level: p.level
    }));
}

function calculateTotalExp(players) {
    return players.reduce((sum, p) => sum + p.exp, 0);
}

function groupPlayersByLevel(players) {
    const groups = {};
    players.forEach(p => {
        const levelGroup = Math.floor(p.level / 10) * 10;
        if (!groups[levelGroup]) {
            groups[levelGroup] = [];
        }
        groups[levelGroup].push(p);
    });
    return groups;
}
