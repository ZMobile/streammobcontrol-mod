package com.blockafeller.ability;

import draylar.identity.ability.AbilityRegistry;
import draylar.identity.ability.IdentityAbility;
import draylar.identity.api.PlayerIdentity;
import draylar.identity.api.platform.IdentityConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;

public class MobAbilityStickHandler {

    /**
     * Sets the mob type in the ItemStack's NBT data.
     *
     * @param stack  The ItemStack instance of this item.
     */
    public static void setMobType(ItemStack stack, String id) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putString("MobType", id);
    }

    /**
     * Gets the current mob type stored in the ItemStack's NBT data.
     *
     * @param stack The ItemStack instance of this item.
     * @return The stored EntityType, or null if not set.
     */
    public static EntityType<? extends LivingEntity> getMobType(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt != null && nbt.contains("MobType")) {
            Identifier id = new Identifier(nbt.getString("MobType"));
            return (EntityType<? extends LivingEntity>) Registries.ENTITY_TYPE.get(id);
        }
        return null;
    }

    /**
     * Handles right-click interaction for a stick with the correct mob type stored in its NBT data.
     */
    public static void onRightClickWithAbilityStick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        if (!world.isClient) {
            // Check if the item is still on cooldown
            if (player.getItemCooldownManager().isCoolingDown(Items.STICK)) {
                player.sendMessage(Text.literal("The ability is still recharging!"), true);
                return;  // Exit early if the item is on cooldown
            }

            // Get the `EntityType` from the item's NBT data
            EntityType<? extends LivingEntity> mobType = getMobType(stack);

            if (mobType != null) {
                // Get the ability associated with the stored mob type from Identity's AbilityRegistry
                IdentityAbility<LivingEntity> ability = AbilityRegistry.get(mobType);

                if (ability != null) {
                    // Execute the ability
                    ability.onUse(player, PlayerIdentity.getIdentity(player), world);

                    // Retrieve the cooldown duration for this ability from IdentityConfig (or use default 20 ticks)
                    int cooldown = IdentityConfig.getInstance().getAbilityCooldownMap().getOrDefault(mobType.toString(), 20);

                    // Apply the cooldown
                    player.getItemCooldownManager().set(Items.STICK, cooldown);
                } else {
                    // If no ability is found for this mob type, notify the player
                    player.sendMessage(Text.of("No ability registered for mob: " + mobType.toString()), true);
                }
            } else {
                player.sendMessage(Text.of("Mob type is not set on this item."), true);
            }
        }
    }
}

