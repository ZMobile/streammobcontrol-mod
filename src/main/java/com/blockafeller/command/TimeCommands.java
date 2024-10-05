package com.blockafeller.command;

import com.blockafeller.time.PlayerTimeData;
import com.blockafeller.time.PlayerTimeDataManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;

public class TimeCommands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("addtime")
                .requires(source -> source.hasPermissionLevel(2))  // Requires OP permissions (level 2 or higher)
                .then(CommandManager.argument("player", EntityArgumentType.players())
                        .then(CommandManager.argument("mobTime", IntegerArgumentType.integer(0))
                                .then(CommandManager.argument("spectatorTime", IntegerArgumentType.integer(0))
                                        .executes(context -> {
                                            Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "player");
                                            int mobTime = IntegerArgumentType.getInteger(context, "mobTime");
                                            int spectatorTime = IntegerArgumentType.getInteger(context, "spectatorTime");

                                            for (ServerPlayerEntity player : players) {
                                                PlayerTimeData data = PlayerTimeDataManager.getPlayerTimeData(player.getUuid());
                                                if (data != null) {
                                                    data.setMobTime(data.getMobTime() + mobTime);
                                                    data.setSpectatorTime(data.getSpectatorTime() + spectatorTime);
                                                    data.setTotalMobTime(data.getTotalMobTime() + mobTime);
                                                    data.setTotalSpectatorTime(data.getTotalSpectatorTime() + spectatorTime);

                                                    player.sendMessage(Text.literal("Your time has been updated: " +
                                                            mobTime + " seconds of mob time and " +
                                                            spectatorTime + " seconds of spectator time added."), false);
                                                }
                                            }
                                            return 1;
                                        })))));
    }
}