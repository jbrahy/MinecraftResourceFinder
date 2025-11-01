# Installation Instructions

## Prerequisites

### 1. Install Java 21

The mod requires Java 21 to build. If you're running Minecraft 1.21.x, you likely already have it installed.

**macOS:**
```bash
# Using Homebrew
brew install openjdk@21

# Set JAVA_HOME
echo 'export PATH="/opt/homebrew/opt/openjdk@21/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

**Or download from:**
- https://adoptium.net/temurin/releases/?version=21

### 2. Verify Java Installation
```bash
java -version
# Should show: openjdk version "21.x.x"
```

## Building the Mod

```bash
cd fabric-mod
./gradlew build
```

The compiled mod will be in: `build/libs/resourcefinder-1.0.0.jar`

## Installing in Minecraft

### 1. Install Fabric Loader
- Go to https://fabricmc.net/use/installer/
- Download and run the installer
- Select Minecraft version 1.21.1
- Click "Install"

### 2. Install Fabric API
- Download from: https://modrinth.com/mod/fabric-api/versions
- Select version for Minecraft 1.21.1
- Place the downloaded JAR in: `~/.minecraft/mods/`
  - macOS: `~/Library/Application Support/minecraft/mods/`
  - Windows: `%APPDATA%\.minecraft\mods\`
  - Linux: `~/.minecraft/mods/`

### 3. Install ResourceFinder Mod
- Copy `build/libs/resourcefinder-1.0.0.jar` to `~/.minecraft/mods/`

### 4. Launch Minecraft
- Open Minecraft Launcher
- Select "fabric-loader-1.21.1" profile
- Click "Play"

## Usage

Once in-game:

```
/rf diamond
/rf ancient_debris
/resourcefinder emerald
```

Click the coordinates in chat to teleport!

## Troubleshooting

**"Unknown command" error:**
- Make sure both Fabric API and ResourceFinder are in the mods folder
- Check the Minecraft logs for errors
- Verify you're using the Fabric profile

**Java version mismatch:**
- Ensure Java 21 is installed
- Check with: `java -version`

**Mod not loading:**
- Check `logs/latest.log` for errors
- Ensure Fabric API is installed
- Verify Minecraft version is 1.21.1
