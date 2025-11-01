# ResourceFinder

A Minecraft resource locator tool for single-player worlds. Find any block type in your world and get guided navigation to the nearest instances.

## Features

- Parse Minecraft Java Edition world files
- Locate any block type (diamonds, ancient debris, emeralds, etc.)
- Calculate distances from your current position
- Sort results by proximity
- Support for all block types via Minecraft block IDs

## Installation

```bash
pip install -r requirements.txt
```

## Usage

```bash
python resource_finder.py --world /path/to/minecraft/saves/WorldName --block diamond_ore
```

## Requirements

- Python 3.8+
- Minecraft Java Edition world saves
- anvil-parser library

## Supported Block Types

Common resources:
- diamond_ore
- deepslate_diamond_ore
- ancient_debris
- emerald_ore
- gold_ore
- iron_ore
- coal_ore
- lapis_ore
- redstone_ore

And many more! Use any valid Minecraft block ID.
