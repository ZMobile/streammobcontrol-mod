package com.blockafeller.morph;

import com.blockafeller.extension.PlayerExtension;
import com.blockafeller.inventory.InventoryFiller;
import com.blockafeller.time.*;
import com.blockafeller.trait.hunger.HungerUtils;
import draylar.identity.api.PlayerIdentity;
import draylar.identity.api.variant.IdentityType;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.GameMode;


public class MorphEventHandler {

    public static void register() {
        // Right-click a mob with Morph Key to transform
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            System.out.println("Right-clicked entity: " + entity);
            if (player instanceof ServerPlayerEntity serverPlayer) {
                if (serverPlayer.interactionManager.getGameMode() == GameMode.SPECTATOR) {
                    PlayerTimeData timeData = PlayerTimeDataManager.getOrCreatePlayerTimeData(serverPlayer.getUuid(), serverPlayer.getServer());
                    if (timeData.getMobTime() > 0) {
                        if (entity instanceof MobEntity targetMob) {
                            if (GracePeriodTimeTracker.getGracePeriodTimeRemaining() > 0) {
                                player.sendMessage(Text.literal("You can't morph during the grace period!"), true);
                            } else {
                                // Get the type of the mob being right-clicked
                                EntityType<?> mobType = targetMob.getType();
                                Identifier mobId = Registries.ENTITY_TYPE.getId(mobType);

                                // Morph into the mob type dynamically
                                PlayerTimeData playerTimeData = PlayerTimeDataManager.getOrCreatePlayerTimeData(serverPlayer.getUuid(), serverPlayer.getServer());
                                playerTimeData.setTotalMobTime(playerTimeData.getMobTime());
                                MorphService.morphPlayerToMob(serverPlayer, targetMob, mobId);
                            }
                            return ActionResult.SUCCESS;
                        } else if (entity instanceof ServerPlayerEntity targetPlayer && ((PlayerExtension) targetPlayer).isInhabiting()) {
                            player.sendMessage(Text.literal("Can't morph into this mob. It is already inhabited by a player!"), true);
                            return ActionResult.SUCCESS;
                        }
                    } else {
                        player.sendMessage(Text.literal("You don't have enough mob time to morph!"), true);
                        return ActionResult.SUCCESS;
                    }
                }
            }
            return ActionResult.PASS;
        });

        // Right-click in air with Reverse Morph Key to transform back
        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack heldItem = player.getStackInHand(hand);
            if (player instanceof ServerPlayerEntity) {
                if (MorphUtil.isReverseMorphKey(heldItem)) {
                    //  ServerBossBar bossBar = PlayerTimeBossBarTracker.getOrCreateBossBar((ServerPlayerEntity) player);
                    //PlayerTimeBossBarTracker.hideBossBar(bossBar, (ServerPlayerEntity) player);
                    MorphService.reverseMorph((ServerPlayerEntity) player);
                } else if (MorphUtil.isSpectateKey(heldItem)) {
                    MorphService.beginSpectating((ServerPlayerEntity) player);
                }
            }
            return TypedActionResult.pass(heldItem);
        });
    }
}
