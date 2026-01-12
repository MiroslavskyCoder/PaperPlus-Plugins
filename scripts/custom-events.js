/**
 * Custom Events - кастомные ивенты на сервере
 * Использует планировщик и систему событий LXXVServer
 */

// Конфигурация ивентов
const EVENTS = {
    METEOR_SHOWER: {
        name: 'Метеоритный дождь',
        duration: 300, // 5 минут
        cooldown: 1800, // 30 минут
        announce: '§6⚡ §cМетеоритный дождь начинается! §6⚡'
    },
    BLESSING: {
        name: 'Благословение',
        duration: 600, // 10 минут
        cooldown: 3600, // 1 час
        announce: '§6✨ §aСервер получил благословение! §6✨'
    },
    TREASURE_HUNT: {
        name: 'Охота за сокровищами',
        duration: 900, // 15 минут
        cooldown: 2400, // 40 минут
        announce: '§6🗝 §eНачинается охота за сокровищами! §6🗝'
    }
};

let activeEvents = new Set();
let eventCooldowns = new Map();

// Запуск ивента "Метеоритный дождь"
function startMeteorShower() {
    if (activeEvents.has('METEOR_SHOWER')) return;
    
    const event = EVENTS.METEOR_SHOWER;
    activeEvents.add('METEOR_SHOWER');
    
    // Объявление
    LXXVServer.broadcast(event.announce);
    LXXVServer.broadcast('§7Падающие метеориты дают дополнительные ресурсы!');
    
    // Эффект для всех онлайн игроков
    const players = LXXVServer.getOnlinePlayers();
    players.forEach(playerName => {
        LXXVServer.playSound(playerName, 'entity.lightning_bolt.thunder', 0.5, 1.0);
        LXXVServer.sendTitle(playerName, '§c⚡ Метеоритный дождь ⚡', '§7Собирай ресурсы!', 10, 40, 10);
    });
    
    // Создать Boss Bar
    LXXVServer.createBossBar('meteor_event', '§cМетеоритный дождь', 'RED', 'PROGRESS');
    players.forEach(playerName => {
        LXXVServer.showBossBar('meteor_event', playerName);
    });
    
    // Обновление прогресс бара
    let timeLeft = event.duration;
    const updateTask = LXXVServer.runTaskTimer(() => {
        timeLeft -= 20;
        const progress = timeLeft / event.duration;
        
        if (timeLeft <= 0) {
            endMeteorShower();
            return;
        }
        
        // Обновить Boss Bar (этот метод нужно добавить в LXXVServer)
        // LXXVServer.updateBossBar('meteor_event', progress);
        
        // Случайные метеориты
        if (Math.random() < 0.3) {
            spawnMeteor();
        }
    }, 0, 20); // Каждую секунду
}

function endMeteorShower() {
    activeEvents.delete('METEOR_SHOWER');
    eventCooldowns.set('METEOR_SHOWER', Date.now());
    
    LXXVServer.broadcast('§7Метеоритный дождь закончился...');
    
    const players = LXXVServer.getOnlinePlayers();
    players.forEach(playerName => {
        LXXVServer.removeBossBar('meteor_event', playerName);
    });
}

function spawnMeteor() {
    const players = LXXVServer.getOnlinePlayers();
    if (players.length === 0) return;
    
    // Выбрать случайного игрока
    const playerName = players[Math.floor(Math.random() * players.length)];
    const player = LXXVServer.getPlayer(playerName);
    if (!player) return;
    
    const loc = player.getLocation();
    const world = loc.getWorld();
    
    // Спавн метеорита рядом с игроком
    const meteorX = loc.getX() + (Math.random() - 0.5) * 50;
    const meteorY = loc.getY() + 50;
    const meteorZ = loc.getZ() + (Math.random() - 0.5) * 50;
    
    // Эффекты
    LXXVServer.spawnParticle('FLAME', {
        world: world.getName(),
        x: meteorX,
        y: meteorY,
        z: meteorZ
    }, 50, 2, 2, 2, 0.1);
    
    LXXVServer.playSound(playerName, 'entity.generic.explode', 0.7, 0.8);
    
    // В реальном проекте здесь можно создать блоки руды или выдать предметы
}

// Запуск ивента "Благословение"
function startBlessing() {
    if (activeEvents.has('BLESSING')) return;
    
    const event = EVENTS.BLESSING;
    activeEvents.add('BLESSING');
    
    LXXVServer.broadcast(event.announce);
    LXXVServer.broadcast('§7Удвоенный опыт и удача на §e10 минут§7!');
    
    const players = LXXVServer.getOnlinePlayers();
    players.forEach(playerName => {
        LXXVServer.playSound(playerName, 'entity.player.levelup', 1.0, 1.5);
        
        // Применить эффекты (нужно добавить в LXXVServer)
        // LXXVServer.addPotionEffect(playerName, 'LUCK', event.duration * 20, 1);
        // LXXVServer.addPotionEffect(playerName, 'SPEED', event.duration * 20, 0);
    });
    
    // Завершение через duration секунд
    LXXVServer.runTaskLater(() => {
        endBlessing();
    }, event.duration * 20);
}

function endBlessing() {
    activeEvents.delete('BLESSING');
    eventCooldowns.set('BLESSING', Date.now());
    LXXVServer.broadcast('§7Благословение закончилось.');
}

// Автоматический запуск случайных ивентов
function scheduleRandomEvents() {
    LXXVServer.runTaskTimer(() => {
        // Проверить кулдауны
        const now = Date.now();
        
        for (const [eventKey, config] of Object.entries(EVENTS)) {
            const lastTime = eventCooldowns.get(eventKey) || 0;
            
            if (now - lastTime >= config.cooldown * 1000 && !activeEvents.has(eventKey)) {
                // 20% шанс запуска ивента
                if (Math.random() < 0.2) {
                    switch(eventKey) {
                        case 'METEOR_SHOWER':
                            startMeteorShower();
                            break;
                        case 'BLESSING':
                            startBlessing();
                            break;
                    }
                }
            }
        }
    }, 6000, 6000); // Проверка каждые 5 минут
}

// Команды для администраторов
LXXVServer.registerCommand('startevent', (player, args) => {
    if (!LXXVServer.hasPermission(player.getName(), 'admin.events')) {
        LXXVServer.sendMessage(player.getName(), '§cНедостаточно прав!');
        return;
    }
    
    if (args.length === 0) {
        LXXVServer.sendMessage(player.getName(), '§eДоступные ивенты: meteor, blessing, treasure');
        return;
    }
    
    switch(args[0].toLowerCase()) {
        case 'meteor':
            startMeteorShower();
            LXXVServer.sendMessage(player.getName(), '§aИвент запущен!');
            break;
        case 'blessing':
            startBlessing();
            LXXVServer.sendMessage(player.getName(), '§aИвент запущен!');
            break;
        default:
            LXXVServer.sendMessage(player.getName(), '§cНеизвестный ивент!');
    }
});

// Запуск планировщика
scheduleRandomEvents();

console.log('§a[CustomEvents] Система кастомных ивентов загружена!');
