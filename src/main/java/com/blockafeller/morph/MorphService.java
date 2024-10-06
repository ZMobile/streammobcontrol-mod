package com.blockafeller.morph;

import com.blockafeller.ability.MorphFlightManager;
import com.blockafeller.extension.PlayerExtension;
import com.blockafeller.inventory.InventoryFiller;
import com.blockafeller.time.PlayerTimeData;
import com.blockafeller.time.PlayerTimeDataManager;
import com.blockafeller.trait.hunger.HungerUtils;
import draylar.identity.api.PlayerIdentity;
import draylar.identity.api.variant.IdentityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

public class MorphService {
    public static void morphPlayerToMob(ServerPlayerEntity player, MobEntity targetMob, Identifier mobId) {
        // Step 1: Save player’s inventory and teleport to mob’s position
        player.teleport(targetMob.getX(), targetMob.getY(), targetMob.getZ());

        // Step 2: Match player’s facing direction to the mob
        player.setYaw(targetMob.getYaw());
        player.setPitch(targetMob.getPitch());

        // Step 3: Equip the player with the mob’s items
        player.getInventory().clear(); // Clear player’s inventory
        player.getInventory().setStack(0, targetMob.getMainHandStack());
        player.getInventory().setStack(40, targetMob.getOffHandStack()); // Off-hand
        player.getInventory().armor.set(0, targetMob.getEquippedStack(EquipmentSlot.HEAD));
        player.getInventory().armor.set(1, targetMob.getEquippedStack(EquipmentSlot.CHEST));
        player.getInventory().armor.set(2, targetMob.getEquippedStack(EquipmentSlot.LEGS));
        player.getInventory().armor.set(3, targetMob.getEquippedStack(EquipmentSlot.FEET));


        // Step 4: Set morph status and game mode
        ((PlayerExtension) player).setInhabiting(false);
        ((PlayerExtension) player).setInhabitedMobType(mobId);
        HungerUtils.setPlayerHunger(player, 10, 0f);

        InventoryFiller.fillInventoryWithPapers(player);
        ((PlayerExtension) player).setInhabiting(true);
        System.out.println("Mob id: " + mobId);
        morphPlayerToMob(player, mobId);
        player.changeGameMode(GameMode.ADVENTURE);
        player.setHealth(targetMob.getHealth());
        MorphFlightManager.conditionallyGiveMorphedPlayerFlightAbilities(player);

        // Step 5: Despawn the target mob
        targetMob.remove(Entity.RemovalReason.DISCARDED);
    }

    public static void reverseMorph(ServerPlayerEntity player) {
        // Step 1: Create a new mob at the player’s location with the same attributes
        Identifier mobId = ((PlayerExtension) player).getInhabitedMobType();
        MobEntity newMob = (MobEntity) Registries.ENTITY_TYPE.get(mobId).create(player.getWorld());

        if (newMob != null) {
            newMob.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());

            // Step 2: Transfer player’s equipment to the mob
            newMob.equipStack(EquipmentSlot.MAINHAND, player.getInventory().getStack(0));
            newMob.equipStack(EquipmentSlot.OFFHAND, player.getInventory().getStack(40));
            newMob.equipStack(EquipmentSlot.HEAD, player.getInventory().armor.get(0));
            newMob.equipStack(EquipmentSlot.CHEST, player.getInventory().armor.get(1));
            newMob.equipStack(EquipmentSlot.LEGS, player.getInventory().armor.get(2));
            newMob.equipStack(EquipmentSlot.FEET, player.getInventory().armor.get(3));
            newMob.setHealth(player.getHealth());
            // Step 3: Spawn the mob in the world
            player.getWorld().spawnEntity(newMob);
        }
        removeMorphAttributes(player);
    }

    public static void removeMorphAttributes(ServerPlayerEntity player) {
        ServerCommandSource source = player.getServer().getCommandSource().withLevel(4).withSilent();
        // Execute the command
        String command = "/identity unequip " + player.getEntityName();

        player.getServer().getCommandManager().executeWithPrefix(source, command);
        player.setMovementSpeed(43.556f);
        //PlayerIdentity.updateIdentity(player, null, null);
        // Step 2: Clear the player’s inventory
        // Step 4: Clear player inventory and reset morph status
        player.getInventory().clear();
        ((PlayerExtension) player).setInhabitedMobType(null);
        ((PlayerExtension) player).setInhabiting(false);

        // Step 5: Set player to Spectator mode
        player.changeGameMode(GameMode.SPECTATOR);
    }
    // Other methods remain unchanged...

    private static void morphPlayerToMob(ServerPlayerEntity player, Identifier mobId) {
        System.out.println("Morphing player to mob: " + mobId);
        // Retrieve the EntityType based on the Identifier
        EntityType<?> entityType = Registries.ENTITY_TYPE.get(mobId);

        // Create an instance of the mob
        Entity createdEntity = entityType.create(player.getWorld());

        // Check if the created entity is a LivingEntity (which is needed for identity)
        if (createdEntity instanceof LivingEntity livingEntity) {
            // Convert the LivingEntity to IdentityType
            IdentityType<LivingEntity> identityType = new IdentityType<>(livingEntity);

            // Update the player's identity using the Identity mod's method
            PlayerIdentity.updateIdentity(player, identityType, livingEntity);
        }
    }

    public static void beginSpectating(ServerPlayerEntity player) {
        System.out.println("Beginning spectating for player: " + player.getEntityName());
        String playerName = player.getEntityName();
        player.changeGameMode(GameMode.SPECTATOR);
        //TpCommand.run(player.getServer(), player, new String[]{playerName, "minecarft:overworld"});
        // Step 2: Clear the player’s inventory
        player.getInventory().clear();
    }

    public static void returnPlayerToLobby(MinecraftServer server, ServerPlayerEntity player) {
        System.out.println("Returning player to lobby: " + player.getEntityName());
        // Step 1: Teleport the player to the lobby+
        //player.teleport(0, 100, 0);
        player.changeGameMode(GameMode.SPECTATOR);
        PlayerTimeData  timeManager = PlayerTimeDataManager.getOrCreatePlayerTimeData(player.getUuid(), server);
        timeManager.setSpectatorTime(180);
        timeManager.setTotalSpectatorTime(180);
        String playerName = player.getEntityName();
        //String command = String.format("mw tp %s stream:lobby", playerName);

        //TpCommand.run(player.getServer(), player, new String[]{playerName, "stream:lobby"});
        ((PlayerExtension) player).setInhabiting(false);
        ((PlayerExtension) player).setInhabitedMobType(null);
        ServerCommandSource source = server.getCommandSource().withLevel(4).withSilent();

        // Execute the command
        String command = "/identity unequip " + playerName;

        server.getCommandManager().executeWithPrefix(source, command);
        //PlayerIdentity.updateIdentity(player, null, null);
        // Step 2: Clear the player’s inventory
        player.getInventory().clear();

        // Step 3: Reset the player’s game mode
        player.changeGameMode(GameMode.SPECTATOR);

        //player.getInventory().setStack(0, MorphUtil.createSpectateKey());
        // Step 4: Reset the player’s health
        player.setHealth(20);
    }
}
