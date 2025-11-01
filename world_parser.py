"""
Minecraft world file parser.
Reads region files and locates specific block types.
"""

import os
from pathlib import Path
from anvil import Region, Block

class WorldParser:
    def __init__(self, world_path):
        """
        Initialize the world parser.

        Args:
            world_path: Path to Minecraft world folder
        """
        self.world_path = Path(world_path)
        self.region_path = self.world_path / 'region'
        self.nether_region_path = self.world_path / 'DIM-1' / 'region'
        self.end_region_path = self.world_path / 'DIM1' / 'region'

        if not self.world_path.exists():
            raise ValueError(f"World path does not exist: {world_path}")

    def find_blocks(self, block_id, dimension='overworld', max_results=None, progress_callback=None):
        """
        Find all instances of a specific block type in the world.

        Args:
            block_id: Minecraft block ID (e.g., 'minecraft:diamond_ore')
            dimension: 'overworld', 'nether', or 'end'
            max_results: Maximum number of blocks to find (None for unlimited)
            progress_callback: Optional function to call with progress updates

        Returns:
            List of tuples (x, y, z) for each block found
        """
        if dimension == 'overworld':
            region_path = self.region_path
        elif dimension == 'nether':
            region_path = self.nether_region_path
        elif dimension == 'end':
            region_path = self.end_region_path
        else:
            raise ValueError(f"Unknown dimension: {dimension}")

        if not region_path.exists():
            return []

        locations = []
        region_files = list(region_path.glob('*.mca'))
        total_regions = len(region_files)

        for idx, region_file in enumerate(region_files):
            if progress_callback:
                progress_callback(idx + 1, total_regions, region_file.name)

            try:
                region = Region.from_file(str(region_file))
                found = self._scan_region(region, block_id)
                locations.extend(found)

                if max_results and len(locations) >= max_results:
                    locations = locations[:max_results]
                    break

            except Exception as e:
                print(f"Error reading region {region_file.name}: {e}")
                continue

        return locations

    def _scan_region(self, region, block_id):
        """
        Scan a single region file for specific blocks.

        Args:
            region: Anvil Region object
            block_id: Block ID to search for

        Returns:
            List of (x, y, z) tuples
        """
        locations = []

        for chunk_x in range(32):
            for chunk_z in range(32):
                try:
                    chunk = region.get_chunk(chunk_x, chunk_z)
                    if chunk is None:
                        continue

                    found = self._scan_chunk(chunk, block_id)
                    locations.extend(found)

                except Exception:
                    continue

        return locations

    def _scan_chunk(self, chunk, block_id):
        """
        Scan a single chunk for specific blocks.

        Args:
            chunk: Anvil Chunk object
            block_id: Block ID to search for

        Returns:
            List of (x, y, z) tuples
        """
        locations = []

        min_y = chunk.get_min_y()
        max_y = chunk.get_max_y()

        for x in range(16):
            for z in range(16):
                for y in range(min_y, max_y):
                    try:
                        block = chunk.get_block(x, y, z)

                        if block and block.id == block_id:
                            world_x = chunk.x * 16 + x
                            world_z = chunk.z * 16 + z
                            locations.append((world_x, y, world_z))

                    except Exception:
                        continue

        return locations

    def get_dimensions(self):
        """
        Get list of available dimensions in this world.

        Returns:
            List of dimension names
        """
        dimensions = []

        if self.region_path.exists():
            dimensions.append('overworld')

        if self.nether_region_path.exists():
            dimensions.append('nether')

        if self.end_region_path.exists():
            dimensions.append('end')

        return dimensions
