package com.resourcefinder;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WorldScanner {

	public static List<BlockLocation> findBlocks(ServerWorld world, BlockPos playerPos, String blockId, int maxResults, int searchRadius) {
		List<BlockLocation> locations = new ArrayList<>();

		Identifier identifier = Identifier.tryParse(blockId);
		if (identifier == null) {
			System.out.println("ResourceFinder: Invalid block ID: " + blockId);
			return locations;
		}

		Block targetBlock = Registries.BLOCK.get(identifier);
		if (targetBlock == null) {
			System.out.println("ResourceFinder: Block not found in registry: " + blockId);
			return locations;
		}

		System.out.println("ResourceFinder: Scanning for " + blockId + " around " + playerPos);

		int chunkX = playerPos.getX() >> 4;
		int chunkZ = playerPos.getZ() >> 4;
		int chunkRadius = searchRadius >> 4;

		int minY = world.getBottomY();
		int maxY = world.getTopY();

		System.out.println("ResourceFinder: Y range " + minY + " to " + maxY);
		System.out.println("ResourceFinder: Chunk radius: " + chunkRadius);

		int chunksScanned = 0;
		int blocksChecked = 0;

		for (int cx = chunkX - chunkRadius; cx <= chunkX + chunkRadius; cx++) {
			for (int cz = chunkZ - chunkRadius; cz <= chunkZ + chunkRadius; cz++) {
				ChunkPos chunkPos = new ChunkPos(cx, cz);

				if (!world.isChunkLoaded(cx, cz)) {
					continue;
				}

				Chunk chunk = world.getChunk(cx, cz, ChunkStatus.FULL, false);
				if (chunk == null) {
					continue;
				}

				chunksScanned++;

				for (int x = 0; x < 16; x++) {
					for (int z = 0; z < 16; z++) {
						for (int y = minY; y < maxY; y++) {
							blocksChecked++;
							BlockPos pos = new BlockPos((cx << 4) + x, y, (cz << 4) + z);
							BlockState state = chunk.getBlockState(pos);

							if (state.getBlock() == targetBlock) {
								double distance = BlockLocation.calculateHorizontalDistance(playerPos, pos);
								String direction = BlockLocation.calculateDirection(playerPos, pos);
								locations.add(new BlockLocation(pos, distance, direction));

								if (locations.size() >= maxResults) {
									System.out.println("ResourceFinder: Found " + locations.size() + " blocks (max reached)");
									System.out.println("ResourceFinder: Scanned " + chunksScanned + " chunks, checked " + blocksChecked + " blocks");
									return sortByDistance(locations);
								}
							}
						}
					}
				}
			}
		}

		System.out.println("ResourceFinder: Found " + locations.size() + " blocks");
		System.out.println("ResourceFinder: Scanned " + chunksScanned + " chunks, checked " + blocksChecked + " blocks");
		return sortByDistance(locations);
	}

	private static List<BlockLocation> sortByDistance(List<BlockLocation> locations) {
		locations.sort(Comparator.comparingDouble(loc -> loc.distance));
		return locations;
	}
}
