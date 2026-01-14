# Create2 API Documentation

## Для разработчиков

Этот документ описывает API Create2 для создания кастомных механических компонентов.

## 📦 Добавление Create2 как зависимость

### Maven
```xml
<dependency>
    <groupId>com.webx</groupId>
    <artifactId>create2</artifactId>
    <version>1.0.0</version>
    <scope>provided</scope>
</dependency>
```

### Gradle (Kotlin DSL)
```kotlin
dependencies {
    compileOnly("com.webx:create2:1.0.0")
}
```

## 🔧 Базовое использование

### Получение доступа к API

```java
import com.webx.create2.Create2Plugin;
import com.webx.create2.kinematic.*;

public class MyPlugin extends JavaPlugin {
    
    private Create2Plugin create2;
    
    @Override
    public void onEnable() {
        Plugin plugin = getServer().getPluginManager().getPlugin("Create2");
        if (plugin instanceof Create2Plugin) {
            create2 = (Create2Plugin) plugin;
            getLogger().info("Create2 API hooked!");
        }
    }
}
```

### Создание кинематического узла

```java
KinematicNetworkManager manager = create2.getNetworkManager();

Vector3i position = new Vector3i(100, 64, 200);
KinematicNodeType type = KinematicNodeType.SHAFT;

manager.createNode(position, type);
```

### Получение информации о узле

```java
Vector3i position = new Vector3i(100, 64, 200);

KinematicNode node = manager.getNode(position);
if (node != null) {
    double rpm = node.getRpm();
    double stress = node.getStressImpact();
    KinematicNodeType type = node.getType();
    
    System.out.println("Node: " + type + " @ " + rpm + " RPM");
}
```

### Получение сети

```java
KinematicNetwork network = manager.getNetwork(position);
if (network != null) {
    System.out.println("Network size: " + network.getSize());
    System.out.println("Network RPM: " + network.getRpm());
    System.out.println("Stress: " + network.getStress() + "/" + network.getStressCapacity());
    System.out.println("Overstressed: " + network.isOverstressed());
}
```

## 🎨 Создание кастомных компонентов

### Шаг 1: Определить тип компонента

```java
// В KinematicNodeType.java добавьте:
CUSTOM_GENERATOR(0.0, 2048.0),  // Генератор с 2048 SU
CUSTOM_MACHINE(16.0, 0.0);      // Машина, потребляющая 16 SU
```

### Шаг 2: Создать обработчик блока

```java
@EventHandler
public void onBlockPlace(BlockPlaceEvent event) {
    Block block = event.getBlock();
    
    if (block.getType() == Material.YOUR_CUSTOM_BLOCK) {
        Vector3i pos = new Vector3i(
            block.getX(),
            block.getY(),
            block.getZ()
        );
        
        manager.createNode(pos, KinematicNodeType.CUSTOM_MACHINE);
    }
}
```

### Шаг 3: Обработка логики

```java
public class CustomMachineNode extends KinematicNode {
    
    private int processingTicks = 0;
    
    public CustomMachineNode(Vector3i position) {
        super(position, KinematicNodeType.CUSTOM_MACHINE);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Проверяем, есть ли вращение
        if (getRpm() > 0) {
            processingTicks++;
            
            // Каждые 100 тиков выполняем действие
            if (processingTicks >= 100) {
                performAction();
                processingTicks = 0;
            }
        }
    }
    
    private void performAction() {
        // Ваша логика обработки
        System.out.println("Machine processing at " + getRpm() + " RPM!");
    }
}
```

## 🔄 Работа с вращением

### Распространение вращения

```java
RotationPropagator propagator = create2.getRotationPropagator();

Vector3i sourcePos = new Vector3i(100, 64, 200);
double rpm = 32.0;

propagator.propagateRotation(network, sourcePos, rpm);
```

### Расчёт передаточного числа

```java
KinematicNode gear1 = manager.getNode(pos1);
KinematicNode gear2 = manager.getNode(pos2);

double ratio = gear1.getGearRatioTo(gear2);
double outputRpm = gear1.getOutputRpm(gear2);

System.out.println("Gear ratio: " + ratio);
System.out.println("Output RPM: " + outputRpm);
```

## 📊 Мониторинг и статистика

### Получение статистики сетей

