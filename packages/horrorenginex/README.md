# HorrorEngineX Plugin

A Minecraft plugin that adds horror and atmospheric effects to the server.

## Features

- 👻 **Horror Events** - Random scary events occur while playing
- 🔊 **Sound Effects** - Creepy ambient sounds and horror audio
- ✨ **Atmospheric Effects** - Potion effects like blindness, slowness, darkness
- 📢 **Horror Messages** - Creepy messages and jump scares
- ⚙️ **Fully Configurable** - All features can be toggled

## Horror Effects

- **Blindness** - Temporary darkness effect
- **Slowness** - Movement slowdown effect
- **Nausea** - Screen rotation effect
- **Darkness** - Ambient darkness potion
- **Creeper Stare** - Feels like something is watching you
- **Phantom Sounds** - Eerie phantom noises

## Commands

- `/horrorenginex` or `/hex` - Main command
- `/hex help` - Show help message
- `/hex status` - Show current status
- `/hex event start|stop` - Control horror events
- `/hex effects trigger|list` - Trigger or list effects
- `/hex bypass` - Toggle horror effects for yourself
- `/hex reload` - Reload configuration

## Permissions

- `horrorenginex.admin` - Admin commands
- `horrorenginex.use` - Use plugin features (default: true)
- `horrorenginex.bypass` - Bypass horror effects

## Configuration

Edit `plugins/HorrorEngineX/config.yml`:

```yaml
horror-events:
  enabled: true
  frequency: 0.001

effects:
  sounds:
    enabled: true
  atmospheric:
    enabled: true
```

## Version
1.0.0

## Author
miroslavsky
