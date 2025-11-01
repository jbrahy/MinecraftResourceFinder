#!/usr/bin/env python3
"""
ResourceFinder - Minecraft Resource Locator
Find any block type in your Minecraft world and get navigation guidance.
"""

import argparse
import sys
from pathlib import Path
from world_parser import WorldParser
from block_database import get_block_id, list_common_resources
from navigation import sort_by_distance, get_direction, calculate_horizontal_distance

def progress_callback(current, total, region_name):
    """Print progress updates while scanning."""
    percent = (current / total) * 100
    print(f"\rScanning regions: {current}/{total} ({percent:.1f}%) - {region_name}", end='', flush=True)

def find_resources(world_path, block_type, player_pos=None, dimension='overworld', max_results=100):
    """
    Find resources in a Minecraft world.

    Args:
        world_path: Path to Minecraft world folder
        block_type: Type of block to find
        player_pos: Optional tuple (x, y, z) of player position
        dimension: Dimension to search in
        max_results: Maximum number of results to find

    Returns:
        List of block locations
    """
    print(f"\nResourceFinder - Locating {block_type}")
    print("=" * 60)

    block_id = get_block_id(block_type)
    print(f"Block ID: {block_id}")
    print(f"Dimension: {dimension}")
    print(f"World: {world_path}")
    print()

    try:
        parser = WorldParser(world_path)
    except ValueError as e:
        print(f"Error: {e}")
        return []

    available_dims = parser.get_dimensions()
    if dimension not in available_dims:
        print(f"Error: Dimension '{dimension}' not found in world.")
        print(f"Available dimensions: {', '.join(available_dims)}")
        return []

    print(f"Scanning world for {block_type}...")
    print()

    locations = parser.find_blocks(
        block_id,
        dimension=dimension,
        max_results=max_results,
        progress_callback=progress_callback
    )

    print()
    print()
    print(f"Found {len(locations)} blocks")
    print()

    return locations

def display_results(locations, player_pos=None, limit=20):
    """
    Display found resources with navigation info.

    Args:
        locations: List of (x, y, z) tuples
        player_pos: Optional player position for distance calculation
        limit: Maximum number of results to display
    """
    if not locations:
        print("No blocks found.")
        return

    if player_pos:
        print(f"Your position: X={player_pos[0]}, Y={player_pos[1]}, Z={player_pos[2]}")
        print()

        sorted_locs = sort_by_distance(locations, player_pos)

        print(f"Nearest {min(limit, len(sorted_locs))} locations:")
        print("-" * 80)
        print(f"{'#':<4} {'X':<8} {'Y':<6} {'Z':<8} {'Distance':<12} {'Direction':<15}")
        print("-" * 80)

        for idx, (dist, x, y, z) in enumerate(sorted_locs[:limit], 1):
            direction = get_direction(player_pos, (x, y, z))
            h_dist = calculate_horizontal_distance(player_pos, (x, y, z))

            print(f"{idx:<4} {x:<8} {y:<6} {z:<8} {h_dist:<12.1f} {direction:<15}")

    else:
        print(f"Showing first {min(limit, len(locations))} locations:")
        print("-" * 60)
        print(f"{'#':<4} {'X':<8} {'Y':<6} {'Z':<8}")
        print("-" * 60)

        for idx, (x, y, z) in enumerate(locations[:limit], 1):
            print(f"{idx:<4} {x:<8} {y:<6} {z:<8}")

    print("-" * 80)
    print()

def main():
    """Main entry point for ResourceFinder CLI."""
    parser = argparse.ArgumentParser(
        description='Find resources in Minecraft worlds',
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog='''
Examples:
  python resource_finder.py --world ~/minecraft/saves/MyWorld --block diamond_ore
  python resource_finder.py --world ~/minecraft/saves/MyWorld --block ancient_debris --dimension nether --position 100 64 200
  python resource_finder.py --list

Common block types:
  diamond_ore, deepslate_diamond_ore, ancient_debris, emerald_ore,
  gold_ore, iron_ore, coal_ore, lapis_ore, redstone_ore
        '''
    )

    parser.add_argument(
        '--world',
        type=str,
        help='Path to Minecraft world folder'
    )

    parser.add_argument(
        '--block',
        type=str,
        help='Block type to find (e.g., diamond_ore, ancient_debris)'
    )

    parser.add_argument(
        '--dimension',
        type=str,
        default='overworld',
        choices=['overworld', 'nether', 'end'],
        help='Dimension to search (default: overworld)'
    )

    parser.add_argument(
        '--position',
        type=int,
        nargs=3,
        metavar=('X', 'Y', 'Z'),
        help='Your current position for distance calculation'
    )

    parser.add_argument(
        '--max',
        type=int,
        default=100,
        help='Maximum number of blocks to find (default: 100)'
    )

    parser.add_argument(
        '--limit',
        type=int,
        default=20,
        help='Maximum number of results to display (default: 20)'
    )

    parser.add_argument(
        '--list',
        action='store_true',
        help='List common resource types'
    )

    args = parser.parse_args()

    if args.list:
        print("\nCommon resource types:")
        print("-" * 40)
        for resource in list_common_resources():
            print(f"  {resource}")
        print()
        return

    if not args.world or not args.block:
        parser.print_help()
        print("\nError: --world and --block are required (or use --list)")
        sys.exit(1)

    player_pos = tuple(args.position) if args.position else None

    locations = find_resources(
        args.world,
        args.block,
        player_pos=player_pos,
        dimension=args.dimension,
        max_results=args.max
    )

    display_results(locations, player_pos=player_pos, limit=args.limit)

if __name__ == '__main__':
    main()
