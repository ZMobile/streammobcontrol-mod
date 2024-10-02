package com.blockafeller.constraints;


import com.blockafeller.ability.CreeperFoodHandler;
import com.blockafeller.ability.MobAbilityStickHandler;
import com.blockafeller.extension.PlayerExtension;
import com.blockafeller.trait.HungerUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class InventoryFiller {
    private static final Identifier SKELETON_ID = new Identifier("minecraft", "skeleton");
    private static final Identifier STRAY_ID = new Identifier("minecraft", "stray");
    private static final Identifier PILLAGER_ID = new Identifier("minecraft", "pillager");
    private static final Identifier PIGLIN_ID = new Identifier("minecraft", "piglin");


    public static void fillInventoryWithPapers(ServerPlayerEntity player) {
        // Iterate over each slot in the player's inventory
        for (int i = 0; i < player.getInventory().size(); i++) {
            if (((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:zombie")
                    || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:zombified_piglin")
                    || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:evoker")
                    || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:vex")
                    || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:drowned")
                    || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:zombie_villager")
                    || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:husk")
                    || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:skeleton")
                    || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:wither_skeleton")
                    || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:stray")
                    || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:piglin")
                    || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:piglin_brute")
                    || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:allay")
                    || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:fox")
                    || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:pillager")
                    || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:vindicator")
                    // Ability holders:
                    || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:blaze")
                    || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:creeper")
                    || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:enderman")
                    || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:ender_dragon")
                    || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:ghast")
                    || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:snow_golem")
                    || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:wither")
                    || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:endermite")
                    || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:llama")
                    || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:trader_llama")
                    || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:witch")
                    || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:evoker")
                    || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:warden")
            ) {
                if (i == 0) {
                    if (((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:blaze")
                            || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:enderman")
                            || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:ender_dragon")
                            || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:ghast")
                            || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:snow_golem")
                            || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:wither")
                            || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:endermite")
                            || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:llama")
                            || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:trader_llama")
                            || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:witch")
                            || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:evoker")
                            || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:warden")) {
                        ItemStack itemStack = new ItemStack(Items.STICK);

                        // Step 2: Get the `EntityType` from the string ID
                        MobAbilityStickHandler.setMobType(itemStack, ((PlayerExtension) player).getInhabitedMobType().toString());

                        // Step 4: Set a custom name for the item (display name)
                        itemStack.setCustomName(Text.literal("Use Ability (Right Click)"));

// Step 6: Replace the player’s inventory slot with the new custom item stack
                        player.getInventory().setStack(i, itemStack);
                    } else if (((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:creeper")) {
                        HungerUtils.setPlayerHunger(player, 19, 0f);
                        ItemStack itemStack = new ItemStack(Items.PUFFERFISH);

                        CreeperFoodHandler.addCreeperFoodTag(itemStack);

                        // Step 4: Set a custom name for the item (display name)
                        itemStack.setCustomName(Text.literal("Explode (Eat Food)"));

// Step 6: Replace the player’s inventory slot with the new custom item stack
                        player.getInventory().setStack(i, itemStack);
                    }
                    continue;
                }
            }
            ItemStack item;
            // Skip hotbar slots (0-8) and the offhand slot (40)
            if (i == 40) {
                continue;
            }

            // Create a custom-named paper item
            item = new ItemStack(Items.PAPER);
            if (i >= 36 && i <= 39) {
                if (((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:zombie")
                        || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:drowned")
                        || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:zombie_villager")
                        || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:husk")
                        || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:skeleton")
                        || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:stray")
                        || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:piglin")) {
                    item = new ItemStack(Items.AIR);
                }
            }
            if (i == 27) {
                System.out.println("Slot 27");
                System.out.println("Inhabiting mob type: " + ((PlayerExtension) player).getInhabitedMobType());
                //If the mob is a skeleton, stray, pillager, or piglin, sets slot 27 to 64 arrows instead of paper
                if (((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:skeleton")
                        || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:stray")
                        || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:pillager")
                        || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:piglin")) {
                    item = new ItemStack(Items.ARROW);
                    item.setCount(64);
                }
            }
            item.setCustomName(Text.literal("Do not interact - Slot " + i));

            // Replace the inventory slot with the custom-named paper
            player.getInventory().setStack(i, item);
        }

        // Force the player to select slot 0 (first hotbar slot) as the active slot
        player.getInventory().selectedSlot = 0;
    }
}
