package com.resourcefinder.gui;

import com.resourcefinder.BlockLocation;
import com.resourcefinder.NavigationHud;
import com.resourcefinder.WorldScanner;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class ResourceFinderScreen extends Screen {
	private final Screen parent;
	private TextFieldWidget searchField;
	private SearchCategory currentCategory = SearchCategory.BLOCKS;
	private List<SearchResult> results = new ArrayList<>();
	private int scrollOffset = 0;

	private static final int RESULT_HEIGHT = 30;
	private static final int RESULTS_PER_PAGE = 10;

	public ResourceFinderScreen(Screen parent) {
		super(Text.literal("Resource Finder"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		super.init();

		int centerX = this.width / 2;

		// Search field at top
		this.searchField = new TextFieldWidget(
			this.textRenderer,
			centerX - 150,
			30,
			300,
			20,
			Text.literal("Search...")
		);
		this.searchField.setMaxLength(50);
		this.searchField.setPlaceholder(Text.literal("Type to search...").formatted(Formatting.GRAY));
		this.searchField.setChangedListener(this::onSearchChanged);
		this.addSelectableChild(this.searchField);

		// Category buttons
		int buttonY = 60;
		int buttonWidth = 70;
		int spacing = 5;
		int startX = centerX - (SearchCategory.values().length * (buttonWidth + spacing)) / 2;

		for (int i = 0; i < SearchCategory.values().length; i++) {
			SearchCategory category = SearchCategory.values()[i];
			int buttonX = startX + i * (buttonWidth + spacing);

			this.addDrawableChild(ButtonWidget.builder(
				Text.literal(category.name),
				button -> setCategory(category)
			).dimensions(buttonX, buttonY, buttonWidth, 20).build());
		}

		// Close button
		this.addDrawableChild(ButtonWidget.builder(
			Text.literal("Close"),
			button -> this.close()
		).dimensions(centerX - 50, this.height - 30, 100, 20).build());

		// Quick search buttons for common items
		addQuickSearchButtons();
	}

	private void addQuickSearchButtons() {
		if (currentCategory != SearchCategory.BLOCKS) return;

		int centerX = this.width / 2;
		int startY = 95;
		int buttonSize = 30;
		int spacing = 5;

		String[] commonBlocks = {
			"diamond_ore", "ancient_debris", "emerald_ore",
			"iron_ore", "gold_ore", "coal_ore"
		};

		for (int i = 0; i < commonBlocks.length; i++) {
			final String blockId = commonBlocks[i];
			int x = centerX - (commonBlocks.length * (buttonSize + spacing)) / 2 + i * (buttonSize + spacing);

			this.addDrawableChild(ButtonWidget.builder(
				Text.literal(""),
				button -> {
					searchField.setText(blockId.replace("_ore", ""));
					performSearch();
				}
			).dimensions(x, startY, buttonSize, buttonSize).build());
		}
	}

	private void setCategory(SearchCategory category) {
		this.currentCategory = category;
		this.clearChildren();
		this.init();
		performSearch();
	}

	private void onSearchChanged(String query) {
		if (query.length() >= 2) {
			performSearch();
		} else if (query.isEmpty()) {
			results.clear();
		}
	}

	private void performSearch() {
		results.clear();
		String query = searchField.getText().toLowerCase();

		if (query.isEmpty()) return;

		if (client == null || client.player == null) return;

		PlayerEntity player = client.player;
		BlockPos playerPos = player.getBlockPos();

		switch (currentCategory) {
			case BLOCKS -> searchBlocks(query, playerPos);
			case ENTITIES -> searchEntities(query, playerPos);
			case PLAYERS -> searchPlayers(query, playerPos);
			case STRUCTURES -> searchStructures(query, playerPos);
		}
	}

	private void searchBlocks(String query, BlockPos playerPos) {
		if (client.world == null) return;

		// Search through registered blocks
		Registries.BLOCK.getIds().forEach(id -> {
			String blockName = id.getPath();
			if (blockName.contains(query)) {
				// For now, just add as potential results
				// Actual scanning happens when user clicks
				results.add(new SearchResult(
					blockName,
					"Block",
					playerPos,
					0,
					"Unknown",
					"overworld"
				));
			}
		});

		// Limit results
		if (results.size() > 50) {
			results = results.subList(0, 50);
		}
	}

	private void searchEntities(String query, BlockPos playerPos) {
		if (client.world == null) return;

		// Search for entities in loaded chunks
		for (Entity entity : client.world.getEntities()) {
			String entityName = entity.getType().getName().getString().toLowerCase();
			if (entityName.contains(query)) {
				BlockPos entityPos = entity.getBlockPos();
				double distance = BlockLocation.calculateHorizontalDistance(playerPos, entityPos);
				String direction = BlockLocation.calculateDirection(playerPos, entityPos);

				results.add(new SearchResult(
					entity.getType().getName().getString(),
					"Entity",
					entityPos,
					distance,
					direction,
					"current"
				));
			}
		}
	}

	private void searchPlayers(String query, BlockPos playerPos) {
		if (client.world == null) return;

		// Search for players
		for (PlayerEntity player : client.world.getPlayers()) {
			if (player == client.player) continue; // Skip self

			String playerName = player.getName().getString().toLowerCase();
			if (playerName.contains(query)) {
				BlockPos targetPos = player.getBlockPos();
				double distance = BlockLocation.calculateHorizontalDistance(playerPos, targetPos);
				String direction = BlockLocation.calculateDirection(playerPos, targetPos);

				results.add(new SearchResult(
					player.getName().getString(),
					"Player",
					targetPos,
					distance,
					direction,
					"current"
				));
			}
		}
	}

	private void searchStructures(String query, BlockPos playerPos) {
		// Structure search would require more complex implementation
		// Could use structure locating commands or NBT data
		results.add(new SearchResult(
			"Structure search coming soon",
			"Info",
			playerPos,
			0,
			"",
			"current"
		));
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackground(context, mouseX, mouseY, delta);
		super.render(context, mouseX, mouseY, delta);

		// Draw title
		context.drawCenteredTextWithShadow(
			this.textRenderer,
			this.title,
			this.width / 2,
			10,
			0xFFFFFF
		);

		// Draw search field
		this.searchField.render(context, mouseX, mouseY, delta);

		// Draw category description
		String desc = currentCategory.description;
		context.drawText(
			this.textRenderer,
			Text.literal(desc).formatted(Formatting.GRAY),
			this.width / 2 - this.textRenderer.getWidth(desc) / 2,
			85,
			0xAAAAAA,
			false
		);

		// Draw results
		renderResults(context, mouseX, mouseY);
	}

	private void renderResults(DrawContext context, int mouseX, int mouseY) {
		int startY = 140;
		int resultWidth = 400;
		int centerX = this.width / 2;

		if (results.isEmpty() && !searchField.getText().isEmpty()) {
			context.drawCenteredTextWithShadow(
				this.textRenderer,
				Text.literal("No results found").formatted(Formatting.GRAY),
				centerX,
				startY + 50,
				0xAAAAAA
			);
			return;
		}

		// Draw results header
		if (!results.isEmpty()) {
			context.drawText(
				this.textRenderer,
				Text.literal(results.size() + " results found").formatted(Formatting.YELLOW),
				centerX - resultWidth / 2,
				startY - 15,
				0xFFFFFF,
				false
			);
		}

		// Draw each result
		int displayCount = Math.min(results.size(), RESULTS_PER_PAGE);
		for (int i = 0; i < displayCount; i++) {
			int index = i + scrollOffset;
			if (index >= results.size()) break;

			SearchResult result = results.get(index);
			int y = startY + i * RESULT_HEIGHT;

			renderResult(context, result, centerX - resultWidth / 2, y, resultWidth, mouseX, mouseY);
		}
	}

	private void renderResult(DrawContext context, SearchResult result, int x, int y, int width, int mouseX, int mouseY) {
		boolean hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + RESULT_HEIGHT - 5;

		// Background
		int bgColor = hovered ? 0x80555555 : 0x80333333;
		context.fill(x, y, x + width, y + RESULT_HEIGHT - 5, bgColor);

		// Name
		context.drawText(
			this.textRenderer,
			Text.literal(result.name).formatted(Formatting.WHITE, Formatting.BOLD),
			x + 5,
			y + 3,
			0xFFFFFF,
			false
		);

		// Type
		context.drawText(
			this.textRenderer,
			Text.literal(result.type).formatted(Formatting.GRAY),
			x + 5,
			y + 15,
			0xAAAAAA,
			false
		);

		// Distance and direction (if not a search suggestion)
		if (result.distance > 0) {
			String distText = String.format("%.0fm %s", result.distance, result.direction);
			context.drawText(
				this.textRenderer,
				Text.literal(distText).formatted(Formatting.YELLOW),
				x + width - this.textRenderer.getWidth(distText) - 5,
				y + 9,
				0xFFFF00,
				false
			);
		}

		// Coordinates
		String coords = String.format("[%d, %d, %d]", result.position.getX(), result.position.getY(), result.position.getZ());
		context.drawText(
			this.textRenderer,
			Text.literal(coords).formatted(Formatting.AQUA),
			x + 100,
			y + 15,
			0x00FFFF,
			false
		);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		// Handle clicking on results
		int startY = 140;
		int resultWidth = 400;
		int centerX = this.width / 2;
		int x = centerX - resultWidth / 2;

		int displayCount = Math.min(results.size(), RESULTS_PER_PAGE);
		for (int i = 0; i < displayCount; i++) {
			int index = i + scrollOffset;
			if (index >= results.size()) break;

			int y = startY + i * RESULT_HEIGHT;

			if (mouseX >= x && mouseX <= x + resultWidth && mouseY >= y && mouseY <= y + RESULT_HEIGHT - 5) {
				SearchResult result = results.get(index);
				handleResultClick(result);
				return true;
			}
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	private void handleResultClick(SearchResult result) {
		if (client == null || client.player == null) return;

		// For blocks, trigger actual search
		if (result.type.equals("Block") && result.distance == 0) {
			// This is a suggestion, perform actual block search
			if (client.player != null) {
				String command = String.format("/rf %s", result.name);
				client.player.networkHandler.sendChatCommand(command.substring(1));
				this.close();
			}
		} else {
			// Set navigation target on client side
			NavigationHud.set_target(result.position, result.name);

			// Also send command to server for particles
			String command = String.format("/rf guide %d %d %d",
				result.position.getX(),
				result.position.getY(),
				result.position.getZ());
			if (client.player != null) {
				client.player.networkHandler.sendChatCommand(command.substring(1));
				this.close();
			}
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (this.searchField.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		if (results.size() > RESULTS_PER_PAGE) {
			scrollOffset = Math.max(0, Math.min(
				results.size() - RESULTS_PER_PAGE,
				scrollOffset - (int)verticalAmount
			));
			return true;
		}
		return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
	}

	@Override
	public void close() {
		if (client != null) {
			client.setScreen(parent);
		}
	}

	@Override
	public boolean shouldPause() {
		return false;
	}
}
