/**
 * Scoreboard Manager - управление скорбордом
 * Использует Scoreboard API через LXXVServer
 */

// Конфигурация
const UPDATE_INTERVAL = 40; // 2 секунды (40 тиков)

// Форматирование чисел
function formatNumber(num) {
    if (num >= 1000000) return (num / 1000000).toFixed(1) + 'M';
    if (num >= 1000) return (num / 1000).toFixed(1) + 'K';
    return num.toString();
}

// Создание скорборда для игрока
function createScoreboard(playerName) {
    const player = LXXVServer.getPlayer(playerName);
    if (!player) return;
    
    // Создать основной objective
    const objectiveName = `sb_${playerName}`;
    LXXVServer.createObjective(objectiveName, 'dummy', '§6§l🌟 LXXV SERVER §6§l🌟');
    
    // Установить как sidebar
    // LXXVServer.setObjectiveDisplay(objectiveName, 'SIDEBAR'); // Метод нужно добавить
    
    console.log(`§a[Scoreboard] Создан скорборд для ${playerName}`);
}

// Обновление скорборда
function updateScoreboard(playerName) {
    const player = LXXVServer.getPlayer(playerName);
    if (!player) return;
    
    const objectiveName = `sb_${playerName}`;
    
    // Получить данные игрока
    const balance = LXXVServer.getBalance(playerName) || 0;
    const health = player.getHealth();
    const maxHealth = player.getMaxHealth();
    const level = player.getLevel();
    const world = player.getWorld().getName();
    const online = LXXVServer.getOnlinePlayers().length;
    const maxPlayers = LXXVServer.getMaxPlayers();
    
    // Очистить старые значения
    // LXXVServer.clearScores(objectiveName);
    
    // Установить новые значения (от большего к меньшему для правильного порядка)
    let line = 15;
    
    LXXVServer.setScore(objectiveName, '§7§m                    ', line--);
    LXXVServer.setScore(objectiveName, `§fИгрок: §e${playerName}`, line--);
    LXXVServer.setScore(objectiveName, '§r', line--);
    
    // Здоровье
    const healthPercent = Math.floor((health / maxHealth) * 100);
    const healthBar = generateBar(healthPercent, '❤', '§c', '§7');
    LXXVServer.setScore(objectiveName, `§f❤ Здоровье: §c${Math.floor(health)}§7/§c${Math.floor(maxHealth)}`, line--);
    
    // Уровень
    LXXVServer.setScore(objectiveName, `§f⭐ Уровень: §b${level}`, line--);
    LXXVServer.setScore(objectiveName, '§r ', line--);
    
    // Баланс (если Vault доступен)
    if (balance !== null) {
        LXXVServer.setScore(objectiveName, `§f💰 Баланс: §e${formatNumber(balance)}$`, line--);
        LXXVServer.setScore(objectiveName, '§r  ', line--);
    }
    
    // Мир
    const worldDisplay = world === 'world' ? 'Обычный' : 
                        world === 'world_nether' ? 'Нижний мир' :
                        world === 'world_the_end' ? 'Край' : world;
    LXXVServer.setScore(objectiveName, `§f🌍 Мир: §a${worldDisplay}`, line--);
    LXXVServer.setScore(objectiveName, '§r   ', line--);
    
    // Игроки онлайн
    LXXVServer.setScore(objectiveName, `§f👥 Онлайн: §e${online}§7/§e${maxPlayers}`, line--);
    LXXVServer.setScore(objectiveName, '§r    ', line--);
    
    // Футер
    LXXVServer.setScore(objectiveName, '§7§m                    ', line--);
    LXXVServer.setScore(objectiveName, '§7play.lxxv.net', line--);
}

// Генерация прогресс-бара
function generateBar(percent, symbol, activeColor, inactiveColor) {
    const total = 10;
    const active = Math.floor((percent / 100) * total);
    return activeColor + symbol.repeat(active) + inactiveColor + symbol.repeat(total - active);
}

// Обработчик входа игрока
LXXVServer.on('playerJoin', (player) => {
    const playerName = player.getName();
    
    // Небольшая задержка перед созданием скорборда
    LXXVServer.runTaskLater(() => {
        createScoreboard(playerName);
        updateScoreboard(playerName);
    }, 20); // 1 секунда
});

// Обработчик выхода игрока
LXXVServer.on('playerQuit', (player) => {
    const playerName = player.getName();
    const objectiveName = `sb_${playerName}`;
    
    // Удалить objective
    LXXVServer.removeObjective(objectiveName);
});

// Периодическое обновление скорбордов
LXXVServer.runTaskTimer(() => {
    const players = LXXVServer.getOnlinePlayers();
    players.forEach(playerName => {
        updateScoreboard(playerName);
    });
}, UPDATE_INTERVAL, UPDATE_INTERVAL);

// Команда для переключения скорборда
LXXVServer.registerCommand('scoreboard', (player, args) => {
    const playerName = player.getName();
    const objectiveName = `sb_${playerName}`;
    
    if (args.length > 0 && args[0] === 'toggle') {
        // Переключить видимость (нужно добавить метод в LXXVServer)
        // const visible = LXXVServer.isObjectiveVisible(objectiveName, playerName);
        // if (visible) {
        //     LXXVServer.hideObjective(objectiveName, playerName);
        //     LXXVServer.sendMessage(playerName, '§7Скорборд скрыт');
        // } else {
        //     LXXVServer.showObjective(objectiveName, playerName);
        //     LXXVServer.sendMessage(playerName, '§aСкорборд показан');
        // }
        
        LXXVServer.sendMessage(playerName, '§eФункция в разработке');
        return;
    }
    
    // Обновить скорборд
    updateScoreboard(playerName);
    LXXVServer.sendMessage(playerName, '§aСкорборд обновлен!');
});

console.log('§a[Scoreboard] Менеджер скорбордов загружен!');
