package com.resourcefinder.gui;

import net.minecraft.util.math.BlockPos;

public class SearchResult {
	public final String name;
	public final String type;
	public final BlockPos position;
	public final double distance;
	public final String direction;
	public final String dimension;

	public SearchResult(String name, String type, BlockPos position, double distance, String direction, String dimension) {
		this.name = name;
		this.type = type;
		this.position = position;
		this.distance = distance;
		this.direction = direction;
		this.dimension = dimension;
	}
}
