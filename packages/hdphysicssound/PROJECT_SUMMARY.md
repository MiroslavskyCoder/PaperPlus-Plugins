# HDPhysicsSound - Summary

**Что сделано**
- Новый модуль `hdphysicssound` добавлен в `settings.gradle.kts`
- Реализован плагин с физикой звука (затухание, окклюзия, простой реверб)
- Добавлены команды, слушатели событий, конфиг и документация

**Ключевые классы**
- `HDPhysicsSoundPlugin` — точка входа, загрузка конфига
- `PhysicsConfig` — все параметры физики и отладки
- `SoundPhysicsEngine` — расчёт затухания/окклюзии, проигрывание звука, реверб
- `OcclusionCalculator` — ray-trace для окклюзии
- `ReverbProfile` — задержка и громкость хвоста
- `SoundEventListener` — события (break/damage/explode/interact)
- `HDPSCommand` — `/hdps test|reload|debug`

**Ресурсы**
- `plugin.yml` — метаданные и команды
- `config.yml` — параметры физики
- `README.md` — инструкция и описание

**Сборка**
```bash
gradle :hdphysicssound:build
# JAR: packages/hdphysicssound/build/libs/HDPhysicsSound-1.0.0.jar
```

**Примечание**
Реализация оригинальная, без копирования кода из sound-physics-remastered; использованы идеи окклюзии/затухания и упрощённый реверб.
