package com.blockafeller.ability;

import com.blockafeller.extension.PlayerExtension;
import net.minecraft.server.network.ServerPlayerEntity;

public class MorphFlightManager {
    public static void conditionallyGiveMorphedPlayerFlightAbilities(ServerPlayerEntity serverPlayer) {
        if (((PlayerExtension) serverPlayer).isInhabiting()
        && ((PlayerExtension) serverPlayer).getInhabitedMobType().toString().equals("minecraft:bat")
        || ((PlayerExtension) serverPlayer).getInhabitedMobType().toString().equals("minecraft:phantom")
        || ((PlayerExtension) serverPlayer).getInhabitedMobType().toString().equals("minecraft:ender_dragon")
        || ((PlayerExtension) serverPlayer).getInhabitedMobType().toString().equals("minecraft:blaze")
        || ((PlayerExtension) serverPlayer).getInhabitedMobType().toString().equals("minecraft:allay")
        || ((PlayerExtension) serverPlayer).getInhabitedMobType().toString().equals("minecraft:vex")
        || ((PlayerExtension) serverPlayer).getInhabitedMobType().toString().equals("minecraft:bee")
        || ((PlayerExtension) serverPlayer).getInhabitedMobType().toString().equals("minecraft:ghast")
        || ((PlayerExtension) serverPlayer).getInhabitedMobType().toString().equals("minecraft:parrot")
        || ((PlayerExtension) serverPlayer).getInhabitedMobType().toString().equals("minecraft:wither")) {
            serverPlayer.getAbilities().allowFlying = true;
            serverPlayer.getAbilities().flying = true;

            serverPlayer.sendAbilitiesUpdate();
        }
    }
}
