package com.blockafeller.util;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashSet;
import java.util.Set;

public class StreamerUtil {
    /**
     * Checks if a player is in the "streamers" team.
     */
    public static boolean isStreamer(ServerPlayerEntity player) {
        Scoreboard scoreboard = player.getScoreboard();
        Team streamerTeam = scoreboard.getTeam("streamers");

        return streamerTeam != null && streamerTeam.getPlayerList().contains(player.getEntityName());
    }
}
