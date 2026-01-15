# Quests Plugin

Командная система заданий, которую можно адаптировать под любую сборку.
Плагин сам по себе не добавляет готовых миссий: весь контент создаёт
администратор через режим редактирования в WebX Panel, после чего
игроки получают доступ к квестам прямо на сервере.

## Features

- **Team & Solo Quests**: Поддерживаются как личные задания, так и
  задачи для целой группы игроков.
- **Flexible Objectives**: Убийства, крафт, сбор ресурсов, прохождение
  точек интереса и метрики из других плагинов.
- **Progress Tracking**: Автоматическое обновление прогресса и точные
  проценты выполнения.
- **Rewards**: Деньги, опыт, предметы, очки клана — всё задаётся в
  конфиге.
- **WebX Panel Editor**: Режим админа позволяет создавать/редактировать
  задания без рестарта сервера.
- **Lightweight Flow**: Плагин не загружает игроков десятками вкладок —
  всё упрощено под сценарии "создал и пошёл играть".

## Commands

| Command | Usage | Permission |
|---------|-------|-----------|
| `/quests list` | Посмотреть доступные задания | quests.use |
| `/quests accept <quest>` | Принять выбранный квест | quests.use |
| `/quests info <quest>` | Детали и прогресс | quests.use |
| `/quests abandon <quest>` | Отказаться от текущего квеста | quests.use |

## Permissions

- `quests.use` - Use quest system (default: true)
- `quests.admin` - Create and manage quests (default: op)

## Configuration

```yaml
quest:
  max-active-quests: 10
  auto-complete: false
  
rewards:
  default-money: 100
  default-exp: 50
```

## Objective Types

- **Kill**: Kill X of a specific entity type
- **Break**: Break X blocks of a specific type
- **Collect**: Collect X of a specific item
- **Location**: Reach a specific location

## Workflow & Integration

- **WebX Panel**: Admin Server заходит в режим редактирования, создаёт
  цепочки заданий, назначает награды/условия.
- **Economy Plugin**: Используется для начисления монет.
- **Vault**: Поддерживается для совместимости с другими экономическими
  системами.
- **Shared Data**: Прогресс квестов отображается в Dashboard и может
  быть использован другими плагинами.

> ⚠️ По умолчанию список квестов пуст. Добавьте свой набор миссий перед
> тем как выдавать доступ игрокам.

## Version

0.1.0

## Author

WebX
