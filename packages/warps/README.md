# Warps Plugin

Advanced warp/teleportation system with GUI, permissions, economy integration, and cooldowns.

## Features

- **Warp Management**: Create, delete, and list warps with `/warp`, `/setwarp`, `/delwarp` commands
- **GUI Menu**: Inventory-based warp selection with `/warps` command
- **Permissions**: Granular permission control with `warps.use`, `warps.admin`, bypass permissions
- **Economy Integration**: Optional cost per warp with `warps.bypass.cost` permission
- **Cooldowns**: Configurable teleport cooldowns with bypass permission
- **Teleport Delays**: Configurable delay with cancel-on-move and cancel-on-damage options
- **Warp Info**: Detailed warp statistics with `/warpinfo` command

## Commands

| Command | Usage | Permission |
|---------|-------|-----------|
| `/warp [name]` | Teleport to warp or open GUI | warps.use |
| `/setwarp <name>` | Create warp at current location | warps.admin |
| `/delwarp <name>` | Delete warp | warps.admin |
| `/warps` | List or open warp GUI | warps.use |
| `/warpinfo <name>` | View warp details | warps.use |

## Permissions

- `warps.use` - Use warp system (default: true)
- `warps.admin` - Create and delete warps (default: op)
- `warps.bypass.cooldown` - Bypass cooldown (default: op)
- `warps.bypass.cost` - Bypass economy cost (default: op)

## Configuration

```yaml
database:
  type: yaml # or mysql
  
teleport:
  delay: 3 # seconds
  cancel-on-move: true
  cancel-on-damage: true
  cooldown: 30 # seconds
  
economy:
  enabled: true
  cost-per-warp: 10.0
  
gui:
  enabled: true
  title: "Warps"
  rows: 3
  fill-empty: true
```

## Version

0.1.0

## Author

WebX
