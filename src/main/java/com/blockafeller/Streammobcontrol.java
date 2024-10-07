package com.blockafeller;

import com.blockafeller.ability.AbilityPacketOverride;
import com.blockafeller.ability.AbilityListener;
import com.blockafeller.ability.CreeperFoodHandler;
import com.blockafeller.command.*;
import com.blockafeller.compass.TrackingCompassMod;
import com.blockafeller.config.ConfigManager;
import com.blockafeller.extension.PlayerExtension;
import com.blockafeller.inventory.DropPreventionHandler;
import com.blockafeller.morph.MorphEventHandler;
import com.blockafeller.morph.MorphService;
import com.blockafeller.time.GracePeriodTimeTracker;
import com.blockafeller.time.PlayerTimeBossBarTracker;
import com.blockafeller.time.PlayerTimeDataManager;
import com.blockafeller.time.PlayerTimeTracker;
import com.blockafeller.trait.damage.MobDamageManager;
import com.blockafeller.trait.hunger.MobHungerManager;
import com.blockafeller.trait.loot.CustomDeathDrops;
import com.blockafeller.trait.loot.ItemDropRemover;
import com.blockafeller.util.StreamerUtil;
import com.mojang.brigadier.ParseResults;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

public class Streammobcontrol implements ModInitializer {
	public static final String MOD_ID = "streammobcontrol";
	private static final String CUSTOM_LOBBY_DIR = "../lib/lobby"; // Navigate one level up to access `lib/lobby`
	// Path to the custom lobby world in the mod's lib folder
	private static final String TARGET_LOBBY_DIR = "lobby";
	private static final String SERVER_PROPERTIES_FILE = "server.properties";

