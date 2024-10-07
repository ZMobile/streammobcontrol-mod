package com.blockafeller.ability;

import com.blockafeller.extension.PlayerExtension;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;

public class CreeperFoodHandler {

    public static void register() {
        // Register the food usage event listener to handle eating interactions
        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getStackInHand(hand);

            // Check if the player is eating a pufferfish with custom NBT and they are a creeper
            if (isCreeper(player) && isCreeperFood(stack)) {
                // Trigger the creeper hissing sound at the start of eating
                if (hand == Hand.MAIN_HAND) {
                    System.out.println("Creeper hiss sound");
                    world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_CREEPER_PRIMED, player.getSoundCategory(), 3.0f, 1.0f);
                    player.playSound(SoundEvents.ENTITY_GENERIC_EAT, 0.0f, 0.0f);
                }
            }
            return TypedActionResult.pass(stack);
        });

        // Register server tick events to manage the player's hunger when they are a creeper
        ServerTickEvents.END_SERVER_TICK.register(CreeperFoodHandler::manageCreeperHunger);
    }

    /**
     * Check if the player is currently a creeper.
     */
    public static boolean isCreeper(PlayerEntity player) {
        // Use the Identity mod or your custom check to see if the player is morphed as a creeper
        if (!((PlayerExtension) player).isInhabiting()) {
            return false;
        }
        return ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:creeper");
    }

    /**
     * Check if the item is a special pufferfish with the NBT tag `CustomFood: "creeper_food"`.
     */
    public static boolean isCreeperFood(ItemStack stack) {
        if (stack.getItem() == Items.PUFFERFISH) {
            NbtCompound nbt = stack.getNbt();
            return nbt != null && "creeper_food".equals(nbt.getString("CustomFood"));
        }
        return false;
    }

    /**
     * Manage the player's hunger level when they are a creeper.
     */
    private static void manageCreeperHunger(MinecraftServer server) {
        server.getPlayerManager().getPlayerList().forEach(player -> {
            if (isCreeper(player)) {
                // Keep the hunger level constant at 18
                if (player.getHungerManager().getFoodLevel() < 10) {
                    player.getHungerManager().setFoodLevel(10);
                }
            }
        });
    }

    /**
     * Add custom NBT data to the pufferfish item to mark it as "creeper food".
     */
    public static void addCreeperFoodTag(ItemStack stack) {
        if (stack.getItem() == Items.PUFFERFISH) {
            NbtCompound nbt = stack.getOrCreateNbt();
            nbt.putString("CustomFood", "creeper_food");
        }
    }
}