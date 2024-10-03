package com.blockafeller.inventory;

import com.blockafeller.extension.PlayerExtension;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

public class DropPreventionHandler {
    public static void registerDropPrevention() {
        PlayerEvent.DROP_ITEM.register((player, item) -> {
            ItemStack stack = item.getStack();
            if ((stack.getItem() == Items.PAPER || stack.getItem() == Items.ARROW) && stack.getName().getString().startsWith("Do not interact") && ((PlayerExtension) player).isInhabiting()) {
                InventoryFiller.fillInventoryWithPapers((ServerPlayerEntity) player);
                return EventResult.interruptFalse(); // Prevent dropping the item
            }
            return EventResult.pass();
        });
    }
}
