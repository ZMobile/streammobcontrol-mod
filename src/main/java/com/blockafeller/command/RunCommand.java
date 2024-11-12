package com.blockafeller.command;

import com.blockafeller.extension.PlayerExtension;
import com.blockafeller.morph.MorphService;
import com.blockafeller.time.GracePeriodTimeTracker;
import com.blockafeller.time.TimeUtils;
import com.blockafeller.util.StreamerUtil;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

public class RunCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("run")
                .requires(source -> source.hasPermissionLevel(2))  // Requires OP permissions (level 2 or higher)
                .then(CommandManager.literal("start")  // Create the "start" sub-command
                        .executes(context -> {
                            // Logic for "/run start" goes here]
                            TimeUtils.setDayTime(context.getSource().getServer());
                            GracePeriodTimeTracker.resetGracePeriodTime();
                            var playerManager = context.getSource().getServer().getPlayerManager();

                            // Get the Overworld spawn location
                            ServerWorld overworld = context.getSource().getServer().getOverworld();
                            BlockPos spawnPos = overworld.getSpawnPos();

                            // Iterate through all players and apply changes to streamers
                            for (ServerPlayerEntity player : playerManager.getPlayerList()) {
                                if (((PlayerExtension) player).isInhabiting()) {
                                    MorphService.reverseMorph(player);

                                }
                                if (StreamerUtil.isStreamer(player)) {
                                    // Set the player's game mode to survival
                                    player.changeGameMode(GameMode.SURVIVAL);
                                    player.setHealth(20.0F);
                                    player.getHungerManager().setFoodLevel(20);

                                    // Teleport the player to the Overworld spawn position
                                    player.teleport(overworld, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), player.getYaw(), player.getPitch());
                                    // Send a feedback message to the player
                                    player.sendMessage(Text.literal("You have been teleported to the Overworld spawn and set to Survival mode!"), false);
                                }
                            }
                            context.getSource().sendFeedback(() -> Text.literal("Run start command executed!"), false);

                            // Placeholder return value
                            return 1;
                        })));
    }
}
