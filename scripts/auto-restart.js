/**
 * Auto Restart - автоматический перезапуск сервера
 * Использует планировщик LXXVServer
 */

// Конфигурация
const RESTART_TIMES = ['04:00', '16:00']; // Время перезапуска
const WARNING_TIMES = [600, 300, 180, 60, 30, 10, 5]; // Предупреждения (секунды до рестарта)

let restartScheduled = false;
let restartTime = null;

// Функция для планирования следующего рестарта
function scheduleNextRestart() {
    const now = new Date();
    const currentTime = `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`;
    
    let nextRestart = null;
    
    for (const time of RESTART_TIMES) {
        const [hours, minutes] = time.split(':').map(Number);
        const restartDate = new Date();
        restartDate.setHours(hours, minutes, 0, 0);
        
        // Если время уже прошло сегодня, планируем на завтра
        if (restartDate <= now) {
            restartDate.setDate(restartDate.getDate() + 1);
        }
        
        if (!nextRestart || restartDate < nextRestart) {
            nextRestart = restartDate;
        }
    }
    
    if (nextRestart) {
        restartTime = nextRestart;
        const delay = nextRestart.getTime() - now.getTime();
        
        console.log(`§e[AutoRestart] Следующий перезапуск: ${nextRestart.toLocaleString('ru-RU')}`);
        
        // Запустить таймер для предупреждений
        startWarningTimer();
    }
}

// Запуск таймера предупреждений
function startWarningTimer() {
    // Проверять каждые 10 секунд
    LXXVServer.runTaskTimer(() => {
        if (!restartTime) return;
        
        const now = new Date();
        const secondsUntilRestart = Math.floor((restartTime.getTime() - now.getTime()) / 1000);
        
        // Проверить, нужно ли отправить предупреждение
        if (WARNING_TIMES.includes(secondsUntilRestart)) {
            sendRestartWarning(secondsUntilRestart);
        }
        
        // Если время пришло
        if (secondsUntilRestart <= 0) {
            performRestart();
        }
    }, 0, 200); // Каждые 10 секунд (200 тиков)
}

// Отправка предупреждения
function sendRestartWarning(seconds) {
    const minutes = Math.floor(seconds / 60);
    let timeStr;
    
    if (minutes > 0) {
        timeStr = `§e${minutes} минут${minutes === 1 ? 'у' : minutes < 5 ? 'ы' : ''}`;
    } else {
        timeStr = `§c${seconds} секунд${seconds === 1 ? 'у' : seconds < 5 ? 'ы' : ''}`;
    }
    
    // Объявление
    LXXVServer.broadcast('§6========================================');
    LXXVServer.broadcast(`§c⚠ §lСЕРВЕР ПЕРЕЗАГРУЗИТСЯ ЧЕРЕЗ ${timeStr}§c ⚠`);
    LXXVServer.broadcast('§6========================================');
    
    // Звук для всех игроков
    const players = LXXVServer.getOnlinePlayers();
    players.forEach(playerName => {
        LXXVServer.playSound(playerName, 'block.note_block.bell', 1.0, 1.0);
        
        // Титр для последних 30 секунд
        if (seconds <= 30) {
            LXXVServer.sendTitle(playerName, '§cПерезагрузка!', timeStr, 5, 30, 5);
        }
    });
    
    console.log(`§e[AutoRestart] Предупреждение: ${seconds}с до перезапуска`);
}

// Выполнение перезапуска
function performRestart() {
    console.log('§c[AutoRestart] Выполняется перезапуск сервера...');
    
    // Финальное сообщение
    LXXVServer.broadcast('§c§l========================================');
    LXXVServer.broadcast('§c§l    СЕРВЕР ПЕРЕЗАГРУЖАЕТСЯ!');
    LXXVServer.broadcast('§c§l========================================');
    
    // Кикнуть всех игроков
    const players = LXXVServer.getOnlinePlayers();
    players.forEach(playerName => {
        LXXVServer.kickPlayer(playerName, '§c§lСервер перезагружается\n\n§7Вернись через 1 минуту!');
    });
    
    // Сохранить все миры
    LXXVServer.saveAllWorlds();
    
    // Через 5 секунд остановить сервер
    LXXVServer.runTaskLater(() => {
        LXXVServer.executeCommand('stop');
    }, 100);
    
    // Планировать следующий рестарт
    restartTime = null;
    scheduleNextRestart();
}

// Команда для проверки времени до рестарта
LXXVServer.registerCommand('restart', (player, args) => {
    const playerName = player.getName();
    
    if (args.length > 0 && args[0] === 'now') {
        // Принудительный рестарт (только для админов)
        if (!LXXVServer.hasPermission(playerName, 'admin.restart')) {
            LXXVServer.sendMessage(playerName, '§cНедостаточно прав!');
            return;
        }
        
        // Запустить рестарт через 30 секунд
        restartTime = new Date(Date.now() + 30000);
        LXXVServer.sendMessage(playerName, '§aПринудительный рестарт запланирован через 30 секунд!');
        return;
    }
    
    // Показать информацию о следующем рестарте
    if (restartTime) {
        const now = new Date();
        const secondsUntilRestart = Math.floor((restartTime.getTime() - now.getTime()) / 1000);
        const minutes = Math.floor(secondsUntilRestart / 60);
        const hours = Math.floor(minutes / 60);
        
        if (hours > 0) {
            LXXVServer.sendMessage(playerName, `§7Следующий рестарт через §e${hours}ч ${minutes % 60}мин`);
        } else if (minutes > 0) {
            LXXVServer.sendMessage(playerName, `§7Следующий рестарт через §e${minutes}мин ${secondsUntilRestart % 60}с`);
        } else {
            LXXVServer.sendMessage(playerName, `§cСледующий рестарт через §e${secondsUntilRestart}с`);
        }
    } else {
        LXXVServer.sendMessage(playerName, '§cРестарт не запланирован');
    }
});

// Запуск
scheduleNextRestart();

console.log('§a[AutoRestart] Система автоматического перезапуска загружена!');
console.log(`§e[AutoRestart] Время перезапусков: ${RESTART_TIMES.join(', ')}`);
