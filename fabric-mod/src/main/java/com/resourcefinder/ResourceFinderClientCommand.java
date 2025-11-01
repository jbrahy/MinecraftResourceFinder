package com.resourcefinder;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

public class ResourceFinderClientCommand {

	public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registry_access) {
		dispatcher.register(ClientCommandManager.literal("rfclient")
			.then(ClientCommandManager.literal("guide")
				.then(ClientCommandManager.argument("x", IntegerArgumentType.integer())
					.then(ClientCommandManager.argument("y", IntegerArgumentType.integer())
						.then(ClientCommandManager.argument("z", IntegerArgumentType.integer())
							.executes(context -> {
								int x = IntegerArgumentType.getInteger(context, "x");
								int y = IntegerArgumentType.getInteger(context, "y");
								int z = IntegerArgumentType.getInteger(context, "z");

								BlockPos target = new BlockPos(x, y, z);
								NavigationHud.set_target(target);

								// Also send server command for particles
								String server_command = String.format("rf guide %d %d %d", x, y, z);
								context.getSource().getClient().getNetworkHandler().sendChatCommand(server_command);

								context.getSource().sendFeedback(
									Text.literal("Navigation target set: ")
										.append(Text.literal(String.format("[%d, %d, %d]", x, y, z))
											.formatted(Formatting.AQUA))
								);

								return 1;
							})
							.then(ClientCommandManager.argument("name", StringArgumentType.greedyString())
								.executes(context -> {
									int x = IntegerArgumentType.getInteger(context, "x");
									int y = IntegerArgumentType.getInteger(context, "y");
									int z = IntegerArgumentType.getInteger(context, "z");
									String name = StringArgumentType.getString(context, "name");

									BlockPos target = new BlockPos(x, y, z);
									NavigationHud.set_target(target, name);

									// Also send server command for particles
									String server_command = String.format("rf guide %d %d %d", x, y, z);
									context.getSource().getClient().getNetworkHandler().sendChatCommand(server_command);

									context.getSource().sendFeedback(
										Text.literal("Navigation target set: ")
											.append(Text.literal(name).formatted(Formatting.YELLOW))
											.append(Text.literal(" at "))
											.append(Text.literal(String.format("[%d, %d, %d]", x, y, z))
												.formatted(Formatting.AQUA))
									);

									return 1;
								}))))))
			.then(ClientCommandManager.literal("clear")
				.executes(context -> {
					NavigationHud.clear_target();
					context.getSource().sendFeedback(
						Text.literal("Navigation cleared!").formatted(Formatting.GREEN)
					);
					return 1;
				})));
	}
}
