# ResourceFinder GUI Features

## Overview

ResourceFinder now includes a powerful graphical interface for searching anything in your Minecraft world!

## Opening the GUI

**Press `R` key** (default) - Opens the Resource Finder interface

You can change this keybinding in:
`Settings → Controls → Resource Finder → Open Resource Finder GUI`

## GUI Features

### 1. Search Box
- Type anything to filter results
- Live search as you type (minimum 2 characters)
- Works across all categories

### 2. Category Tabs

#### 🧱 Blocks
- Search for any block type by name
- Examples: "diamond", "iron", "emerald", "coal"
- Quick-select buttons for common ores
- Click a result to perform a full world scan
- Shows all matching block types instantly

#### 🐑 Entities
- Find mobs, animals, villagers in loaded chunks
- Search by name: "zombie", "cow", "villager", "creeper"
- Shows real-time positions
- Only searches loaded chunks (render distance)

#### 👤 Players
- Find other players in multiplayer
- Search by username
- Shows distance and direction
- Real-time position tracking

#### 🏛️ Structures
- Structure search (coming soon)
- Will locate villages, temples, etc.

### 3. Results Display

Each result shows:
- **Name** - Item/entity/player name
- **Type** - Category (Block/Entity/Player/etc.)
- **Coordinates** - [X, Y, Z]
- **Distance** - Horizontal distance in meters
- **Direction** - Cardinal direction from you
- **Dimension** - Current dimension

### 4. Click to Navigate

**Click any result** to:
- For blocks: Trigger a full world scan and find closest instances
- For entities/players: Set navigation target with particle marker
- Automatically closes GUI and runs command

### 5. Scrolling

- Mouse wheel to scroll through results
- Shows up to 10 results per page
- Displays total count at top

## Usage Examples

### Find Diamonds
1. Press `R`
2. Click "Blocks" tab
3. Type "diamond" or click diamond quick-button
4. Click a result to scan the world
5. Closest diamonds appear with navigation

### Find Villagers
1. Press `R`
2. Click "Entities" tab
3. Type "villager"
4. See all villagers in loaded chunks
5. Click one to set navigation target

### Find Players (Multiplayer)
1. Press `R`
2. Click "Players" tab
3. Type player name
4. See their position and distance
5. Click to navigate to them

## Tips

- **Quick access**: Press `R` anytime (except when another GUI is open)
- **Partial search**: Type "dia" to find "diamond_ore", "diamond_block", etc.
- **Live updates**: Entity and player positions update in real-time
- **Close**: Click "Close" button or press `ESC`
- **Navigation**: After clicking a result, use the particle beam to find the location
- **Clear markers**: Use `/rf clear` to remove navigation particles

## Commands Still Available

The original commands still work:
- `/rf diamond` - Quick block search from chat
- `/rf guide X Y Z` - Set manual navigation target
- `/rf clear` - Clear navigation markers

## Search Categories Explained

### Blocks
- Searches Minecraft's block registry
- Shows matching block types as suggestions
- Clicking performs actual world scan
- Finds blocks in loaded chunks only
- Sorted by distance (closest first)

### Entities
- Searches loaded entities in real-time
- Includes: mobs, animals, villagers, boats, minecarts
- Only shows entities in loaded chunks
- Position updates as entities move

### Players
- Searches all online players
- Excludes yourself
- Shows distance even if they're far away
- Updates in real-time

### Structures
- Planned feature
- Will use structure location data
- May require exploring to discover structures

## Performance

- **GUI is lightweight** - No lag when opening
- **Search is instant** - Filters existing data
- **World scans** - Run when you click block results
- **Entity search** - Only scans loaded chunks
- **Background scanning** - World scans don't freeze game

## Keyboard Shortcuts

- `R` - Open GUI
- `ESC` - Close GUI
- `Mouse Wheel` - Scroll results
- `Click` - Select/activate result
- `Type` - Filter search

## Future Enhancements

Potential additions:
- Structure detection and navigation
- Biome finder
- Custom waypoint saving
- Search history
- Favorite searches
- Item search (in chests, inventories)
- Map integration
- Minimap overlay
