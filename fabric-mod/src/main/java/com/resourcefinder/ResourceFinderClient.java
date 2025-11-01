package com.resourcefinder;

import com.resourcefinder.gui.ResourceFinderScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ResourceFinderClient implements ClientModInitializer {
	private static KeyBinding openGuiKey;
	private static KeyBinding clearNavigationKey;

	@Override
	public void onInitializeClient() {
		// Register keybinding (default: R key)
		openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.resourcefinder.open_gui",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_R,
			"category.resourcefinder"
		));

		// Register clear navigation keybinding (default: C key)
		clearNavigationKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.resourcefinder.clear_navigation",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_C,
			"category.resourcefinder"
		));

		// Register tick event to check for key press
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (openGuiKey.wasPressed()) {
				if (client.currentScreen == null) {
					client.setScreen(new ResourceFinderScreen(null));
				}
			}

			while (clearNavigationKey.wasPressed()) {
				NavigationHud.clear_target();
				if (client.player != null) {
					client.player.sendMessage(
						net.minecraft.text.Text.literal("Navigation cleared!")
							.formatted(net.minecraft.util.Formatting.GREEN),
						false
					);
				}
			}
		});

		// Register navigation HUD
		NavigationHud.register();

		// Register client commands
		ClientCommandRegistrationCallback.EVENT.register(ResourceFinderClientCommand::register);

		ResourceFinderMod.LOGGER.info("ResourceFinder Client initialized - Press R to open GUI, C to clear navigation");
	}
}