	//public static final Item ABILITY_STICK = new MobAbilityStickItem();


	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			onPlayerRespawn(newPlayer);
		});
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayerEntity player = handler.getPlayer();
			onPlayerJoin(server, player);
		});
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			ServerPlayerEntity player = handler.getPlayer();
			if (((PlayerExtension) player).isInhabiting()) {
				MorphService.reverseMorph(player);
			}
		});
		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			ConfigManager.loadConfig();
		});

		// Register the event listener
		UseBlockCallback.EVENT.register((player, world, hand, blockHitResult) -> {
			if (world.isClient()) {
				return ActionResult.PASS;
			}

			// Check if the block is a door
			if (((PlayerExtension)player).isInhabiting()) {
				// Check if the player is in the disallowed list
					//player.sendMessage(new LiteralText("You are not allowed to open this door!"), true);
				return ActionResult.FAIL;
			}

			return ActionResult.PASS;
		});
		/*updateServerPropertiesEarly();

		ServerLifecycleEvents.SERVER_STARTING.register(this::setupCustomLobbyWorld);*/
		ServerLifecycleEvents.SERVER_STARTED.register(this::setupCustomLobbyWorld);

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			ControlCommand.register(dispatcher, registryAccess);
			TimeCommands.register(dispatcher);
			StreamerCommands.register(dispatcher);

			RunCommand.register(dispatcher);

			KickCycleCommand.register(dispatcher);
			GracePeriodCommand.register(dispatcher);
		});
		//ServerLifecycleEvents.SERVER_STARTING.register(this::setupCustomLobbyWorld);
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		DropPreventionHandler.register();
		AbilityListener.register();
		//CreeperFoodExplosion.register();
		CreeperFoodHandler.register();
		MobHungerManager.register();
		//PotionHandler.registerPotionHandler();
		CustomDeathDrops.register();
		MorphEventHandler.register();
		MobDamageManager.register();

		PlayerTimeDataManager.register();
		PlayerTimeTracker.register();
		PlayerTimeBossBarTracker.register();
		GracePeriodTimeTracker.register();

		AbilityPacketOverride.register();
		ItemDropRemover.register();

		TrackingCompassMod.register();
		LOGGER.info("Hello Fabric world!");
	}

	// Event handler method
	private void onPlayerRespawn(ServerPlayerEntity player) {
		if (!StreamerUtil.isStreamer(player)) {
			System.out.println("Teleporting to lobby 2");
			//MorphService.returnPlayerToLobby(player);
			if (ConfigManager.getConfig().isKickCycle()) {
				player.networkHandler.disconnect(Text.literal("Kick on death is assigned in order to cycle players. Please rejoin to try again."));
			} else {
				MorphService.returnPlayerToLobby(player.getServer(), player);
			}
		} else if (((PlayerExtension) player).isInhabiting()) {
			MorphService.removeMorphAttributes(player);
		}
	}

	// Event handler method
	private void onPlayerJoin(MinecraftServer server, ServerPlayerEntity player) {

		if (!StreamerUtil.isStreamer(player)) {
			System.out.println("Teleporting to lobby 2");
			MorphService.returnPlayerToLobby(server, player);
		}
	}


	/**
	 * Sets up the custom lobby world if it doesn't already exist in the server's root directory.
	 */
	private void setupCustomLobbyWorld(MinecraftServer server) {
		//HeadlessCreateCommand.run(server, new String[]{"create", "stream:lobby", "NORMAL"});
		//HeadlessTpCommand.run(server, "stream:lobby", new String[]{"gamerule", "fallDamage", "false"});
		//HeadlessGameruleCommand.run(server, "stream:lobby", new String[]{"gamerule", "fallDamage", "false"});
		//HeadlessGameruleCommand.run(server, "stream:lobby", new String[]{"gamerule", "doMobSpawning", "false"});
		//HeadlessGameruleCommand.run(server, "stream:lobby", new String[]{"gamerule", "doDaylightCycle", "false"});
		/*// Get the path to the server's root directory
		String gradleProjectRoot = System.getProperty("user.dir");
		File gradleProjectRootParent = new File(gradleProjectRoot).getParentFile();
		File customLobbyWorld = new File(gradleProjectRootParent, "lib/lobby");
		File targetLobbyWorldDir = new File(gradleProjectRoot, TARGET_LOBBY_DIR);

		// Debugging output to verify paths
		System.out.println("Lobby location: " + customLobbyWorld.getAbsolutePath());
		System.out.println("Lobby target: " + targetLobbyWorldDir.getAbsolutePath());
		System.out.println("Expected custom Lobby world source directory: " + customLobbyWorld.getAbsolutePath());

		// Check if the custom world folder already exists in the root directory
		if (targetLobbyWorldDir.exists()) {
			//Delete it
			try {
				Files.walk(targetLobbyWorldDir.toPath())
						.sorted(java.util.Comparator.reverseOrder())
						.map(Path::toFile)
						.forEach(File::delete);
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Failed to delete existing Lobby world: " + e.getMessage());
			}
		}
			System.out.println("Target Lobby world directory not found: " + targetLobbyWorldDir.getAbsolutePath());
			System.out.println("Attempting to copy from custom world directory...");

			// If the source directory exists, copy it to the root directory
			if (customLobbyWorld.exists() && customLobbyWorld.isDirectory()) {
				try {
					System.out.println("Copying custom Lobby world from " + customLobbyWorld.getAbsolutePath() + " to " + targetLobbyWorldDir.getAbsolutePath());
					copyFolder(customLobbyWorld.toPath(), targetLobbyWorldDir.toPath());
					System.out.println("Successfully copied custom Lobby world!");
				} catch (IOException e) {
					e.printStackTrace();
					System.err.println("Failed to copy custom Lobby world: " + e.getMessage());
				}
			} else {
				System.err.println("Custom Lobby world directory not found or not a directory: " + customLobbyWorld.getAbsolutePath());
			}
		//} else {
			//System.out.println("Lobby world already exists in the server root directory, skipping copy.");
		//}*/
	}

	/**
	 * Recursively copy a folder and its contents from source to destination.
	 */
	private void copyFolder(Path source, Path destination) throws IOException {
		Files.walk(source).forEach(sourcePath -> {
			try {
				Path targetPath = destination.resolve(source.relativize(sourcePath));
				Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Copied: " + sourcePath + " to " + targetPath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	private void updateServerPropertiesEarly() {
		File propertiesFile = new File(SERVER_PROPERTIES_FILE);
		Properties properties = new Properties();

		// Load the server.properties file if it exists
		if (propertiesFile.exists()) {
			try (InputStream input = new FileInputStream(propertiesFile)) {
				properties.load(input);
			} catch (IOException e) {
				System.err.println("Failed to load server.properties: " + e.getMessage());
				return;
			}
		}

		// Set the "level-name" property to "lobby" if not already set
		String currentLevelName = properties.getProperty("level-name");
		if (!"lobby".equals(currentLevelName)) {
			properties.setProperty("level-name", "lobby");

			// Save the modified properties back to server.properties
			try (OutputStream output = new FileOutputStream(propertiesFile)) {
				properties.store(output, "Modified by StreamerMod to set the default level-name to lobby");
				System.out.println("Updated server.properties to set level-name=lobby");
			} catch (IOException e) {
				System.err.println("Failed to save server.properties: " + e.getMessage());
			}
		}
	}

	private void setWorldSpawns(MinecraftServer server, BlockPos spawnPos) {
		// Run the /setworldspawn command for the Overworld
		executeCommand(server, String.format("setworldspawn %d %d %d", spawnPos.getX(), spawnPos.getY(), spawnPos.getZ()));

		// Optionally, run the same command for a custom world (like "stream:lobby")
		//executeCommand(server, String.format("execute in minecraft:overworld run setworldspawn %d %d %d", spawnPos.getX(), spawnPos.getY(), spawnPos.getZ()));

		System.out.println("Set spawn positions using /setworldspawn command.");
	}

	/**
	 * Helper method to execute a command as the server.
	 */
	private void executeCommand(MinecraftServer server, String command) {
		// Create a command source as the server itself
		ServerCommandSource source = server.getCommandSource();

		// Parse the command string into ParseResults
		ParseResults<ServerCommandSource> parseResults = server.getCommandManager().getDispatcher().parse(command, source);

		// Execute the command using the parsed result
		server.getCommandManager().execute(parseResults, command);
	}

}