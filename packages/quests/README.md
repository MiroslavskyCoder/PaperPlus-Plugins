# Quests Plugin

Quest system with objectives, rewards, and progress tracking.

## Features

- **Quest System**: Accept, progress, abandon, and complete quests
- **Objectives**: Kill, break blocks, collect items, reach locations
- **Progress Tracking**: Automatic progress updates for objectives
- **Rewards**: Money, experience points, and items
- **Leaderboard**: Track quest completions per player
- **Admin Tools**: Create and manage quests via commands

## Commands

| Command | Usage | Permission |
|---------|-------|-----------|
| `/quests list` | View available quests | quests.use |
| `/quests accept <quest>` | Accept quest | quests.use |
| `/quests info <quest>` | View quest details | quests.use |
| `/quests abandon <quest>` | Abandon quest | quests.use |

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

## Integration

- **Economy Plugin**: Provides money rewards
- **Vault**: Ready for Vault integration

## Version

0.1.0

## Author

WebX
