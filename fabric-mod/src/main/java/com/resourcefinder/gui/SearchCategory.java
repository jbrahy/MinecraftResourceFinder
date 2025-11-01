package com.resourcefinder.gui;

public enum SearchCategory {
	BLOCKS("Blocks", "Search for any block type"),
	ENTITIES("Entities", "Search for mobs, animals, villagers"),
	PLAYERS("Players", "Search for other players"),
	STRUCTURES("Structures", "Search for villages, temples, etc.");

	public final String name;
	public final String description;

	SearchCategory(String name, String description) {
		this.name = name;
		this.description = description;
	}
}
