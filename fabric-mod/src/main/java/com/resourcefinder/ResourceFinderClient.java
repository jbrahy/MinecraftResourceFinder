package com.resourcefinder;

import com.resourcefinder.gui.ResourceFinderScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ResourceFinderClient implements ClientModInitializer {
	private static KeyBinding openGuiKey;

	@Override
	public void onInitializeClient() {
		// Register keybinding (default: R key)
		openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.resourcefinder.open_gui",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_R,
			"category.resourcefinder"
		));

		// Register tick event to check for key press
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (openGuiKey.wasPressed()) {
				if (client.currentScreen == null) {
					client.setScreen(new ResourceFinderScreen(null));
				}
			}
		});

		ResourceFinderMod.LOGGER.info("ResourceFinder Client initialized - Press R to open GUI");
	}
}
