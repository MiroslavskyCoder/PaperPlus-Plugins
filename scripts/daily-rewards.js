/**
 * Daily Rewards - ежедневные награды
 * Использует Vault API через LXXVServer для работы с экономикой
 */

const DAILY_REWARD = 1000;
const STREAK_BONUS = 500;

// Хранилище данных игроков (в реальном проекте использовать базу данных)
const playerData = new Map();

// Обработчик входа игрока
LXXVServer.on('playerJoin', (player) => {
    const playerName = player.getName();
    const uuid = player.getUniqueId().toString();
    
    // Проверить доступность Vault
    const balance = LXXVServer.getBalance(playerName);
    if (balance === null) {
        console.log('§c[DailyRewards] Vault не подключен!');
        return;
    }
    
    // Получить данные игрока
    let data = playerData.get(uuid);
    if (!data) {
        data = { lastClaim: 0, streak: 0 };
        playerData.set(uuid, data);
    }
    
    const now = Date.now();
    const dayMs = 24 * 60 * 60 * 1000;
    
    // Проверить, может ли игрок получить награду
    if (now - data.lastClaim >= dayMs) {
        // Обновить серию
        if (now - data.lastClaim < dayMs * 2) {
            data.streak++;
        } else {
            data.streak = 1;
        }
        data.lastClaim = now;
        
        // Рассчитать награду
        const reward = DAILY_REWARD + (data.streak > 1 ? STREAK_BONUS : 0);
        
        // Выдать деньги
        if (LXXVServer.deposit(playerName, reward)) {
            LXXVServer.sendMessage(playerName, '§6========== §aЕЖЕДНЕВНАЯ НАГРАДА §6==========');
            LXXVServer.sendMessage(playerName, `§a+ §e${reward}$ §7(день ${data.streak})`);
            if (data.streak > 1) {
                LXXVServer.sendMessage(playerName, `§6Бонус за серию: §e+${STREAK_BONUS}$`);
            }
            LXXVServer.sendMessage(playerName, `§7Ваш баланс: §e${LXXVServer.getBalance(playerName)}$`);
            LXXVServer.sendMessage(playerName, '§6====================================');
            
            // Эффекты
            LXXVServer.playSound(playerName, 'entity.player.levelup', 1.0, 1.5);
            LXXVServer.spawnParticle('VILLAGER_HAPPY', player.getLocation(), 20, 0.5, 0.5, 0.5, 0.1);
        }
    } else {
        // Сообщить сколько осталось до следующей награды
        const remaining = dayMs - (now - data.lastClaim);
        const hours = Math.floor(remaining / (60 * 60 * 1000));
        LXXVServer.sendMessage(playerName, `§7Следующая награда через §e${hours}ч`);
    }
});

// Команда для проверки статуса
LXXVServer.registerCommand('dailycheck', (player, args) => {
    const playerName = player.getName();
    const uuid = player.getUniqueId().toString();
    const data = playerData.get(uuid);
    
    if (!data) {
        LXXVServer.sendMessage(playerName, '§cНет данных о наградах');
        return;
    }
    
    const now = Date.now();
    const dayMs = 24 * 60 * 60 * 1000;
    const remaining = dayMs - (now - data.lastClaim);
    
    if (remaining > 0) {
        const hours = Math.floor(remaining / (60 * 60 * 1000));
        const minutes = Math.floor((remaining % (60 * 60 * 1000)) / (60 * 1000));
        LXXVServer.sendMessage(playerName, `§7Следующая награда через §e${hours}ч ${minutes}мин`);
        LXXVServer.sendMessage(playerName, `§7Серия: §e${data.streak} дней`);
    } else {
        LXXVServer.sendMessage(playerName, '§aНаграда доступна! Перезайдите на сервер.');
    }
});

console.log('§a[DailyRewards] Система ежедневных наград загружена!');
