/**
 * Welcome Script - приветствие новых игроков
 * Использует LXXVServer API для работы с игроками
 */

// Приветственное сообщение для новых игроков
LXXVServer.on('playerJoin', (player) => {
    const playerName = player.getName();
    
    // Отправить приветствие игроку
    LXXVServer.sendMessage(playerName, '§6=============================');
    LXXVServer.sendMessage(playerName, '§a  Добро пожаловать на сервер!');
    LXXVServer.sendMessage(playerName, `§e  Привет, §b${playerName}§e!`);
    LXXVServer.sendMessage(playerName, '§6=============================');
    
    // Показать титр
    LXXVServer.sendTitle(playerName, '§6Добро пожаловать!', `§e${playerName}`, 10, 70, 20);
    
    // Воспроизвести звук
    LXXVServer.playSound(playerName, 'entity.player.levelup', 1.0, 1.0);
    
    // Через 3 секунды показать подсказку
    LXXVServer.runTaskLater(() => {
        LXXVServer.sendActionBar(playerName, '§aИспользуй §e/help §aдля справки');
    }, 60); // 60 тиков = 3 секунды
    
    // Объявить всем на сервере
    LXXVServer.broadcast(`§7[§a+§7] §e${playerName} §7присоединился к игре!`);
});

console.log('§a[Welcome] Скрипт приветствия загружен!');
