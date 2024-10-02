package com.blockafeller.mixin;

import com.blockafeller.constraints.InventoryFiller;
import com.blockafeller.extension.PlayerExtension;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {

    @Shadow private ItemStack cursorStack;

    @Inject(method = "onSlotClick", at = @At("HEAD"), cancellable = true)
    private void preventSlotInteraction(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        System.out.println("Slot index: " + slotIndex + ", button: " + button + ", action type: " + actionType);
        System.out.println("Cursor stack: " + this.cursorStack);
        if (slotIndex == -999) {
            System.out.println("Cursor slot interaction detected");
        }
        //System.out.println("Stack in the slot: " + stackInTheSlot);
        if (player instanceof PlayerExtension playerExtension && playerExtension.isInhabiting()) {
            System.out.println("Player is inhabiting");

            if (slotIndex == 36
                    || slotIndex == 45
                    || slotIndex == 8
                    || slotIndex == 7
                    || slotIndex == 6
                    || slotIndex == 5
                    || slotIndex == -999) {
                if (slotIndex == 8
                        || slotIndex == 7
                        || slotIndex == 6
                        || slotIndex == 5) {
                    if (!(((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:zombie")
                            || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:evoker")
                            || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:drowned")
                            || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:zombie_villager")
                            || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:husk")
                            || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:skeleton")
                            || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:stray"))
                    ) {
                        ci.cancel();
                    }
                }
                if (slotIndex == 36) {
                    if (!(((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:zombie")
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
                            || ((PlayerExtension) player).getInhabitedMobType().toString().equals("minecraft:vindicator"))) {
                        ci.cancel();
                    }
                }
                System.out.println("Special slot interaction detected");
                if (slotIndex == -999) {
                    System.out.println("Cursor slot interaction detected");
                } else {
                    if (((cursorStack.getItem() == Items.PAPER
                            || cursorStack.getItem() == Items.ARROW)
                            && cursorStack.getName().getString().startsWith("Do not interact"))
                /*|| ((stackInTheSlot.getItem() == Items.PAPER
                    || stackInTheSlot.getItem() == Items.ARROW)
                    && stackInTheSlot.getName().getString().startsWith("Do not interact"))*/) {
                        System.out.println("Blocked interaction in special slot");
                        InventoryFiller.fillInventoryWithPapers((ServerPlayerEntity) player);
                        ci.cancel(); // Cancel the interaction for cursor stack
                    } else {
                        System.out.println("Allowed interaction in special slot");
                        System.out.println("Slot index: " + slotIndex);
                    }
                }
            } else {
                System.out.println("Blocked interaction in normal slot");
                ci.cancel(); // Cancel the interaction and prevent slot changes
            }
        }
    }
}
