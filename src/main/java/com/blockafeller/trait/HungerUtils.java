package com.blockafeller.trait;

import net.minecraft.entity.player.PlayerEntity;

public class HungerUtils {

    /**
     * Sets the player's hunger level.
     *
     * @param player      The player entity whose hunger level to set.
     * @param foodLevel   The desired hunger level (0 to 20).
     * @param saturation  The desired saturation level.
     */
    public static void setPlayerHunger(PlayerEntity player, int foodLevel, float saturation) {
        // Set the player's food level (0 - 20)
        player.getHungerManager().setFoodLevel(foodLevel);

        // Set the player's saturation level
        player.getHungerManager().setSaturationLevel(saturation);
    }
}
