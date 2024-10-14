package com.blockafeller.morph;

import com.blockafeller.config.ConfigManager;
import com.blockafeller.extension.PlayerExtension;
import com.blockafeller.inventory.InventoryFiller;
import com.blockafeller.time.*;
import com.blockafeller.trait.hunger.HungerUtils;
import com.blockafeller.twitch.memory.*;
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
                    if (ConfigManager.getConfig().isMobTimeLimitEnabled()) {
                        if (timeData.getMobTime() <= 0) {
                            serverPlayer.sendMessage(Text.literal("You don't have enough mob time to morph!"), true);
                            return ActionResult.SUCCESS;
                        }
                    }
                    int minimumSubTierForMorphing = ConfigManager.getConfig().getMinimumSubTierForMorphing();
                    if (minimumSubTierForMorphing > 0) {
                        if (!PlayerAuthDataManager.getPlayerAuthDataMap().hasAuthData(player.getUuid())) {
                            serverPlayer.sendMessage(Text.literal("You need to authenticate with Twitch to morph!"), true);
                            return ActionResult.SUCCESS;
                        }
                        PlayerAuthData playerAuthData = PlayerAuthDataManager.getPlayerAuthDataMap().getAuthData(player.getUuid());
                        ViewerDonationData viewerDonationData = ViewerDonationDataManager.getViewerDonationDataMap().getViewerDonationData(playerAuthData.getTwitchUserId());
                        if (!viewerDonationData.isSubscribed() || viewerDonationData.getSubscriptionTier() < minimumSubTierForMorphing) {
                            serverPlayer.sendMessage(Text.literal("You need to be subscribed at tier " + minimumSubTierForMorphing + " or above to morph!"), true);
                            return ActionResult.SUCCESS;
                        }
                    }
                    if (entity instanceof MobEntity targetMob) {
                        if (GracePeriodTimeTracker.getGracePeriodTimeRemaining() > 0) {
                            player.sendMessage(Text.literal("You can't morph during the grace period! Grace period time remaining: " + GracePeriodTimeTracker.getGracePeriodTimeRemaining()), true);
                        } else {
                            // Get the type of the mob being right-clicked
                            // Morph into the mob type dynamically
                            if (ConfigManager.getConfig().isMobTimeLimitEnabled()) {
                                PlayerTimeData playerTimeData = PlayerTimeDataManager.getOrCreatePlayerTimeData(serverPlayer.getUuid(), serverPlayer.getServer());
                                playerTimeData.setTotalMobTime(playerTimeData.getMobTime());
                            }
                            MorphService.morphPlayerToMob(serverPlayer, targetMob);
                        }
                        return ActionResult.SUCCESS;
                    } else if (entity instanceof ServerPlayerEntity targetPlayer && ((PlayerExtension) targetPlayer).isInhabiting()) {
                        player.sendMessage(Text.literal("Can't morph into this mob. It is already inhabited by a player!"), true);
                        return ActionResult.SUCCESS;
                    }
                }
            }
            return ActionResult.PASS;
        });

        // Right-click in air with Reverse Morph Key to transform back
        UseItemCallback.EVENT.register((player, world, hand) -> {
            System.out.println("Right-clicked");
            ItemStack heldItem = player.getStackInHand(hand);
            if (player instanceof ServerPlayerEntity) {
                System.out.println("Right-clicked player selected slot: " +    player.getInventory().selectedSlot);
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
