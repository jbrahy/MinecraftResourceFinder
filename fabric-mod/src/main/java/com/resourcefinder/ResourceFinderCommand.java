package com.resourcefinder;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ResourceFinderCommand {

	private static final Map<String, String> BLOCK_ALIASES = new HashMap<>();
	private static final Map<UUID, BlockPos> PLAYER_TARGETS = new ConcurrentHashMap<>();

	static {
		BLOCK_ALIASES.put("diamond", "minecraft:diamond_ore");
		BLOCK_ALIASES.put("diamonds", "minecraft:diamond_ore");
		BLOCK_ALIASES.put("deepslate_diamond", "minecraft:deepslate_diamond_ore");
		BLOCK_ALIASES.put("ancient_debris", "minecraft:ancient_debris");
		BLOCK_ALIASES.put("debris", "minecraft:ancient_debris");
		BLOCK_ALIASES.put("netherite", "minecraft:ancient_debris");
		BLOCK_ALIASES.put("emerald", "minecraft:emerald_ore");
		BLOCK_ALIASES.put("emeralds", "minecraft:emerald_ore");
		BLOCK_ALIASES.put("gold", "minecraft:gold_ore");
		BLOCK_ALIASES.put("iron", "minecraft:iron_ore");
		BLOCK_ALIASES.put("coal", "minecraft:coal_ore");
		BLOCK_ALIASES.put("copper", "minecraft:copper_ore");
		BLOCK_ALIASES.put("lapis", "minecraft:lapis_ore");
		BLOCK_ALIASES.put("redstone", "minecraft:redstone_ore");
		BLOCK_ALIASES.put("quartz", "minecraft:nether_quartz_ore");
	}

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(CommandManager.literal("resourcefinder")
			.then(CommandManager.argument("block", StringArgumentType.string())
				.executes(context -> execute(context, StringArgumentType.getString(context, "block")))));

		dispatcher.register(CommandManager.literal("rf")
			.then(CommandManager.argument("block", StringArgumentType.string())
				.executes(context -> execute(context, StringArgumentType.getString(context, "block"))))
			.then(CommandManager.literal("guide")
				.then(CommandManager.argument("coords", StringArgumentType.greedyString())
					.executes(context -> setGuideTarget(context, StringArgumentType.getString(context, "coords")))))
			.then(CommandManager.literal("clear")
				.executes(ResourceFinderCommand::clearGuideTarget)));
	}

	private static int execute(CommandContext<ServerCommandSource> context, String blockInput) {
		ServerCommandSource source = context.getSource();

		if (!(source.getEntity() instanceof ServerPlayerEntity player)) {
			source.sendError(Text.literal("This command can only be used by players"));
			return 0;
		}

		String blockId = resolveBlockId(blockInput);
		ServerWorld world = player.getServerWorld();
		BlockPos playerPos = player.getBlockPos();

		source.sendFeedback(() -> Text.literal("Searching for ")
			.append(Text.literal(blockId).formatted(Formatting.YELLOW))
			.append(Text.literal("...").formatted(Formatting.GRAY)), false);

		int searchRadius = 128;
		int maxResults = 50;

		new Thread(() -> {
			try {
				List<BlockLocation> locations = WorldScanner.findBlocks(world, playerPos, blockId, maxResults, searchRadius);

				world.getServer().execute(() -> {
					if (locations.isEmpty()) {
						source.sendFeedback(() -> Text.literal("No ")
							.append(Text.literal(blockId).formatted(Formatting.YELLOW))
							.append(Text.literal(" found within " + searchRadius + " blocks").formatted(Formatting.RED)), false);
					} else {
						displayResults(source, locations, blockId, playerPos);
					}
				});
			} catch (Exception e) {
				world.getServer().execute(() -> {
					source.sendError(Text.literal("Error scanning world: " + e.getMessage()));
					ResourceFinderMod.LOGGER.error("Error scanning world", e);
				});
			}
		}).start();

		return 1;
	}

	private static String resolveBlockId(String input) {
		String lower = input.toLowerCase();

		if (BLOCK_ALIASES.containsKey(lower)) {
			return BLOCK_ALIASES.get(lower);
		}

		if (!input.contains(":")) {
			return "minecraft:" + lower;
		}

		return input;
	}

	private static void displayResults(ServerCommandSource source, List<BlockLocation> locations, String blockId, BlockPos playerPos) {
		source.sendFeedback(() -> Text.literal("─────────────────────────────────────────────").formatted(Formatting.DARK_GRAY), false);

		source.sendFeedback(() -> Text.literal("Found ")
			.append(Text.literal(String.valueOf(locations.size())).formatted(Formatting.GREEN, Formatting.BOLD))
			.append(Text.literal(" × ").formatted(Formatting.DARK_GRAY))
			.append(Text.literal(blockId).formatted(Formatting.YELLOW))
			.append(Text.literal(" (showing nearest " + Math.min(20, locations.size()) + ")").formatted(Formatting.GRAY)), false);

		source.sendFeedback(() -> Text.literal("─────────────────────────────────────────────").formatted(Formatting.DARK_GRAY), false);

		int count = 0;
		for (BlockLocation loc : locations) {
			if (count >= 20) break;
			count++;

			final int index = count;
			BlockPos pos = loc.pos;

			source.sendFeedback(() -> {
				MutableText message = Text.literal(String.format("#%-2d ", index)).formatted(Formatting.DARK_GRAY);

				MutableText coords = Text.literal(String.format("[%d, %d, %d]", pos.getX(), pos.getY(), pos.getZ()))
					.formatted(Formatting.AQUA, Formatting.UNDERLINE)
					.styled(style -> style
						.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
							String.format("/rfclient guide %d %d %d", pos.getX(), pos.getY(), pos.getZ())))
						.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							Text.literal("Click to set navigation target").formatted(Formatting.GREEN))));

				message.append(coords);

				String distText = String.format(" %.0fm", loc.distance);
				message.append(Text.literal(distText).formatted(Formatting.GRAY));

				message.append(Text.literal(" " + loc.direction).formatted(Formatting.YELLOW));

				return message;
			}, false);
		}

		source.sendFeedback(() -> Text.literal("─────────────────────────────────────────────").formatted(Formatting.DARK_GRAY), false);

		source.sendFeedback(() -> Text.literal("Tip: Click coordinates to set navigation target!").formatted(Formatting.DARK_GRAY, Formatting.ITALIC), false);
	}

	private static int setGuideTarget(CommandContext<ServerCommandSource> context, String coordsString) {
		ServerCommandSource source = context.getSource();

		if (!(source.getEntity() instanceof ServerPlayerEntity player)) {
			source.sendError(Text.literal("This command can only be used by players"));
			return 0;
		}

		try {
			String[] parts = coordsString.split("\\s+");
			if (parts.length != 3) {
				source.sendError(Text.literal("Invalid coordinates format. Use: /rf guide X Y Z"));
				return 0;
			}

			int x = Integer.parseInt(parts[0]);
			int y = Integer.parseInt(parts[1]);
			int z = Integer.parseInt(parts[2]);

			BlockPos target = new BlockPos(x, y, z);
			PLAYER_TARGETS.put(player.getUuid(), target);

			ServerWorld world = player.getServerWorld();

			// Spawn vertical beam of particles (more visible)
			for (int i = 0; i < 40; i++) {
				world.spawnParticles(
					ParticleTypes.END_ROD,
					x + 0.5, y + i * 0.5, z + 0.5,
					3, 0.15, 0.15, 0.15, 0.02
				);
			}

			// Add glowing particle ring at base
			for (int angle = 0; angle < 360; angle += 15) {
				double radians = Math.toRadians(angle);
				double offsetX = Math.cos(radians) * 2;
				double offsetZ = Math.sin(radians) * 2;
				world.spawnParticles(
					ParticleTypes.GLOW,
					x + 0.5 + offsetX, y + 0.5, z + 0.5 + offsetZ,
					2, 0.1, 0.1, 0.1, 0.01
				);
			}

			BlockPos playerPos = player.getBlockPos();
			double distance = BlockLocation.calculateHorizontalDistance(playerPos, target);
			String direction = BlockLocation.calculateDirection(playerPos, target);

			source.sendFeedback(() -> Text.literal("Navigation target set!").formatted(Formatting.GREEN, Formatting.BOLD), false);
			source.sendFeedback(() -> Text.literal("Target: ")
				.append(Text.literal(String.format("[%d, %d, %d]", x, y, z)).formatted(Formatting.AQUA))
				.append(Text.literal(String.format(" - %.0fm %s", distance, direction)).formatted(Formatting.YELLOW)), false);
			source.sendFeedback(() -> Text.literal("Particles mark the location. Use ")
				.append(Text.literal("/rf clear").formatted(Formatting.AQUA))
				.append(Text.literal(" to remove.")), false);

			return 1;

		} catch (NumberFormatException e) {
			source.sendError(Text.literal("Invalid coordinates. Use numbers only."));
			return 0;
		}
	}

	private static int clearGuideTarget(CommandContext<ServerCommandSource> context) {
		ServerCommandSource source = context.getSource();

		if (!(source.getEntity() instanceof ServerPlayerEntity player)) {
			source.sendError(Text.literal("This command can only be used by players"));
			return 0;
		}

		if (PLAYER_TARGETS.remove(player.getUuid()) != null) {
			source.sendFeedback(() -> Text.literal("Navigation target cleared!").formatted(Formatting.GREEN), false);
		} else {
			source.sendFeedback(() -> Text.literal("No navigation target set.").formatted(Formatting.GRAY), false);
		}

		return 1;
	}
}
