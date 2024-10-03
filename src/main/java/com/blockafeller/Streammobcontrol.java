package com.blockafeller;

import com.blockafeller.ability.AbilityStickListener;
import com.blockafeller.ability.CreeperFoodExplosion;
import com.blockafeller.ability.CreeperFoodHandler;
import com.blockafeller.ability.PotionHandler;
import com.blockafeller.command.ControlCommand;
import com.blockafeller.inventory.DropPreventionHandler;
import com.blockafeller.trait.MobHungerManager;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Streammobcontrol implements ModInitializer {
	public static final String MOD_ID = "streammobcontrol";

	//public static final Item ABILITY_STICK = new MobAbilityStickItem();


	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
					ControlCommand.register(dispatcher, registryAccess);
				});
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		DropPreventionHandler.registerDropPrevention();
		AbilityStickListener.registerAbilityStickListener();
		//CreeperFoodExplosion.register();
		CreeperFoodHandler.register();
		MobHungerManager.register();
		//PotionHandler.registerPotionHandler();
		LOGGER.info("Hello Fabric world!");
	}
}