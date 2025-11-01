package com.resourcefinder;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceFinderMod implements ModInitializer {
	public static final String MOD_ID = "resourcefinder";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("ResourceFinder mod initializing...");

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			ResourceFinderCommand.register(dispatcher);
		});

		LOGGER.info("ResourceFinder mod initialized successfully!");
	}
}
