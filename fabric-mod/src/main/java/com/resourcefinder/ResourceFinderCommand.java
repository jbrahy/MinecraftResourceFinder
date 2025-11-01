package com.resourcefinder;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
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

public class ResourceFinderCommand {

	private static final Map<String, String> BLOCK_ALIASES = new HashMap<>();

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
				.executes(context -> execute(context, StringArgumentType.getString(context, "block")))));
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
							String.format("/tp @s %d %d %d", pos.getX(), pos.getY(), pos.getZ())))
						.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							Text.literal("Click to teleport").formatted(Formatting.GREEN))));

				message.append(coords);

				String distText = String.format(" %.0fm", loc.distance);
				message.append(Text.literal(distText).formatted(Formatting.GRAY));

				message.append(Text.literal(" " + loc.direction).formatted(Formatting.YELLOW));

				return message;
			}, false);
		}

		source.sendFeedback(() -> Text.literal("─────────────────────────────────────────────").formatted(Formatting.DARK_GRAY), false);

		source.sendFeedback(() -> Text.literal("Tip: Click coordinates to teleport!").formatted(Formatting.DARK_GRAY, Formatting.ITALIC), false);
	}
}
