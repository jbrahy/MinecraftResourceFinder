package com.resourcefinder;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

public class NavigationHud {
	private static BlockPos target_position = null;
	private static String target_name = "Target";

	public static void register() {
		HudRenderCallback.EVENT.register(NavigationHud::render);
	}

	public static void set_target(BlockPos position, String name) {
		target_position = position;
		target_name = name != null ? name : "Target";
	}

	public static void set_target(BlockPos position) {
		set_target(position, null);
	}

	public static void clear_target() {
		target_position = null;
		target_name = "Target";
	}

	public static BlockPos get_target() {
		return target_position;
	}

	public static boolean has_target() {
		return target_position != null;
	}

	private static void render(DrawContext context, RenderTickCounter tick_counter) {
		if (target_position == null) {
			return;
		}

		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player == null) {
			return;
		}

		PlayerEntity player = client.player;
		BlockPos player_position = player.getBlockPos();

		// Calculate distance and direction
		double distance = BlockLocation.calculateHorizontalDistance(player_position, target_position);
		String direction = BlockLocation.calculateDirection(player_position, target_position);

		// Calculate vertical difference
		int vertical_diff = target_position.getY() - player_position.getY();
		String vertical_indicator = vertical_diff > 0 ? "↑" : (vertical_diff < 0 ? "↓" : "→");

		// Render HUD in top-right corner
		int screen_width = context.getScaledWindowWidth();
		int x_offset = screen_width - 10;
		int y_offset = 10;

		// Background panel
		int panel_width = 200;
		int panel_height = 60;
		context.fill(x_offset - panel_width, y_offset, x_offset, y_offset + panel_height, 0xAA000000);

		// Border
		context.fill(x_offset - panel_width, y_offset, x_offset, y_offset + 1, 0xFF00AAAA); // Top
		context.fill(x_offset - panel_width, y_offset + panel_height - 1, x_offset, y_offset + panel_height, 0xFF00AAAA); // Bottom
		context.fill(x_offset - panel_width, y_offset, x_offset - panel_width + 1, y_offset + panel_height, 0xFF00AAAA); // Left
		context.fill(x_offset - 1, y_offset, x_offset, y_offset + panel_height, 0xFF00AAAA); // Right

		// Title
		context.drawText(
			client.textRenderer,
			Text.literal("Navigation Target").formatted(Formatting.AQUA, Formatting.BOLD),
			x_offset - panel_width + 5,
			y_offset + 5,
			0xFFFFFF,
			true
		);

		// Target name
		context.drawText(
			client.textRenderer,
			Text.literal(target_name).formatted(Formatting.YELLOW),
			x_offset - panel_width + 5,
			y_offset + 18,
			0xFFFFFF,
			true
		);

		// Distance
		String distance_text = String.format("Distance: %.0fm", distance);
		context.drawText(
			client.textRenderer,
			Text.literal(distance_text).formatted(Formatting.WHITE),
			x_offset - panel_width + 5,
			y_offset + 30,
			0xFFFFFF,
			true
		);

		// Direction
		String direction_text = String.format("Direction: %s %s", direction, vertical_indicator);
		context.drawText(
			client.textRenderer,
			Text.literal(direction_text).formatted(Formatting.GOLD),
			x_offset - panel_width + 5,
			y_offset + 42,
			0xFFFFFF,
			true
		);

		// Draw directional arrow in center bottom
		render_directional_arrow(context, client, player_position);
	}

	private static void render_directional_arrow(DrawContext context, MinecraftClient client, BlockPos player_position) {
		if (target_position == null) {
			return;
		}

		int screen_width = context.getScaledWindowWidth();
		int screen_height = context.getScaledWindowHeight();
		int center_x = screen_width / 2;
		int arrow_y = screen_height - 80;

		// Calculate angle to target
		double dx = target_position.getX() - player_position.getX();
		double dz = target_position.getZ() - player_position.getZ();
		double angle_to_target = Math.toDegrees(Math.atan2(dx, -dz));

		// Get player's yaw (facing direction)
		float player_yaw = client.player.getYaw();

		// Calculate relative angle
		double relative_angle = angle_to_target - player_yaw;
		while (relative_angle > 180) relative_angle -= 360;
		while (relative_angle < -180) relative_angle += 360;

		// Draw arrow indicator
		String arrow;
		if (Math.abs(relative_angle) < 15) {
			arrow = "▲"; // Straight ahead
		} else if (relative_angle < -15 && relative_angle > -75) {
			arrow = "◄"; // Left
		} else if (relative_angle > 15 && relative_angle < 75) {
			arrow = "►"; // Right
		} else if (relative_angle < -75 && relative_angle > -105) {
			arrow = "◄◄"; // More left
		} else if (relative_angle > 75 && relative_angle < 105) {
			arrow = "►►"; // More right
		} else {
			arrow = "▼"; // Behind
		}

		// Draw background circle
		context.fill(center_x - 20, arrow_y - 15, center_x + 20, arrow_y + 15, 0xAA000000);

		// Draw arrow
		int arrow_width = client.textRenderer.getWidth(arrow);
		context.drawText(
			client.textRenderer,
			Text.literal(arrow).formatted(Formatting.YELLOW, Formatting.BOLD),
			center_x - arrow_width / 2,
			arrow_y - 4,
			0xFFFF00,
			true
		);
	}
}