```java
KinematicNetworkManager.NetworkStats stats = manager.getStats();

System.out.println("Total networks: " + stats.totalNetworks);
System.out.println("Total components: " + stats.totalComponents);
System.out.println("Average size: " + stats.avgNetworkSize);
System.out.println("Largest network: " + stats.largestNetwork);
System.out.println("Overstressed: " + stats.overstressedNetworks);
```

### Проверка состояния сети

```java
if (network.isOverstressed()) {
    System.out.println("Network is overstressed!");
    System.out.println("Stress: " + network.getStress());
    System.out.println("Capacity: " + network.getStressCapacity());
}
```

## 🎯 Примеры использования

### Пример 1: Автоматический генератор

```java
public class AutoGenerator implements Listener {
    
    private Create2Plugin create2;
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType() == Material.REDSTONE_BLOCK) {
            Vector3i pos = toVector3i(event.getBlock().getLocation());
            
            // Создаём мотор
            KinematicNetworkManager manager = create2.getNetworkManager();
            manager.createNode(pos, KinematicNodeType.MOTOR);
            
            // Устанавливаем RPM
            KinematicNode node = manager.getNode(pos);
            node.setRpm(128.0);
            node.setStressCapacity(5000.0);
            
            event.getPlayer().sendMessage("Generator created!");
        }
    }
}
```

### Пример 2: Детектор перегрузки

```java
public class OverloadDetector {
    
    public void checkNetwork(KinematicNetwork network, Player player) {
        if (network.isOverstressed()) {
            double percentage = network.getStressPercentage();
            
            player.sendMessage(ChatColor.RED + "⚠ Network Overloaded!");
            player.sendMessage(ChatColor.YELLOW + "Stress: " + 
                String.format("%.1f%%", percentage));
            
            // Воспроизведём звук предупреждения
            player.playSound(
                player.getLocation(),
                Sound.BLOCK_ANVIL_LAND,
                1.0f,
                0.5f
            );
        }
    }
}
```

### Пример 3: Кастомный механизм обработки

```java
public class CustomProcessor implements Runnable {
    
    private KinematicNode node;
    private Inventory inputInv;
    private Inventory outputInv;
    
    @Override
    public void run() {
        // Проверяем вращение
        if (node.getRpm() < 16.0) {
            return; // Недостаточно скорости
        }
        
        // Проверяем перегрузку
        KinematicNetwork network = getNetwork(node);
        if (network.isOverstressed()) {
            return; // Сеть перегружена
        }
        
        // Обрабатываем предметы
        ItemStack input = inputInv.getItem(0);
        if (input != null && input.getType() == Material.IRON_ORE) {
            // Время обработки зависит от RPM
            int ticks = (int) (100 / (node.getRpm() / 16.0));
            
            // Создаём результат
            ItemStack output = new ItemStack(Material.IRON_INGOT, 2);
            outputInv.addItem(output);
            
            // Уменьшаем входной предмет
            input.setAmount(input.getAmount() - 1);
        }
    }
}
```

## 🔍 Отладка

### Включение debug режима

```java
create2.getConfig().set("debug.show-networks", true);
create2.saveConfig();
```

### Визуализация сетей

```java
public void visualizeNetwork(KinematicNetwork network, Player player) {
    for (Vector3i pos : network.getComponents()) {
        Location loc = new Location(
            player.getWorld(),
            pos.x + 0.5,
            pos.y + 0.5,
            pos.z + 0.5
        );
        
        // Спавн частиц
        player.spawnParticle(
            Particle.REDSTONE,
            loc,
            10,
            new Particle.DustOptions(Color.AQUA, 1.0f)
        );
    }
}
```

## 📝 События

### Слушатель событий сети

```java
// TODO: Implement custom events
public class NetworkEvent extends Event {
    private final KinematicNetwork network;
    private final NetworkEventType type;
    
    public enum NetworkEventType {
        CREATED,
        MERGED,
        SPLIT,
        OVERSTRESSED
    }
}
```

## ⚠️ Важные замечания

1. **Async операции**: Некоторые операции могут быть async, используйте синхронизацию
2. **Производительность**: Большие сети (>500 блоков) могут влиять на TPS
3. **Персистентность**: Сети пока не сохраняются при перезагрузке (TODO)

## 🔗 Полезные ссылки

- [GitHub Repository](#)
- [Wiki Documentation](#)
- [Discord Support](#)
