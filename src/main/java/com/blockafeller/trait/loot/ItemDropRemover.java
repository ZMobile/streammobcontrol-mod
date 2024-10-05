package com.blockafeller.trait.loot;

import com.blockafeller.morph.MorphUtil;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.ItemEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public class ItemDropRemover {

    public static void register() {
        // Listen for new entities being loaded into the world
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            // Check if the entity is an ItemEntity and belongs to a server world
            if (entity instanceof ItemEntity itemEntity && world instanceof ServerWorld) {
                // Check if the item is a clock and its name starts with "Leave Morph"
                if (itemEntity.getStack().getItem().toString().contains("clock")) {
                    if (MorphUtil.isReverseMorphKey(itemEntity.getStack())) {
                        // Remove the item entity from the world
                        itemEntity.discard(); // Safely remove the item from the world
                    }
                } else if (itemEntity.getStack().getItem().toString().contains("stick")) {
                    if (MorphUtil.isAbilityStick(itemEntity.getStack())) {
                        // Remove the item entity from the world
                        itemEntity.discard(); // Safely remove the item from the world
                    }
                } else if (itemEntity.getStack().getItem().toString().contains("compass")) {
                    if (MorphUtil.isSpectateKey(itemEntity.getStack())) {
                        // Remove the item entity from the world
                        itemEntity.discard(); // Safely remove the item from the world
                    }
                }
            }
        });
    }
}
