package com.blockafeller.inventory;

import com.blockafeller.extension.PlayerExtension;
import com.blockafeller.morph.MorphUtil;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

public class DropPreventionHandler {
    public static void register() {
        PlayerEvent.DROP_ITEM.register((player, item) -> {
            ItemStack stack = item.getStack();
            if (MorphUtil.isDoNotInteractItem(stack) && ((PlayerExtension) player).isInhabiting()) {
                InventoryFiller.fillInventoryWithPapers((ServerPlayerEntity) player);
                return EventResult.interruptFalse(); // Prevent dropping the item
            }
            return EventResult.pass();
        });
    }
}
