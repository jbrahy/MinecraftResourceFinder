# ResourceFinder Quick Start Guide

This project contains TWO tools for finding resources in Minecraft:

## 1. Python Tool (External - Works Now)

Analyzes saved world files from outside the game.

**Location:** Root directory

**Usage:**
```bash
python resource_finder.py --world ~/Library/Application\ Support/minecraft/saves/YourWorld --block diamond_ore --position 100 64 200
```

**Pros:**
- Works immediately (already installed)
- Can scan entire world, even unloaded chunks
- No game modifications needed

**Cons:**
- Must be run outside Minecraft
- Need to know your coordinates manually

---

## 2. Fabric Mod (In-Game - Requires Setup)

In-game commands for instant resource finding.

**Location:** `fabric-mod/` directory

**Commands:**
```
/rf diamond
/rf ancient_debris
/resourcefinder emerald
```

**Features:**
- Auto-detects your position
- Clickable coordinates to teleport
- Shows distance and direction
- Works while playing

**Setup Required:**
1. Install Java 21
2. Build the mod: `cd fabric-mod && ./gradlew build`
3. Install Fabric Loader and Fabric API
4. Copy JAR to mods folder

**Full instructions:** See `fabric-mod/INSTALLATION.md`

---

## Recommended Workflow

1. **Start with Python tool** - Use it right now to find resources in your existing worlds
2. **Install the mod when ready** - For better in-game experience

---

## Python Tool - Detailed Usage

### Find diamonds near your position:
```bash
python resource_finder.py \
  --world "~/Library/Application Support/minecraft/saves/MyWorld" \
  --block diamond_ore \
  --position 100 64 -200
```

### Find ancient debris in the Nether:
```bash
python resource_finder.py \
  --world "~/Library/Application Support/minecraft/saves/MyWorld" \
  --block ancient_debris \
  --dimension nether \
  --position 50 15 100
```

### List all available block types:
```bash
python resource_finder.py --list
```

---

## Fabric Mod - In-Game Commands

Once installed, just type in chat:

```
/rf diamond          → Find diamond ore
/rf debris           → Find ancient debris
/rf emerald          → Find emerald ore
/resourcefinder coal → Find coal ore (long form)
```

Results show:
- Clickable coordinates (click to teleport!)
- Distance from you
- Cardinal direction
- Sorted by proximity

---

## Project Structure

```
DiamondViewer/
├── resource_finder.py      # Python CLI tool (ready to use)
├── world_parser.py         # World file scanner
├── block_database.py       # Block type definitions
├── navigation.py           # Distance/direction calc
├── requirements.txt        # Python dependencies
├── README.md              # Python tool docs
│
└── fabric-mod/            # In-game mod
    ├── src/               # Java source code
    ├── build.gradle       # Build configuration
    ├── README.md          # Mod usage guide
    └── INSTALLATION.md    # Setup instructions
```

---

## Which Tool Should I Use?

**Use Python Tool if:**
- You want to start immediately
- You want to scan the entire world
- You don't want to modify your game

**Use Fabric Mod if:**
- You play frequently and want quick access
- You want in-game clickable coordinates
- You don't mind installing mods

**Use Both!**
- Python tool for world overview
- Mod for quick in-game checks
