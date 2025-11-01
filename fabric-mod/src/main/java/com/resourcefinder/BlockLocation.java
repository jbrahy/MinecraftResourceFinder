package com.resourcefinder;

import net.minecraft.util.math.BlockPos;

public class BlockLocation {
	public final BlockPos pos;
	public final double distance;
	public final String direction;

	public BlockLocation(BlockPos pos, double distance, String direction) {
		this.pos = pos;
		this.distance = distance;
		this.direction = direction;
	}

	public static double calculateDistance(BlockPos from, BlockPos to) {
		double dx = to.getX() - from.getX();
		double dy = to.getY() - from.getY();
		double dz = to.getZ() - from.getZ();
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	public static double calculateHorizontalDistance(BlockPos from, BlockPos to) {
		double dx = to.getX() - from.getX();
		double dz = to.getZ() - from.getZ();
		return Math.sqrt(dx * dx + dz * dz);
	}

	public static String calculateDirection(BlockPos from, BlockPos to) {
		double dx = to.getX() - from.getX();
		double dz = to.getZ() - from.getZ();

		double angle = Math.toDegrees(Math.atan2(dx, -dz));
		if (angle < 0) {
			angle += 360;
		}

		String[] directions = {
			"North", "North-Northeast", "Northeast", "East-Northeast",
			"East", "East-Southeast", "Southeast", "South-Southeast",
			"South", "South-Southwest", "Southwest", "West-Southwest",
			"West", "West-Northwest", "Northwest", "North-Northwest"
		};

		int index = (int)((angle + 11.25) / 22.5) % 16;
		return directions[index];
	}
}
