# ResourceFinder Fabric Mod

An in-game Minecraft mod that lets you find any resource block using commands.

## Features

- **Two Commands**: `/resourcefinder <block>` or `/rf <block>`
- **Smart Aliases**: Use shortcuts like `/rf diamond` instead of full block IDs
- **Auto-Detection**: Automatically uses your current position and dimension
- **Clickable Results**: Click coordinates in chat to teleport
- **Distance & Direction**: Shows how far and which way to go
- **Background Scanning**: Non-blocking world scan (won't freeze your game)

## Installation

1. **Install Fabric Loader**
   - Download from https://fabricmc.net/use/
   - Run the installer for Minecraft 1.21.1

2. **Install Fabric API**
   - Download from https://modrinth.com/mod/fabric-api
   - Place in `.minecraft/mods/` folder

3. **Install ResourceFinder**
   - Build this mod: `./gradlew build`
   - Find JAR in `build/libs/`
   - Place in `.minecraft/mods/` folder

## Usage

### Basic Commands
```
/rf diamond          # Find diamond ore
/rf ancient_debris   # Find ancient debris in nether
/resourcefinder emerald  # Long form command
```

### Supported Aliases
- `diamond`, `diamonds` → diamond_ore
- `debris`, `netherite` → ancient_debris
- `emerald`, `emeralds` → emerald_ore
- `gold`, `iron`, `coal`, `copper`, `lapis`, `redstone`
- `quartz` → nether_quartz_ore

### Using Full Block IDs
```
/rf minecraft:spawner
/rf minecraft:end_portal_frame
/rf deepslate_diamond_ore
```

## In-Game Display

Results show:
- **Rank** (#1, #2, etc.)
- **Coordinates** (clickable to teleport)
- **Distance** (in blocks)
- **Direction** (cardinal direction from you)

## Building from Source

```bash
cd fabric-mod
./gradlew build
```

Output: `build/libs/resourcefinder-1.0.0.jar`

## Requirements

- Minecraft 1.21.1
- Fabric Loader 0.16.9+
- Fabric API 0.108.0+
- Java 21+

## Notes

- Only searches loaded chunks within 512 blocks
- Maximum 100 results (displays top 20)
- Works in all dimensions (Overworld, Nether, End)
- Single-player and multiplayer compatible
