# HorrorEngineX Plugin

A comprehensive Minecraft plugin that adds horror, atmospheric effects, and world generation to create a truly scary server experience.

## Features

### Core Horror Effects
- 👻 **Horror Events** - Random scary events occur while playing
- 🔊 **Sound Effects** - Creepy ambient sounds and horror audio
- ✨ **Atmospheric Effects** - Potion effects like blindness, slowness, darkness
- 📢 **Horror Messages** - Creepy messages and jump scares
- 🎬 **Cinematic Effects** - Advanced visual rendering effects, screen glitches
- ⚙️ **Fully Configurable** - All features can be toggled

### World Generation System
- 🏚️ **Haunted Houses** - Auto-generated 1-8 story abandoned buildings (12x12 or 20x20 size) with loot
- 🧪 **Secret Laboratories** - Massive underground multi-room research facilities (60x60x12) with director office, scientist rooms, bathrooms, storage, cafeterias, libraries, and recreation room
- 🪵 **Sawmills** - Wood processing buildings with log piles and machinery
- 🗻 **Cave Networks** - Procedural 3x3 tunnel systems up to 600 blocks long
- 👁️ **Animal Watchers** - Pigs, cows, sheep stare at nearby players
- 💧 **Constant Rain** - Perpetual rainstorms in selected worlds
- 🪨 **Large Block Formations** - Ominous stone structures floating in air

## Horror Effects

- **Blindness** - Temporary darkness effect
- **Slowness** - Movement slowdown effect  
- **Confusion** - Screen rotation effect
- **Darkness** - Ambient darkness potion
- **Creeper Stare** - Feels like something is watching you
- **Phantom Sounds** - Eerie phantom noises
- **Screen Glitches** - Reality-bending visual distortions
- **Entity Glitches** - Weird movement and appearance bugs

## Commands

- `/horrorenginex` or `/hex` - Main command
- `/hex help` - Show help message
- `/hex status` - Show current status
- `/hex event start|stop` - Control horror events
- `/hex effects trigger|list` - Trigger or list effects
- `/hex cinematic on|off` - Toggle cinematic effects
- `/hex glitch` - Trigger random glitch effect
- `/hex bypass` - Toggle horror effects for yourself
- `/hex reload` - Reload configuration

## Permissions

- `horrorenginex.admin` - Admin commands
- `horrorenginex.use` - Use plugin features (default: true)
- `horrorenginex.bypass` - Bypass horror effects

## Configuration

Edit `plugins/HorrorEngineX/config.yml`:

### Basic Horror Effects
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

### World Generation
```yaml
world-gen:
  constant-rain: true
  rain-worlds:
    - "world"
  
  haunted-houses: true
  house-generation-chance: 0.05
  
  cave-tunnels: true
  tunnel-generation-chance: 0.03
  
  secret-labs: true
  lab-generation-chance: 0.02
  
  sawmills: true
  sawmill-generation-chance: 0.04
  
  large-blocks: true
  large-block-chance: 0.08
  
  animal-watchers: true
  animal-detection-range: 20
```

## Detailed Guides

- 📖 [World Generation Guide](./WORLD_GENERATION.md) - Complete world gen documentation
- ⚙️ [Configuration Guide](./config.yml) - Full configuration reference

## Installation

1. Drop `horrorenginex.jar` into `plugins/` folder
2. Restart server
3. Edit `plugins/HorrorEngineX/config.yml` as needed
4. Use `/hex reload` to apply changes

## Performance

- Asynchronous world generation
- Lazy structure generation (only when needed)
- Configurable generation rates
- Minimal server impact

## Dependencies

- Bukkit/Paper API 1.20.4+
- Java 17+

## Author

HorrorEngineX Development Team

    enabled: true
```

## Version
1.0.0

## Author
miroslavsky
