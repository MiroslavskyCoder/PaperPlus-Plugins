# Clans Plugin

Full-featured clan system with territories, ranks, and member management.

## Features

- **Clan Creation**: Players can create clans with `/clan create`
- **Member Management**: Invite, kick, and manage clan members
- **Rank System**: Leader, Officer, Member ranks with permission levels
- **Territory System**: Claim chunks and protect from enemies
- **PvP Protection**: Prevent damage between clan members
- **Block Protection**: Prevent blocks from breaking in enemy territory
- **Experience**: Clan levels and experience tracking

## Commands

| Command | Usage | Permission |
|---------|-------|-----------|
| `/clan create <name>` | Create clan | clans.use |
| `/clan disband` | Disband clan | clans.use |
| `/clan invite <player>` | Invite player | clans.use |
| `/clan kick <player>` | Kick member | clans.use |
| `/clan rank <player> <rank>` | Change rank | clans.use |
| `/clan info` | View clan info | clans.use |
| `/clan list` | List all clans | clans.use |
| `/clan territory` | Territory commands | clans.use |

## Permissions

- `clans.use` - Use clan system (default: true)
- `clans.admin` - Manage all clans (default: op)

## Configuration

```yaml
clan:
  max-members: 50
  max-level: 5
  exp-to-level: 10000
  territory:
    enabled: true
    chunks-per-member: 5
```

## Ranks

- **Leader**: Full control, can disband, manage ranks
- **Officer**: Can invite/kick regular members, edit clan
- **Member**: Can build in territory, view info

## Territory

- Claims are per chunk (16x16 blocks)
- Members can build freely in clan territory
- Enemy players cannot break blocks
- Members can't damage each other

## Version

0.1.0

## Author

WebX
