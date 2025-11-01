# Prism Launcher Setup Guide

## Step 1: Install Java 21 (if needed)

```bash
# macOS with Homebrew
brew install openjdk@21

# Verify installation
java -version
```

## Step 2: Build the Mod

```bash
cd /Users/jbrahy/OtherProjects/Minecraft/DiamondViewer/fabric-mod
./gradlew build
```

The mod JAR will be at:
```
/Users/jbrahy/OtherProjects/Minecraft/DiamondViewer/fabric-mod/build/libs/resourcefinder-1.0.0.jar
```

## Step 3: Set Up in Prism Launcher

### Create/Edit Your Instance

1. **Open Prism Launcher**

2. **Select or Create Instance**
   - Right-click your instance → "Edit Instance"
   - Or create new instance with Minecraft 1.21.1

3. **Install Fabric**
   - Click "Version" tab
   - Click "Install Fabric"
   - Select latest Fabric Loader version

4. **Install Fabric API**
   - Click "Mods" tab
   - Click "Download mods"
   - Search for "Fabric API"
   - Download version for 1.21.1

5. **Add ResourceFinder Mod**
   - Click "Mods" tab
   - Click "Add File" or "Add .jar"
   - Navigate to:
     `/Users/jbrahy/OtherProjects/Minecraft/DiamondViewer/fabric-mod/build/libs/`
   - Select `resourcefinder-1.0.0.jar`

### Alternative: Development Mode

For easier testing/development, you can add the build folder directly:

1. In Prism, right-click instance → "Folder" → "Mods"
2. Create a symlink to auto-update:
   ```bash
   ln -s /Users/jbrahy/OtherProjects/Minecraft/DiamondViewer/fabric-mod/build/libs/resourcefinder-1.0.0.jar ~/path/to/prism/instances/YourInstance/.minecraft/mods/
   ```

## Step 4: Launch and Test

1. Launch your instance from Prism
2. Create/join a world
3. Press `T` to open chat
4. Type: `/rf diamond`

You should see the ResourceFinder scanning for diamonds!

## Usage

### Basic Commands
```
/rf diamond
/rf ancient_debris
/rf emerald
/resourcefinder coal
```

### Aliases
- `diamond`, `diamonds` → diamond_ore
- `debris`, `netherite` → ancient_debris
- `gold`, `iron`, `coal`, `copper`
- And more! (see README.md)

### Features
- Click coordinates to teleport
- Shows distance and direction
- Auto-detects your position
- Works in all dimensions

## Development Workflow

When you make changes to the mod:

```bash
# 1. Edit Java files
# 2. Rebuild
cd fabric-mod
./gradlew build

# 3. Restart Minecraft
# Prism will use the updated JAR automatically!
```

## Troubleshooting

**Mod not showing in mods list:**
- Check Fabric Loader is installed
- Check Fabric API is installed
- Look at the log in Prism (click "Launch" tab after starting)

**Command not working:**
- Open chat and start typing `/rf` - it should auto-complete
- Check the Prism log for errors
- Make sure you're in-game (not main menu)

**Build fails:**
- Ensure Java 21 is installed: `java -version`
- Try: `./gradlew clean build`

## Quick Reference

**Mod location:**
```
/Users/jbrahy/OtherProjects/Minecraft/DiamondViewer/fabric-mod/build/libs/resourcefinder-1.0.0.jar
```

**Build command:**
```bash
cd /Users/jbrahy/OtherProjects/Minecraft/DiamondViewer/fabric-mod && ./gradlew build
```

**Test in-game:**
```
/rf diamond
```
